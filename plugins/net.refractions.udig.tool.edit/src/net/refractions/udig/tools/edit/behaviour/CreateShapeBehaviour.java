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
package net.refractions.udig.tools.edit.behaviour;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.LockingBehaviour;
import net.refractions.udig.tools.edit.MouseTracker;
import net.refractions.udig.tools.edit.commands.CreateEditGeomCommand;
import net.refractions.udig.tools.edit.commands.DeselectEditGeomCommand;
import net.refractions.udig.tools.edit.commands.SetCurrentGeomCommand;
import net.refractions.udig.tools.edit.commands.SetEditStateCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.ShapeType;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;

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
