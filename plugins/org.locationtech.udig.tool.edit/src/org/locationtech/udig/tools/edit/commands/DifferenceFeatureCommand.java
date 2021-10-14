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

import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.IllegalFilterException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.core.internal.GeometryBuilder;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.animation.GeometryOperationAnimation;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.IsBusyStateProvider;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.PrimitiveShapeIterator;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.BBOX;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;

/**
 * Splits a feature based on the current shape in the handler.
 * <p>
 * After the command the current shape will be set to null and
 * the edit blackboard will be cleared.
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

                FeatureCollection<SimpleFeatureType, SimpleFeature>  features = getFeatures(monitor);
                if( features == null ){                    
                    return; // did not hit anything
                }
                try {
                    List<Geometry> geoms = new ArrayList<Geometry>();
                    geoms.add(createReferenceGeom());
                    
                    SimpleFeature first = runDifferenceOp(features.features(), geoms);
                    
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
    private void createAddFeatureCommands( List<UndoableMapCommand> commands, List<Geometry> geoms, SimpleFeature first ) throws IllegalAttributeException {
        SimpleFeatureType featureType = first.getFeatureType();
        if ((geoms.size() > 1 && !featureType.getGeometryDescriptor().getType().getBinding()
                .isAssignableFrom(MultiPolygon.class))
                || !featureType.getGeometryDescriptor().getType().getBinding().isAssignableFrom(
                        MultiPolygon.class)) {
            for( Geometry geom : geoms ) {
                SimpleFeature newFeature = SimpleFeatureBuilder.copy(first);
                newFeature.setDefaultGeometry(geom);
                commands.add(handler.getContext().getEditFactory()
                        .createAddFeatureCommand(newFeature, layer));
            }
        } else {
            SimpleFeature newFeature = SimpleFeatureBuilder.copy(first);
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
     * @param iter
     * @param geoms the geometry to remove the features in iter from.  IE the geometries that will be diffed.  Is
     * also the list of resulting geometries.
     * @return
     */
    public static SimpleFeature runDifferenceOp( FeatureIterator<SimpleFeature> iter, List<Geometry> geoms ) {
    	Geometry createdGeometry = combineIntoOneGeometry( geoms );    	
    	Geometry differenceGeometry = createdGeometry;
    	SimpleFeature first=null;
    	try {

        	Set<Geometry> featureGeoms = new HashSet<Geometry>();
	        while( iter.hasNext() ) {
	            SimpleFeature f = iter.next();
	            
	            if (first == null){
	                first = f;
	            }
	            Geometry featureGeometry = (Geometry) f.getDefaultGeometry();
	            featureGeoms.add( featureGeometry );
	        }
	        Geometry existingGeometry = combineIntoOneGeometry( featureGeoms );
	        if( existingGeometry!=null ){
	            differenceGeometry = createdGeometry.difference( existingGeometry );
	        }
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

    /**
     * Grab some features from the layer using shape bounds.
     *
     * @param monitor
     * @return
     * @throws IOException
     * @throws NoninvertibleTransformException
     * @throws IllegalFilterException
     */
    private FeatureCollection<SimpleFeatureType, SimpleFeature>  getFeatures(IProgressMonitor monitor) throws IOException, NoninvertibleTransformException, IllegalFilterException {
        FeatureSource<SimpleFeatureType, SimpleFeature> source =layer.getResource(FeatureSource.class, new SubProgressMonitor(monitor,2));
        SimpleFeatureType schema = layer.getSchema();
        Rectangle bounds = shape.getBounds();
        double[] toTransform = new double[]{bounds.getMinX(), bounds.getMinY(),
                bounds.getMaxX(), bounds.getMaxY()};
        handler.getContext().worldToScreenTransform().inverseTransform(toTransform, 0,
                toTransform, 0, 2);
        ReferencedEnvelope transformedBounds = new ReferencedEnvelope(toTransform[0], toTransform[2],
                toTransform[1], toTransform[3], handler.getContext().getCRS());

        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
        ReferencedEnvelope layerBounds;
        try {
            MathTransform transform = layer.mapToLayerTransform();
            layerBounds = new ReferencedEnvelope(JTS.transform(transformedBounds, transform), layer.getCRS());
        } catch (Exception e) {
            layerBounds = transformedBounds;
        }
        String geomAttributeName = layer.getSchema().getGeometryDescriptor().getLocalName();
//        Geometry boundsAsGeom = new GeometryFactory().toGeometry(layerBounds);
//        
//        Intersects filter = filterFactory.intersects(filterFactory.literal(boundsAsGeom),  filterFactory.property(geomAttributeName));
        String srs;
        try {
            srs = CRS.lookupIdentifier(layerBounds.getCoordinateReferenceSystem(), false);
        } catch (FactoryException e) {
            // we cannot convert our request to the data CRS
            // so we cannot return any features
            ProjectPlugin.getPlugin().log(e);
            return null;
        }
        BBOX filter = filterFactory.bbox(geomAttributeName, layerBounds.getMinX(), 
        		layerBounds.getMinY(), layerBounds.getMaxX(), layerBounds.getMaxY(), srs);
        Query query=new Query(schema.getName().getLocalPart(), filter);

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
