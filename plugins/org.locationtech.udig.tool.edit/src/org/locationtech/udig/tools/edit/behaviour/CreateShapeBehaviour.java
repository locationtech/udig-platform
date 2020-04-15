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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.LockingBehaviour;
import org.locationtech.udig.tools.edit.MouseTracker;
import org.locationtech.udig.tools.edit.commands.CreateEditGeomCommand;
import org.locationtech.udig.tools.edit.commands.DeselectEditGeomCommand;
import org.locationtech.udig.tools.edit.commands.SetCurrentGeomCommand;
import org.locationtech.udig.tools.edit.commands.SetEditStateCommand;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditUtils;
import org.locationtech.udig.tools.edit.support.ShapeType;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;

/**
 * <p>Requirements: * <ul> * <li>Mouse Dragged</li>
 * <li>CurrentState == NONE</li>
 * <li>Mouse button 1 down</li>
 * </ul> * </p> * <p>Action: * <ul> * <li>draws a shape as the mouse is dragged</li>
 * <li>creates a feature on the current layer when mouse is released</li>
 * </ul> * </p>
 * @author jones
 * @since 1.1.0
 */
public class CreateShapeBehaviour implements EventBehaviour, LockingBehaviour {

    /**
     * creates a shape upon request.  
     * 
     * @author jones
     * @since 1.1.0
     */
    public static abstract class ShapeFactory {
        protected boolean middleAsOrigin = false;
        
        /**
         * Creates a GeneralPath with the top left corner at 0,0 and
         * a total width and height as indicated
         *
         * @param width width of path created
         * @param height height of path created
         * @return a GeneralPath with the top left corner at 0,0 and
         * a total width and height as indicated
         */
        public abstract GeneralPath create(int width, int height);
        
        public boolean useMiddleAsOrigin() {
            return middleAsOrigin;
        }
        public void setMiddleAsOrigin(boolean middleAsOrigin) {
            this.middleAsOrigin = middleAsOrigin;
        }

    }

    private ShapeFactory factory;
    private GeneralPath path;
    private DrawShapeCommand drawCommand;

    /**
     * @param factory
     */
    public CreateShapeBehaviour( ShapeFactory factory ) {
        this.factory=factory;
    }

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        if( handler.isLocked() && handler.isLockOwner(this) 
                && eventType==EventType.DRAGGED)
            return true;
        
        boolean legalState=handler.getCurrentState()==EditState.NONE;
        
        return !e.modifiersDown() && legalState && e.buttons==MapMouseEvent.BUTTON1 && eventType==EventType.DRAGGED;
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
        if( path==null ){
            path=factory.create(1,1);
            handler.getBehaviours().add(new Creator());
            handler.lock(this);
        }
        MouseTracker tracker=handler.getMouseTracker();
        int translationX;
        int translationY;
        if (factory.useMiddleAsOrigin())
        {
           translationX=tracker.getDragStarted().getX();
           translationY=tracker.getDragStarted().getY();
        }
        else
        {
           translationX=Math.min(tracker.getDragStarted().getX(), e.x);
           translationY=Math.min(tracker.getDragStarted().getY(), e.y);
        }

        int scaleX=Math.abs(tracker.getDragStarted().getX()-e.x);
        int scaleY=Math.abs(tracker.getDragStarted().getY()-e.y);
      //force square/circle if shift is activated
        if (e.isShiftDown()) 
        {
           if (scaleX > scaleY)
           {
               scaleY = scaleX;
           }
           else
           {
               scaleX = scaleY;
           }
        }
        

        AffineTransform transform=AffineTransform.getTranslateInstance(translationX, translationY);
        transform.scale(scaleX, scaleY);
        Shape transformedShape = path.createTransformedShape(transform);
        if( drawCommand==null ){
            drawCommand=new DrawShapeCommand();
            drawCommand.setPaint(PreferenceUtil.instance().getDrawGeomsLine());
            handler.getContext().sendSyncCommand(drawCommand);
        }
        drawCommand.setShape(transformedShape);
        handler.repaint();
        
        return null;
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    public Object getKey( EditToolHandler handler ) {
        return this;
    }

    private class Creator implements LockingBehaviour{

        public Object getKey( EditToolHandler handler ) {
            return CreateShapeBehaviour.this;
        }

        public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
            return handler.isLockOwner(this) && eventType==EventType.RELEASED && e.button==MapMouseEvent.BUTTON1;
        }

        public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
            try{
                PathIterator iter = drawCommand.getShape().getPathIterator(AffineTransform.getTranslateInstance(0,0), 1.0);
                UndoableComposite commands=new UndoableComposite();
                commands.getCommands().add(handler.getContext().getEditFactory().createNullEditFeatureCommand());
                EditBlackboard bb = handler.getEditBlackboard(handler.getEditLayer());
                commands.getCommands().add(new DeselectEditGeomCommand(handler, bb.getGeoms())); 
                ShapeType shapeType = determineLayerType(handler);
                
                
                CreateEditGeomCommand createEditGeomCommand = new CreateEditGeomCommand(bb, "newShape", shapeType); //$NON-NLS-1$
                commands.getCommands().add(createEditGeomCommand); 
                commands.getCommands().add(EditUtils.instance.appendPathToShape(iter, shapeType, handler, bb, createEditGeomCommand.getShapeProvider()));
                commands.getCommands().add( new SetCurrentGeomCommand(handler, createEditGeomCommand.getShapeProvider()));
                commands.getCommands().add(handler.getCommand(handler.getAcceptBehaviours()));
                
                commands.getFinalizerCommands().add( new SetEditStateCommand(handler, EditState.NONE));
                return commands;
            }finally{
                drawCommand.setValid(false);
                drawCommand=null;
                path=null;
                handler.unlock(this);
            }
        }

        private ShapeType determineLayerType( EditToolHandler handler ) {
            Class<?> type = handler.getEditLayer().getSchema().getGeometryDescriptor().getType().getBinding();
            
            if( LineString.class.isAssignableFrom(type) || LinearRing.class.isAssignableFrom(type) 
                    || MultiLineString.class.isAssignableFrom(type))
                return ShapeType.LINE;
            
            return ShapeType.POLYGON;
        }

        public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
            EditPlugin.log("", error); //$NON-NLS-1$
        }
        
    }
    
}
