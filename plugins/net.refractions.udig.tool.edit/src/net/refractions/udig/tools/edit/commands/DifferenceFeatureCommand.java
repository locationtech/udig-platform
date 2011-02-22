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

import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.core.internal.GeometryBuilder;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.animation.GeometryOperationAnimation;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.IsBusyStateProvider;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.PrimitiveShapeIterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.filter.BBoxExpression;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.FilterType;
import org.geotools.filter.GeometryFilter;
import org.geotools.filter.IllegalFilterException;
import org.geotools.geometry.jts.JTS;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Splits a feature based on the current shape in the handler. After the command the current shape
 * will be set to null and the edit blackboard will be cleared.
 *
 * @author jones
 * @since 1.1.0
 */
public class DifferenceFeatureCommand extends AbstractCommand implements UndoableMapCommand {

    private EditToolHandler     handler;
    private PrimitiveShape      shape;
    private EditState           state;
    private ILayer              layer;
    private ArrayList<EditGeom> geoms;
    private EditState           endState;
    private UndoableComposite   writeCommand;
    private boolean             addedEndVertex = false;

    /**
     * @param handler
     */
    public DifferenceFeatureCommand( EditToolHandler handler, EditState endState ) {
        this.handler = handler;
        this.layer = handler.getEditLayer();
        this.endState = endState;
    }

