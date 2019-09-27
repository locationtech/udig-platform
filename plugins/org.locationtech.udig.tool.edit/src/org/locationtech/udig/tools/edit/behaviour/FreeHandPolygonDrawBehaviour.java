/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.behaviour;

import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoRedoCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.DrawPathCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.LockingBehaviour;
import org.locationtech.udig.tools.edit.commands.AddVertexCommand;
import org.locationtech.udig.tools.edit.commands.CreateAndSelectHoleCommand;
import org.locationtech.udig.tools.edit.commands.CreateEditGeomCommand;
import org.locationtech.udig.tools.edit.commands.SetCurrentGeomCommand;
import org.locationtech.udig.tools.edit.commands.SetEditStateCommand;
import org.locationtech.udig.tools.edit.preferences.PreferenceConstants;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditUtils;
import org.locationtech.udig.tools.edit.support.PathAdapter;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.ShapeType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.simple.SimpleFeatureType;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * <p>
 * Requirements:
 * <ul>
 * <li>EventType==DRAGGED</li>
 * <li>button1 is down</li>
 * </ul>
 * </p>
 * <p>
 * Action:
 * <ul>
 * <li>if currentshape is null then a new polygon is created by free hand drawing.</li>
 * <li>if currentshape is not null then a hole is created by free hand drawing.</li>
 * <li>handler is locked until mouse is released</li>
 * </ul>
 * </p>
 * 
 * @author jones
 * @since 1.1.0
 */
public class FreeHandPolygonDrawBehaviour implements LockingBehaviour {

    private Creator creator;
    private DrawPathCommand drawShapeCommand;
    private ShapeType type = ShapeType.POLYGON;
    private PathAdapter generalPath;
    private DrawPathCommand drawEndPoints;

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean draggedEvent = eventType == EventType.DRAGGED;
        boolean legalButtons = e.buttons == MapMouseEvent.BUTTON1;
        return draggedEvent && legalButtons;

    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
        UndoableMapCommand command = null;
        if (creator == null || generalPath==null || drawEndPoints==null || drawShapeCommand==null )
            command = init(handler, e, eventType);
        generalPath.lineTo(e.x, e.y);
        
        updateShapes(handler, e);
        
        SetEditStateCommand setEditStateCommand = new SetEditStateCommand(handler, EditState.CREATING);
        
