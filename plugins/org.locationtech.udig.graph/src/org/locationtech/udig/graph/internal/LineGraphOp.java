/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.graph.internal;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.graph.build.feature.FeatureGraphGenerator;
import org.geotools.graph.build.line.LineStringGraphGenerator;
import org.geotools.graph.structure.Graph;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.util.GeoToolsAdapters;
import org.locationtech.udig.core.internal.CorePlugin;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.SetLayerVisibilityCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.operations.IOp;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;

public class LineGraphOp implements IOp {

    @Override
    public void op(Display display, Object target, IProgressMonitor monitor) throws Exception {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        monitor.beginTask("build graph", 100); //$NON-NLS-1$
        Layer layer = (Layer) target;
        Filter filter = layer.getFilter();
        if (filter == Filter.EXCLUDE) {
            // nothing selected? let us work with everything then...
            filter = Filter.INCLUDE;
        }
        monitor.subTask("grab features for " + filter); //$NON-NLS-1$

        FeatureSource<SimpleFeatureType, SimpleFeature> source = layer
                .getResource(FeatureSource.class, SubMonitor.convert(monitor, 10));

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = source.getFeatures(filter);

        FeatureType schema = features.getSchema();
        Class<?> binding = schema.getGeometryDescriptor().getType().getBinding();
        Graph graph = null;
        if (MultiLineString.class.isAssignableFrom(binding)) {
            graph = buildFromMultiLineString(features, SubMonitor.convert(monitor, 90));
        }
        if (graph == null) {
            // prompt or otherwise any user?
            System.out.println("Could not create a graph from the current selection"); //$NON-NLS-1$
            return;
        }
        IBlackboard blackboard = layer.getMap().getBlackboard();
        blackboard.put("graph", graph); //$NON-NLS-1$

        try {
            makeGraphVisible(layer.getMap());
        } catch (IOException notAvailable) {
        }
    }

    private void makeGraphVisible(IMap map) throws IOException {
        ILayer graphLayer = null;
        ILayer pathLayer = null;

        ID graphId = new ID(new URL(null, GraphMapGraphic.ID, CorePlugin.RELAXED_HANDLER));
        ID pathId = new ID(new URL(null, PathMapGraphic.ID, CorePlugin.RELAXED_HANDLER));

        for (ILayer look : map.getMapLayers()) {
            URL id = look.getGeoResource().getIdentifier();
            if (URLUtils.urlEquals(id, graphId.toURL(), false)) {
                graphLayer = look;
                break;
            }
        }
        if (graphLayer == null) {
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            IGeoResource resource = catalog.getById(IGeoResource.class, graphId,
                    new NullProgressMonitor());
            if (resource == null) {
                return; // not available?
            }
            List<IGeoResource> resourceList = Collections.singletonList(resource);
            List<? extends ILayer> added = ApplicationGIS.addLayersToMap(map, resourceList, 0);
            if (added.isEmpty()) {
                return; // not available?
            }
            graphLayer = added.get(0);
        }
        if (!graphLayer.isVisible()) {
            map.sendCommandASync(new SetLayerVisibilityCommand(graphLayer, true));
        }

        for (ILayer look : map.getMapLayers()) {
            URL id = look.getGeoResource().getIdentifier();
            if (URLUtils.urlEquals(id, pathId.toURL(), false)) {
                pathLayer = look;
                break;
            }
        }
        if (pathLayer == null) {
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            IGeoResource resource = catalog.getById(IGeoResource.class, pathId,
                    new NullProgressMonitor());
            if (resource == null) {
                return; // not available?
            }
            List<IGeoResource> resourceList = Collections.singletonList(resource);
            List<? extends ILayer> added = ApplicationGIS.addLayersToMap(map, resourceList, 0);
            if (added.isEmpty()) {
                return; // not available?
            }
            pathLayer = added.get(0);
        }
        if (!pathLayer.isVisible()) {
            map.sendCommandASync(new SetLayerVisibilityCommand(pathLayer, true));
        }
        pathLayer.refresh(null);
    }

    private Graph buildFromMultiLineString(
            FeatureCollection<SimpleFeatureType, SimpleFeature> features,
            IProgressMonitor monitor) {
        // create a linear graph generate
        LineStringGraphGenerator lineStringGen = new LineStringGraphGenerator();

        // wrap it in a feature graph generator
        final FeatureGraphGenerator featureGen = new FeatureGraphGenerator(lineStringGen);

        // throw all the features into the graph generator
        try {
            features.accepts(new FeatureVisitor() {
                @Override
                public void visit(Feature feature) {
                    featureGen.add(feature);
                }
            }, GeoToolsAdapters.progress(monitor));
            return featureGen.getGraph();
        } catch (IOException e) {
            return null;
        }
    }

}