    @SuppressWarnings("unchecked")
    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.DifferenceFeatureCommand_runTaskMessage, 10);
        monitor.worked(1);
        this.state = handler.getCurrentState();
        this.shape = handler.getCurrentShape();
        handler.setCurrentShape(null);
        List<UndoableMapCommand> commands = new ArrayList<UndoableMapCommand>();

        //check that start point is same as end point
        Point startPoint = shape.getPoint(0);
        if (!startPoint.equals(shape.getPoint(shape.getNumPoints() - 1))) {
            addedEndVertex = true;
            shape.getEditBlackboard().addPoint(startPoint.getX(), startPoint.getY(), shape);
        }
        GeometryOperationAnimation indicator = new GeometryOperationAnimation(
                PrimitiveShapeIterator.getPathIterator(shape).toShape(), new IsBusyStateProvider(
                        handler));
        try {
            AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), indicator);
            handler.setCurrentState(EditState.BUSY);

            if (writeCommand == null) {
                EditBlackboard editBlackboard = handler.getEditBlackboard(layer);
                this.geoms = new ArrayList<EditGeom>(editBlackboard.getGeoms());
                geoms.remove(shape.getEditGeom());

                editBlackboard.clear();

                FeatureCollection features = getFeatures(monitor);

                try {
                    List<Geometry> geoms = new ArrayList<Geometry>();
                    geoms.add(createReferenceGeom());

                    Feature first = runDifferenceOp(features.features(), geoms);

                    if (first == null)
                        return;

                    createAddFeatureCommands(commands, geoms, first);
                } finally {
                    monitor.worked(2);
                }
                this.writeCommand = new UndoableComposite(commands);
            }
            writeCommand.setMap(getMap());
            handler.setCurrentState(EditState.COMMITTING);
            writeCommand.execute(new SubProgressMonitor(monitor, 5));
        } finally {
            indicator.setValid(false);
            handler.setCurrentState(endState);
            monitor.done();
        }
    }

    @SuppressWarnings("unchecked")
    private void createAddFeatureCommands( List<UndoableMapCommand> commands, List<Geometry> geoms, Feature first ) throws IllegalAttributeException {
        FeatureType featureType = first.getFeatureType();
        if ((geoms.size() > 1 && !featureType.getDefaultGeometry().getType()
                .isAssignableFrom(MultiPolygon.class))
                || !featureType.getDefaultGeometry().getType().isAssignableFrom(
                        MultiPolygon.class)) {
            for( Geometry geom : geoms ) {
                Feature newFeature = featureType.duplicate(first);
                newFeature.setDefaultGeometry(geom);
                commands.add(handler.getContext().getEditFactory()
                        .createAddFeatureCommand(newFeature, layer));
            }
        } else {
            Feature newFeature = featureType.duplicate(first);
            GeometryFactory factory = new GeometryFactory();

            newFeature.setDefaultGeometry(factory.createMultiPolygon(geoms
                    .toArray(new Polygon[geoms.size()])));
            commands.add(handler.getContext().getEditFactory().createAddFeatureCommand(
                    newFeature, layer));
        }
    }

    /**
     * Collect the provided geometries into a single geometry.
     *
     * @param geometryCollection
     * @return A single geometry which is the union of the provided geometryCollection
     */
    static Geometry combineIntoOneGeometry( Collection<Geometry> geometryCollection ){
        //GeometryFactory factory = FactoryFinder.getGeometryFactory( null );
        GeometryFactory factory = new GeometryFactory();

    	Geometry combined = factory.buildGeometry( geometryCollection );
        return combined.union();
    }

    /**
     * This is public only for testing it is NOT API
     * @param iter
     * @param geoms the geometry to remove the features in iter from.  IE the geometries that will be diffed.  Is
     * also the list of resulting geometries.
     * @return
     */
    public static Feature runDifferenceOp( FeatureIterator iter, List<Geometry> geoms ) {

    	Geometry createdGeometry = combineIntoOneGeometry( geoms );
    	Geometry differenceGeometry = createdGeometry;
    	Feature first=null;
    	try {

        	Set<Geometry> featureGeoms = new HashSet<Geometry>();
        	while( iter.hasNext() ) {
	            Feature f = iter.next();

	            if (first == null){
	                first = f;
	            }
	            Geometry featureGeometry = (Geometry) f.getDefaultGeometry();
	            featureGeoms.add( featureGeometry );
	        }
	        Geometry existingGeometry = combineIntoOneGeometry( featureGeoms );
	        differenceGeometry = createdGeometry.difference( existingGeometry );
        } finally{
            if( iter!=null )
                iter.close();
        }
        geoms.clear();
        for( int i=0; i<differenceGeometry.getNumGeometries(); i++){
        	geoms.add( differenceGeometry.getGeometryN(i) );
        }
        return first;
    }

    private FeatureCollection getFeatures(IProgressMonitor monitor) throws IOException, NoninvertibleTransformException, IllegalFilterException {
        FeatureSource source=layer.getResource(FeatureSource.class, new SubProgressMonitor(monitor,2));
        FeatureType schema = layer.getSchema();
        Rectangle bounds = shape.getBounds();
        double[] toTransform = new double[]{bounds.getMinX(), bounds.getMinY(),
                bounds.getMaxX(), bounds.getMaxY()};
        handler.getContext().worldToScreenTransform().inverseTransform(toTransform, 0,
                toTransform, 0, 2);
        Envelope transformedBounds = new Envelope(toTransform[0], toTransform[2],
                toTransform[1], toTransform[3]);

        FilterFactory filterFactory = FilterFactoryFinder.createFilterFactory();
        Envelope layerBounds;
        try {
            MathTransform transform = layer.mapToLayerTransform();
            layerBounds = JTS.transform(transformedBounds, transform);
        } catch (Exception e) {
            layerBounds = transformedBounds;
        }
        BBoxExpression bb = filterFactory.createBBoxExpression(layerBounds);
        GeometryFilter filter = filterFactory
                .createGeometryFilter(FilterType.GEOMETRY_BBOX);
        filter.addRightGeometry(bb);

        String geomAttributeName = layer.getSchema().getDefaultGeometry().getName();

        filter.addLeftGeometry(filterFactory.createAttributeExpression(geomAttributeName));
        Query query=new DefaultQuery(schema.getTypeName(), filter);

        return source.getFeatures(query);
    }

    /**
     * @return
     */
    private Geometry createReferenceGeom() {
        LinearRing ring = GeometryBuilder.create().safeCreateGeometry(LinearRing.class,
                shape.coordArray());
        GeometryFactory fac = new GeometryFactory();
        return fac.createPolygon(ring, new LinearRing[0]);

    }
    public void rollback( IProgressMonitor monitor ) throws Exception {
        GeometryOperationAnimation indicator = new GeometryOperationAnimation(
                PrimitiveShapeIterator.getPathIterator(shape).toShape(), new IsBusyStateProvider(
                        handler));
        try {
            monitor.beginTask(Messages.DifferenceFeatureCommand_undoTaskMessage, 10);
            monitor.worked(1);

            AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), indicator);

            handler.setCurrentState(EditState.BUSY);

            SubProgressMonitor submonitor = new SubProgressMonitor(monitor, 5);
            writeCommand.rollback(submonitor);
            submonitor.done();

            EditBlackboard bb = handler.getEditBlackboard(layer);
            bb.clear();
            for( EditGeom geom : geoms ) {
                addGeom(bb, geom);
            }
            PrimitiveShape shell = addGeom(bb, shape.getEditGeom()).getShell();
            handler.setCurrentShape(shell);
            if (addedEndVertex) {
                bb.removeCoordinate(shape.getNumCoords() - 1, shape
                        .getCoord(shape.getNumCoords() - 1), shell);
            }
            handler.setCurrentState(state);
        } catch (Exception e) {
            handler.setCurrentState(EditState.NONE);
            throw e;
        } finally {
            indicator.setValid(false);
            monitor.done();
        }
    }

    /**
     * @param bb
     * @param geom
     * @return
     */
    private EditGeom addGeom( EditBlackboard bb, EditGeom geom ) {
        EditGeom newGeom = bb.newGeom(geom.getFeatureIDRef().get(), geom.getShapeType());
        newGeom.setChanged(geom.isChanged());
        for( PrimitiveShape shape : geom ) {
            PrimitiveShape newShape = newGeom.getShell();
            if (shape != geom.getShell())
                newShape = newGeom.newHole();
            Coordinate[] coords = shape.coordArray();
            for( int i = 0; i < coords.length; i++ ) {
                bb.addCoordinate(coords[i], newShape);
            }
        }
        return newGeom;
    }

    public String getName() {
        return Messages.DifferenceFeatureCommand_name;
    }
}
