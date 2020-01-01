/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.collection.AdaptorFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableCommand;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.GeometryCreationUtil;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.ShapeType;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;

/**
 * Splits a line at the selected Vertices.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SplitLineCommand extends AbstractCommand implements MapCommand, UndoableCommand {

    private EditBlackboard editBlackboard;
    private IBlockingProvider<PrimitiveShape> shapeProvider;
    private Set<Point> points;
    private PrimitiveShape oldshape;
    private IBlockingProvider<SimpleFeature> featureProvider;
    private IBlockingProvider<ILayer> layerProvider;
    private SimpleFeature oldFeature;
    private ILayer layer;
    private List<FeatureId> newFids = new ArrayList<FeatureId>();
    private boolean currentShapeSet = false;
    private Geometry oldGeometry;
    private EditGeom first;

    /**
     * New instance
     * 
     * @param editBlackboard the blackboard that the feature is on.
     * @param provider the
     * @param feature
     * @param evaluationObject
     * @param points
     */
    public SplitLineCommand( EditBlackboard editBlackboard,
            IBlockingProvider<PrimitiveShape> provider, IBlockingProvider<SimpleFeature> featureProvider,
            IBlockingProvider<ILayer> layerProvider, Set<Point> points ) {
        this.editBlackboard = editBlackboard;
        this.shapeProvider = provider;
        this.layerProvider = layerProvider;
        this.featureProvider = featureProvider;
        this.points = points;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        editBlackboard.startBatchingEvents();
        
        oldshape = shapeProvider.get(new SubProgressMonitor(monitor, 1));
        oldFeature = featureProvider.get(new SubProgressMonitor(monitor, 1));
        oldGeometry = (Geometry) oldFeature.getDefaultGeometry();
        layer = layerProvider.get(new SubProgressMonitor(monitor, 1));

        editBlackboard.removeGeometries(Collections.singleton(oldshape.getEditGeom()));
        ShapeType shapeType = oldshape.getEditGeom().getShapeType();
        EditGeom current = editBlackboard.newGeom(oldshape.getEditGeom().getFeatureIDRef().get(), shapeType);
        first = current;

        final Set<EditGeom> addedGeoms=new HashSet<EditGeom>();
        for( int i = 0; i < oldshape.getNumPoints(); i++ ) {
            addCoords(current.getShell(), i);
            if (current.getShell().getNumPoints() > 1 && i < oldshape.getNumPoints() - 1
                    && points.contains(oldshape.getPoint(i))) {

                current = editBlackboard.newGeom("newFeature" + System.currentTimeMillis(), shapeType); //$NON-NLS-1$
                List<Coordinate> coords = oldshape.getCoordsAt(i);
                editBlackboard.addCoordinate(coords.get(coords.size() - 1), current.getShell());
                addedGeoms.add(current);
            }
        }
        
        editBlackboard.removeGeometries(addedGeoms);

        if (getCurrentShape() == oldshape) {
            currentShapeSet = true;
            setCurrentShape(first.getShell());
        }

        final FeatureStore<SimpleFeatureType, SimpleFeature> store = layer.getResource(FeatureStore.class, new SubProgressMonitor(
                monitor, 1));

        modifyOldFeature(store);

        createAndAddFeatures(addedGeoms, store);
        
        editBlackboard.fireBatchedEvents();
    }

    @SuppressWarnings({"unchecked"}) 
    private void modifyOldFeature( final FeatureStore<SimpleFeatureType, SimpleFeature> store ) throws IOException, IllegalAttributeException {
        FilterFactory fac = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Filter filter = fac.id(FeatureUtils.stringToId(fac, oldFeature.getID()));

        Geometry g = GeometryCreationUtil.createGeom(LineString.class, first.getShell(), true);
        if (store.getSchema().getGeometryDescriptor().getType().getBinding()
                .isAssignableFrom(MultiLineString.class))
            g = new GeometryFactory().createMultiLineString(new LineString[]{(LineString) g});

        store.modifyFeatures(store.getSchema().getGeometryDescriptor().getName(), g, filter);
        oldFeature.setDefaultGeometry(g);
    }

    /**
     *
     * @param addedGeoms
     * @param store
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void createAndAddFeatures( final Set<EditGeom> addedGeoms, final FeatureStore<SimpleFeatureType, SimpleFeature> store ) throws IOException {
        newFids = store.addFeatures(new AdaptorFeatureCollection("createAndAddCollection", store.getSchema()){

            @Override
            public int size() {
                return addedGeoms.size() - 1;
            }

            @Override
            protected Iterator openIterator() {
                final Iterator<EditGeom> iter = addedGeoms.iterator();
                return new Iterator(){
                    GeometryFactory factory = new GeometryFactory();
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @SuppressWarnings("unchecked")
                    public Object next() {
                        List<Object> attrs = oldFeature.getAttributes();
                        try {
                            SimpleFeature feature = SimpleFeatureBuilder.build(store.getSchema(), attrs, "copyOf"+oldFeature.getID());
                            Geometry geom = GeometryCreationUtil.createGeom(LineString.class, iter
                                    .next().getShell(), true);
                            if (getSchema().getGeometryDescriptor().getType()
                                    .getBinding().isAssignableFrom(
                                            MultiLineString.class))
                                geom = factory
                                        .createMultiLineString(new LineString[]{(LineString) geom});
                            feature.setDefaultGeometry(geom);
                            return feature;
                        } catch (IllegalAttributeException e) {
                            throw (RuntimeException) new RuntimeException().initCause(e);
                        }
                    }

                    public void remove() {
                    }

                };
            }

            @Override
            protected void closeIterator( Iterator close ) {
            }

        });
    }

    private void setCurrentShape( PrimitiveShape shape ) {
        getMap().getBlackboard().put(EditToolHandler.CURRENT_SHAPE, shape);
    }

    private PrimitiveShape getCurrentShape() {
        return (PrimitiveShape) getMap().getBlackboard().get(EditToolHandler.CURRENT_SHAPE);
    }

    private void addCoords( PrimitiveShape current, int i ) {
        List<Coordinate> coords = oldshape.getCoordsAt(i);
        for( Coordinate coordinate : coords ) {
            editBlackboard.addCoordinate(coordinate, current);
        }
    }

    public String getName() {
        return Messages.SplitLineCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        editBlackboard.removeGeometries(Collections.singleton(first));

        EditGeom newGeom = editBlackboard.newGeom(oldshape.getEditGeom().getFeatureIDRef().get(), oldshape.getEditGeom().getShapeType());

        for( int i = 0; i < oldshape.getNumCoords(); i++ ) {
            editBlackboard.addCoordinate(oldshape.getCoord(i), newGeom.getShell());
        }

        if (currentShapeSet)
            setCurrentShape(newGeom.getShell());

        FeatureStore<SimpleFeatureType, SimpleFeature> store = layer.getResource(FeatureStore.class, new SubProgressMonitor(monitor,
                1));

        FilterFactory factory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Set<FeatureId> ids = new HashSet<FeatureId>();
        for( FeatureId id : newFids ) {
        	ids.add(id);
        }
        Id filter = factory.id(ids);

        store.removeFeatures(filter);
        Geometry oldType = (Geometry) oldFeature.getDefaultGeometry();
		GeometryDescriptor newType = store.getSchema().getGeometryDescriptor();
		store.modifyFeatures(newType.getName(), oldType, factory.id(FeatureUtils.stringToId(factory, oldFeature.getID())));
        oldFeature.setDefaultGeometry(oldGeometry);
        newFids.clear();
    }

}