        return executeCommand(handler, command, setEditStateCommand);
    }

    private UndoableMapCommand executeCommand( EditToolHandler handler, UndoableMapCommand command2, SetEditStateCommand setEditStateCommand ) {
        UndoableMapCommand command=command2;
        if (command != null) {
            List<UndoableMapCommand> commands = new ArrayList<UndoableMapCommand>();
            commands.add(command);
            commands.add(setEditStateCommand);
            UndoableComposite undoableComposite = new UndoableComposite(commands);
            command=undoableComposite;
        } else {
            command=setEditStateCommand;
        }
        command.setMap(handler.getContext().getMap());
        try {
            command.run(new NullProgressMonitor());
        } catch (Exception e1) {
            throw (RuntimeException) new RuntimeException( e1 );
        }
        return new UndoRedoCommand(command);
    }

    private void updateShapes( EditToolHandler handler, MapMouseEvent e ) {
        Point dragStarted = handler.getMouseTracker().getDragStarted();
        if( isOverEndPoint(e, PreferenceUtil.instance().getVertexRadius(), dragStarted)){
            drawEndPoints.setFill(PreferenceUtil.instance().getDrawVertexFillColor());
        }else{
            drawEndPoints.setFill(null);            
        }
        
        if (type == ShapeType.POLYGON)
            drawShapeCommand.line(e.x, e.y, dragStarted.getX(), dragStarted.getY());

        handler.repaint();
    }

    private UndoableMapCommand init( final EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        if( !handler.isLockOwner(this) )
            handler.lock(this);
        Point dragStarted = handler.getMouseTracker().getDragStarted();
        if( creator!=null )
            handler.getBehaviours().remove(creator);
        creator = new Creator(dragStarted);
        initDrawCommands(handler, dragStarted);
        handler.getBehaviours().add(creator);

        ILayer selectedLayer = handler.getEditLayer();
        EditBlackboard editBlackboard = handler.getEditBlackboard(selectedLayer);
        int vertexRadius = PreferenceUtil.instance().getVertexRadius();
        Point nearestPoint = editBlackboard.overVertex(dragStarted, vertexRadius);

        if( nearestPoint==null )
            nearestPoint=dragStarted;
        
        initShapePath(nearestPoint);

        SimpleFeatureType schema = selectedLayer.getSchema();
        determineShapeType(schema);
        
        if( type==ShapeType.POLYGON && EditPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_FILL_POLYGONS) ){
            drawShapeCommand.setFill(PreferenceUtil.instance().getDrawGeomsFill());
        }
        
        EditBlackboard blackboard = editBlackboard;
        PrimitiveShape currentShape = handler.getCurrentShape();
        if (currentShape == null) {
            UndoableComposite undoableComposite = startNewShape(handler, blackboard);
            return undoableComposite;
        }

        currentShape=currentShape.getEditGeom().getShell();
        
        //if current shape is a line and point matches up with one of the end points then
        // continue line.
        if( currentShape.getEditGeom().getShapeType()==ShapeType.LINE ){
            if( currentShape.getNumPoints()>0 && nearestPoint.equals(currentShape.getPoint(0)) ){
                // over the first end point so reverse order and return;
                EditUtils.instance.reverseOrder(currentShape);
                return null;
            }
            
            if( currentShape.getNumPoints()>1 && !nearestPoint.equals(currentShape.getPoint(currentShape.getNumPoints()-1)) ){
                // not over one of the end points
                return createNewGeom(handler);
            }
            return null;
        }
        
        
        if (currentShape.getEditGeom().getShapeType()==ShapeType.POINT) {
            // it's a point create a new point. 
            return createNewGeom(handler);
        }else{
            if (currentShape.contains(nearestPoint, true)) {
                if (handler.getCurrentGeom().getShell() == currentShape) {
                    for( PrimitiveShape hole : handler.getCurrentGeom().getHoles() ) {
                        if (hole.contains(nearestPoint, true)) {
                            return createNewGeom(handler);    
                        }
                    }
                    type=ShapeType.POLYGON;
                    handler.getContext().sendSyncCommand(new CreateAndSelectHoleCommand(handler));
                }else{
                    return createNewGeom(handler);
                }
            } else {
                return createNewGeom(handler);
            }
        }
        return null;
    }

    private UndoableComposite startNewShape( final EditToolHandler handler, EditBlackboard blackboard ) {
        List<UndoableMapCommand> commands = new ArrayList<UndoableMapCommand>();
        final CreateEditGeomCommand createEditGeomCommand = new CreateEditGeomCommand(
                blackboard, "freeHandDraw", ShapeType.LINE); //$NON-NLS-1$
        commands.add(createEditGeomCommand);
        class PrimitiveProvider implements IBlockingProvider<PrimitiveShape> {

            public PrimitiveShape get(IProgressMonitor monitor, Object... params) {
                return createEditGeomCommand.get(monitor).getShell();
            }

        }

        commands.add(new SetCurrentGeomCommand(handler, new PrimitiveProvider())); 
        
        if( handler.getContext().getEditManager().getEditFeature()!=null ){
            commands.add(handler.getContext().getEditFactory().createNullEditFeatureCommand());
        }
        UndoableComposite undoableComposite = new UndoableComposite(commands);
        return undoableComposite;
    }

    private void determineShapeType( SimpleFeatureType schema ) {
        if (Polygon.class.isAssignableFrom(schema.getGeometryDescriptor().getType().getBinding())
                || MultiPolygon.class.isAssignableFrom(schema.getGeometryDescriptor().getType().getBinding()))
            type = ShapeType.POLYGON;
        else if (LineString.class.isAssignableFrom(schema.getGeometryDescriptor().getType().getBinding())
                || LinearRing.class.isAssignableFrom(schema.getGeometryDescriptor().getType().getBinding())
                || MultiLineString.class.isAssignableFrom(schema.getGeometryDescriptor().getType().getBinding()))
            type = ShapeType.LINE;
        else {
            type = ShapeType.UNKNOWN;
        }
    }

    private void initShapePath( Point nearestPoint ) {
        generalPath = new PathAdapter();
        if( Platform.getOS().equals(Platform.OS_LINUX) ){
            generalPath.setPath(new GeneralPath());
        }else{
            generalPath.setPath(new Path(getDisplay()) );
        }
        generalPath.moveTo(nearestPoint.getX(), nearestPoint.getY());
        if (generalPath.isPath())
            drawShapeCommand.setPath(generalPath.getPath());
        else
            drawShapeCommand.setPath(Display.getCurrent(), generalPath.getPathIterator());
    }

    private void initDrawCommands( final EditToolHandler handler, Point dragStarted ) {
        if (drawShapeCommand == null) {
            drawShapeCommand = new DrawPathCommand();
            drawShapeCommand.setPaint(PreferenceUtil.instance().getDrawGeomsLine());

            handler.getContext().sendASyncCommand(drawShapeCommand);
        }
        if( drawEndPoints==null ){
            drawEndPoints=new DrawPathCommand();
            int vertexRadius = PreferenceUtil.instance().getVertexRadius();
            Path path = new Path(getDisplay());
            int i = vertexRadius+vertexRadius;
            path.addRectangle(dragStarted.getX()-vertexRadius, dragStarted.getY()-vertexRadius, i, i);
            if( EditPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_FILL_VERTICES) ){
                drawEndPoints.setFill(PreferenceUtil.instance().getDrawVertexFillColor());
            }
            drawEndPoints.setPath(path);
            handler.getContext().sendASyncCommand(drawEndPoints);
            
        }
    }

    private Device getDisplay() {
        Display display = PlatformUI.getWorkbench().getDisplay();
        if( display!=null )
            return display;
        return Display.getDefault();
    }

    /**
     * Runs the accept behaviours, creates a new EditGeom and sets it to be the Current Geom
     * 
     * @param handler
     */
    private UndoableMapCommand createNewGeom( EditToolHandler handler ) {
        List<UndoableMapCommand> commands=new ArrayList<UndoableMapCommand>();
        
        EditBlackboard editBlackboard = handler.getCurrentShape().getEditBlackboard();
        final CreateEditGeomCommand createEditGeomCommand = new CreateEditGeomCommand(
                editBlackboard, "freeHandDraw"+System.currentTimeMillis(), ShapeType.LINE); //$NON-NLS-1$
        commands.add(createEditGeomCommand);
        class PrimitiveProvider implements IBlockingProvider<PrimitiveShape> {
            public PrimitiveShape get(IProgressMonitor monitor, Object... params) {
                return createEditGeomCommand.get(monitor).getShell();
            }

        }

        commands.add(new SetCurrentGeomCommand(handler, new PrimitiveProvider())); 
       
        return new UndoableComposite(commands);
    }
    
    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    public Object getKey( EditToolHandler handler ) {
        return this;
    }

    boolean isOverEndPoint( MapMouseEvent e, int vertexRadius, Point start ) {
        EditUtils.MinFinder finder = new EditUtils.MinFinder(start);
        double dist = finder.dist(Point.valueOf(e.x, e.y));
        boolean b = dist < vertexRadius;
        return b;
    }

    private class Creator implements LockingBehaviour {

        private Point start;

        /**
         * @param dragStarted
         */
        public Creator( Point dragStarted ) {
            start = dragStarted;
        }

        public Object getKey( EditToolHandler handler ) {
            return FreeHandPolygonDrawBehaviour.this;
        }

        public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {

            return handler.isLockOwner(this) && eventType == EventType.RELEASED;
        }

        public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
                EventType eventType ) {

            try {

                PrimitiveShape shape = handler.getCurrentShape();
                int vertexRadius = PreferenceUtil.instance().getVertexRadius();
                if (type == ShapeType.UNKNOWN) {
                    if (isOverEndPoint(e, vertexRadius, start))
                        shape.getEditGeom().setShapeType(ShapeType.POLYGON);
                    else {
                        shape.getEditGeom().setShapeType(ShapeType.LINE);
                    }
                } else {
                    shape.getEditGeom().setShapeType(type);
                }
                
                if( shape.getEditGeom().getShapeType()==ShapeType.POLYGON  )
                    generalPath.close();
                
                UndoableComposite appendPathToShape;
                    appendPathToShape = EditUtils.instance.appendPathToShape(handler, generalPath.getPathIterator(),
                        shape);
                // Now we need to collapse the last two vertices if they are
                // within snapping distance.  
                if ( shape.getEditGeom().getShapeType()==ShapeType.POLYGON ){
                    
                    List< ? extends MapCommand> commands = appendPathToShape.getCommands();
                    AddVertexCommand lastVertexCommand=(AddVertexCommand) commands.get(commands.size()-1);
                    AddVertexCommand previousVertexCommand=(AddVertexCommand) commands.get(commands.size()-2);
                    EditUtils.MinFinder finder = new EditUtils.MinFinder(lastVertexCommand.getPointToAdd());
                    double dist = finder.dist(previousVertexCommand.getPointToAdd());
                    if( dist<vertexRadius ){
                        commands.remove(previousVertexCommand);
                    }
                }
                if( handler.getCurrentShape()!=null )
                    appendPathToShape.getFinalizerCommands().add(new SetEditStateCommand( handler, EditState.MODIFYING));
                else{
                    appendPathToShape.getFinalizerCommands().add(new SetEditStateCommand( handler, EditState.CREATING));
                }
                
                appendPathToShape.setMap(handler.getContext().getMap());
                appendPathToShape.run(new NullProgressMonitor());
                
                return new UndoRedoCommand(appendPathToShape);
            } catch (Exception exception) {
                throw (RuntimeException) new RuntimeException( exception );
            } finally {
                creator = null;
                handler.getBehaviours().remove(this);
                handler.unlock(this);
                drawShapeCommand.dispose();
                drawShapeCommand = null;
                drawEndPoints.dispose();
                drawEndPoints=null;
                generalPath=null;
            }
        }

        public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
            EditPlugin.log("", error); //$NON-NLS-1$
        }

    }

}
