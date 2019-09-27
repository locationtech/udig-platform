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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.EditUtils;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Sets the selected feature to be the edit feature.
 * <ul>
 * <li>Sets the editblack board so it contains the default geometry of the feature.</li>  
 * <li>Sets the edit managers editFeature to be the feature.</li>
 * <li>Sets the EditToolHandler's currentGeom to be the newly added geom.</li>
 * @author jones
 * @since 1.1.0
 */
public class SelectFeatureAsEditFeatureCommand extends AbstractCommand implements Command, UndoableMapCommand {

    private ILayer selectedLayer;
    private SimpleFeature feature;
    private EditToolHandler handler;
    private UndoableMapCommand unselectcommand;
    private UndoableMapCommand command;
    private List<EditGeom> removed;
    private EditGeom currentGeom;
    private PrimitiveShape currentShape;
    private EditState currentState;
    private Point mouse;
    private ReferencedEnvelope refreshBounds;

    public SelectFeatureAsEditFeatureCommand( EditToolHandler handler, SimpleFeature feature, ILayer selectedLayer, Point mouse ) {
        if( mouse==null )
            throw new NullPointerException("mouse is null"); //$NON-NLS-1$
        this.handler=handler;
        this.feature=feature;
        this.selectedLayer=selectedLayer;
        this.mouse=mouse;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.SetGeomCommand_undoTask, 30);
        EditUtils.instance.cancelHideSelection(selectedLayer);
        IToolContext context = handler.getContext();

        this.refreshBounds=new ReferencedEnvelope(feature.getBounds());
        EditUtils.instance.refreshLayer(selectedLayer, feature, refreshBounds, false, true);

        unselectcommand = context.getSelectionFactory().createNoSelectCommand();
        command = context.getEditFactory().createSetEditFeatureCommand(feature,
                selectedLayer);
        
        unselectcommand.setMap(getMap());
        command.setMap(getMap());
        
        unselectcommand.run(new SubProgressMonitor(monitor, 10));
        command.run(new SubProgressMonitor(monitor, 10));
        
        this.removed=handler.getEditBlackboard(selectedLayer).getGeoms();
        this.currentGeom=handler.getCurrentGeom();
        this.currentShape=handler.getCurrentShape();
        this.currentState=handler.getCurrentState();
        
        handler.setCurrentState(EditState.MODIFYING);
        

        Collection<EditGeom> geoms = handler.getEditBlackboard(selectedLayer).setGeometries(
                (Geometry) feature.getDefaultGeometry(), feature.getID()).values();
        Class<?> type = selectedLayer.getSchema().getGeometryDescriptor().getType().getBinding();
        boolean polygonLayer=Polygon.class.isAssignableFrom(type) || MultiPolygon.class.isAssignableFrom(type);
        EditGeom over = EditUtils.instance.getGeomWithMouseOver(geoms, mouse, polygonLayer);
        handler.setCurrentShape(over.getShell());
        monitor.done();
    }

    public String getName() {
        return Messages.SetGeomCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.SetGeomCommand_runTask, 30);
        handler.setCurrentShape(currentShape);
        EditBlackboard bb = handler.getEditBlackboard(selectedLayer);
        handler.setCurrentState(currentState);
        EditGeom newCurrentGeom=null;
        List<EditGeom> empty = bb.getGeoms();
        
        for( EditGeom original : removed ) {
            EditGeom inBlackboard = bb.newGeom(original.getFeatureIDRef().get(), original.getShapeType());
            inBlackboard.setChanged(original.isChanged());
            if( original == currentGeom )
                newCurrentGeom=inBlackboard;
            
            PrimitiveShape destination = inBlackboard.getShell();
            newCurrentGeom = setCurrentGeom(newCurrentGeom, destination, original.getShell());
            
            for( Iterator<Coordinate> iter=original.getShell().coordIterator(); iter.hasNext(); ) {
                bb.addCoordinate(iter.next(), destination);
            }
            
            for( PrimitiveShape shape : original.getHoles() ) {
                destination=inBlackboard.newHole();
                newCurrentGeom = setCurrentGeom(newCurrentGeom, destination, shape);
                for( Iterator<Coordinate> iter=shape.coordIterator(); iter.hasNext(); ) {
                    bb.addCoordinate(iter.next(), destination);
                }
            }
        }

        bb.removeGeometries(empty);
        monitor.worked(10);
        refreshBounds.expandToInclude(new ReferencedEnvelope(feature.getBounds()));
        EditUtils.instance.refreshLayer(selectedLayer, feature, refreshBounds, true, false);

        command.rollback(new SubProgressMonitor(monitor, 10));
        unselectcommand.rollback(new SubProgressMonitor(monitor, 10));
    }
    

    private EditGeom setCurrentGeom( EditGeom newCurrentGeom, PrimitiveShape destination, PrimitiveShape shape ) {
        if( currentGeom!=null && newCurrentGeom!=null && shape==currentShape ){
            handler.setCurrentShape(destination);
        }
        return newCurrentGeom;
    }

}
