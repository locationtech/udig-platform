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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.animation.AddVertexAnimation;
import org.locationtech.udig.tools.edit.animation.DeleteVertexAnimation;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.Selection;
import org.locationtech.udig.tools.edit.support.ShapeType;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.WaitCondition;

import org.eclipse.core.runtime.IProgressMonitor;

import org.locationtech.jts.geom.Coordinate;

/**
 * Removes the set of selected vertices from the blackboard.
 * 
 * @author jones
 * @since 1.1.0
 */
public class RemoveSelectedVerticesCommand extends AbstractCommand
        implements
            Command,
            UndoableMapCommand {

    private EditToolHandler handler;
    private List<Bag> undoData;
    private boolean runAnimation;

    public RemoveSelectedVerticesCommand( EditToolHandler handler ) {
        this.handler = handler;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        EditBlackboard blackboard = handler.getEditBlackboard(handler.getEditLayer());
        blackboard.startBatchingEvents();
        Selection selection = blackboard.getSelection();
        undoData = new ArrayList<Bag>();
        EditState oldState = handler.getCurrentState();
        try {
            handler.setCurrentState(EditState.BUSY);
            DeleteVertexAnimation deleteVertexAnimation=null;
            if( runAnimation ){
                for( Point point : selection ) {
                    deleteVertexAnimation = new DeleteVertexAnimation(point);
                    AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), deleteVertexAnimation);
                }
                if( deleteVertexAnimation!=null ){
                    final DeleteVertexAnimation finalDeleteVertexAnim=deleteVertexAnimation;
                    PlatformGIS.wait(deleteVertexAnimation.getFrameInterval(), 5000, new WaitCondition(){
    
                        public boolean isTrue()  {
                            return !finalDeleteVertexAnim.isValid();
                        }
                        
                    }, null);
                }
            }
            HashSet<Point> points = new HashSet<Point>(selection);
            HashSet<EditGeom> allAffectedGeoms = new HashSet<EditGeom>();
            for( Point point : points ) {
                allAffectedGeoms.addAll(blackboard.getGeoms(point.getX(), point.getY()));
            }
            
            Map<PrimitiveShape, Integer> deletes = new HashMap<PrimitiveShape, Integer>();
            
            for( EditGeom geom : allAffectedGeoms ) {
                for( PrimitiveShape shape : geom ) {
                    for( int i = 0; i < shape.getNumPoints(); i++ ) {
                        Point shapePoint = shape.getPoint(i);
                        if (points.contains(shapePoint)) {
                            Bag bag = new Bag();
                            bag.p = shapePoint;
                            bag.coords = shape.getCoordsAt(i);
                            bag.shape = shape;
                            bag.index = i - get(deletes, shape);
                            bag.action = Action.REMOVE;
                            increment( deletes, shape);
                            undoData.add(bag);
                        }
                    }
                }
            }
            for( Point point : points ) {
                blackboard.removeCoordsAtPoint(point.getX(), point.getY());
            }
            for( PrimitiveShape shape : deletes.keySet() ) {
                if (shape.getNumPoints()>0 && shape.getEditGeom().getShapeType() == ShapeType.POLYGON) {
                    if( !shape.getPoint(0).equals(shape.getPoint(shape.getNumPoints()-1)) ){
                        List<Coordinate> singletonList = Collections.singletonList(shape
                                .getCoord(0));
                        blackboard.addCoordinate(shape.getCoord(0), shape);
                        Bag bag = new Bag();
                        bag.p = shape.getPoint(shape.getNumPoints()-1);
                        bag.coords = singletonList;
                        bag.shape = shape;
                        bag.index = -1;
                        bag.action = Action.ADD;
                        undoData.add(bag);
                    }
                }

            }
        } finally {
            handler.setCurrentState(oldState);
            blackboard.fireBatchedEvents();
        }
    }

    private void increment( Map<PrimitiveShape, Integer> deletes, PrimitiveShape shape ) {
        if( !deletes.containsKey(shape) ){
            deletes.put(shape, 1);
        }else{
            int val=deletes.get(shape)+1;
            deletes.put(shape, val);
        }
    }

    private static int get( Map<PrimitiveShape, Integer> deletes, PrimitiveShape shape ) {
        if( deletes.containsKey(shape) ){
            return deletes.get(shape);
        }
        return 0;
    }

    public String getName() {
        return Messages.RemoveSelectedVerticesCommand_name;
    }
    
    public void rollback( IProgressMonitor monitor ) throws Exception {
        EditBlackboard blackboard = handler.getEditBlackboard(handler.getEditLayer());
        blackboard.startBatchingEvents();
        
        try {
            for( int i = undoData.size() - 1; i > -1; i-- ) {
                Bag bag = undoData.get(i);
                switch( bag.action ) {
                case ADD:
                    blackboard.removeCoordinate( bag.shape.getNumPoints()-1, bag.coords.get(0), bag.shape );
                    break;
                case REMOVE:
                    if( runAnimation )
                        AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), new AddVertexAnimation(bag.p.getX(), bag.p.getY()));
                    blackboard.insertCoords(bag.index, bag.p, bag.coords, bag.shape);
                    blackboard.selectionAdd(bag.p);
                    break;

                default:
                    break;
                }
            }
        } finally {
            blackboard.fireBatchedEvents();
        }
    }

    enum Action{
        ADD, REMOVE
    }
    
    static class Bag {
        Action action;
        Point p;
        int index;
        List<Coordinate> coords;
        PrimitiveShape shape;
        
        @Override
        public String toString() {
            return action+" "+coords; //$NON-NLS-1$
        }
    }

    /**
     * If run is true then the animations will be run otherwise not.
     *
     * @param run whether animations should be ran
     */
    public void setRunAnimation( boolean run ) {
        this.runAnimation=run;
    }
    
}
