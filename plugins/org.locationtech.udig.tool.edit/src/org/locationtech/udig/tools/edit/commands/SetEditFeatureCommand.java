/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.factory.GeoTools;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;

/**
 * Sets the feature currently being edited in the edit manager.
 *
 * @author chorner
 * @since 1.1.0
 */
public class SetEditFeatureCommand extends AbstractCommand implements UndoableMapCommand {

    // The size of the box that is searched. It uses the preferences settings value for the size of
    // the area
    private int SEARCH_SIZE = Platform.getPreferencesService().getInt(ProjectUIPlugin.ID,
            PreferenceConstants.FEATURE_SELECTION_SCALEFACTOR,
            PreferenceConstants.DEFAULT_FEATURE_SELECTION_SCALEFACTOR, null);

    EditToolHandler handler;
    Point clickPoint;
    PrimitiveShape shape;
    SimpleFeature oldFeature;
    ILayer oldLayer;
    private Layer editLayer;
    private Filter oldFilter;

    /**
     * @deprecated
     */
    public SetEditFeatureCommand (EditToolHandler handler, MapMouseEvent e, PrimitiveShape shape) {
        this.handler = handler;
        this.clickPoint = Point.valueOf(e.x, e.y);
        this.shape = shape;
    }
    public SetEditFeatureCommand (EditToolHandler handler, Point clickPoint, PrimitiveShape shape) {
        this.handler = handler;
        this.clickPoint = clickPoint;
        this.shape = shape;
    }
    
    public void run( IProgressMonitor monitor ) throws Exception {
        IToolContext context = handler.getContext();
        java.awt.Point point = new java.awt.Point(clickPoint.getX(), clickPoint.getY());
        ReferencedEnvelope bbox = handler.getContext().getBoundingBox(point,SEARCH_SIZE);
        FeatureCollection<SimpleFeatureType, SimpleFeature> fc = context.getFeaturesInBbox(handler.getEditLayer(), bbox);
        FeatureIterator<SimpleFeature> it = fc.features();
        SimpleFeature feature = null;
        while (it.hasNext()) {
            SimpleFeature feat = it.next();
            if (feat.getID().equals(shape.getEditGeom().getFeatureIDRef().toString())) {
                feature = feat;
                break;
            }
        }
        it.close();
        oldFeature = getMap().getEditManagerInternal().getEditFeature();
        oldLayer = getMap().getEditManagerInternal().getEditLayer();
        editLayer = (Layer) handler.getEditLayer();
        oldFilter = editLayer.getFilter();
        getMap().getEditManagerInternal().setEditFeature(feature, editLayer);
        editLayer.setFilter(fidFilter(feature));
        
    }

    private Filter fidFilter( SimpleFeature feature ) {
        FilterFactory factory = CommonFactoryFinder
        .getFilterFactory(GeoTools.getDefaultHints());
        Id id = factory.id(Collections.singleton(factory.featureId(feature.getID())));
        return id;
    }
    public String getName() {
        return "Set Current Selection";
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        getMap().getEditManagerInternal().setEditFeature(oldFeature, (Layer) oldLayer);
        editLayer.setFilter(oldFilter);
    }

}
