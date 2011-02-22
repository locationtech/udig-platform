/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.commands;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableCommand;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.GeometryCreationUtil;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.feature.Feature;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.collection.AbstractFeatureCollection;
import org.geotools.filter.FidFilter;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

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
    private IBlockingProvider<Feature> featureProvider;
    private IBlockingProvider<ILayer> layerProvider;
    private Feature oldFeature;
    private ILayer layer;
    private Set<String> newFids = new HashSet<String>();
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
            IBlockingProvider<PrimitiveShape> provider, IBlockingProvider<Feature> featureProvider,
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
        oldGeometry = oldFeature.getDefaultGeometry();
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

        final FeatureStore store = layer.getResource(FeatureStore.class, new SubProgressMonitor(
                monitor, 1));

        modifyOldFeature(store);

        createAndAddFeatures(addedGeoms, store);

        editBlackboard.fireBatchedEvents();
    }

    @SuppressWarnings({"unchecked"})
    private void modifyOldFeature( final FeatureStore store ) throws IOException, IllegalAttributeException {
        FilterFactory fac = FilterFactoryFinder.createFilterFactory();
        Filter filter = fac.createFidFilter(oldFeature.getID());

        Geometry g = GeometryCreationUtil.createGeom(LineString.class, first.getShell(), true);
        if (store.getSchema().getDefaultGeometry().getType()
                .isAssignableFrom(MultiLineString.class))
            g = new GeometryFactory().createMultiLineString(new LineString[]{(LineString) g});

        store.modifyFeatures(store.getSchema().getDefaultGeometry(), g, filter);
        oldFeature.setDefaultGeometry(g);
    }

    /**
     *
     * @param addedGeoms
     * @param store
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void createAndAddFeatures( final Set<EditGeom> addedGeoms, final FeatureStore store ) throws IOException {
        newFids = store.addFeatures(new AbstractFeatureCollection(store.getSchema()){

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
                        Object[] attrs = oldFeature.getAttributes(new Object[store.getSchema()
                                .getAttributeCount()]);
                        try {
                            Feature feature = store.getSchema().create(attrs);
                            Geometry geom = GeometryCreationUtil.createGeom(LineString.class, iter
                                    .next().getShell(), true);
                            if (getSchema().getDefaultGeometry().getType().isAssignableFrom(
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

        FeatureStore store = layer.getResource(FeatureStore.class, new SubProgressMonitor(monitor,
                1));
        FilterFactory filterFac = FilterFactoryFinder.createFilterFactory();
        FidFilter filter = filterFac.createFidFilter();
        filter.addAllFids(newFids);
        store.removeFeatures(filter);
        store.modifyFeatures(store.getSchema().getDefaultGeometry(), oldFeature
                .getDefaultGeometry(), filterFac.createFidFilter(oldFeature.getID()));
        oldFeature.setDefaultGeometry(oldGeometry);
        newFids.clear();
    }

}
