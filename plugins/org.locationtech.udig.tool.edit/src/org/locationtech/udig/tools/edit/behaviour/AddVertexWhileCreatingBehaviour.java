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

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.UndoRedoCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.animation.MessageBubble;
import org.locationtech.udig.tools.edit.commands.AddVertexCommand;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Add a vertex as the last vertex in the shape
 * 
 * <p>Requirements: * <ul> * <li>event type == RELEASE</li> * <li>edit state == CREATING </li>
 * <li>no modifiers down</li>
 * <li>button 1 released</li>
 * <li>no buttons down</li>
 * <li>current shape and geometry are set</li>
 * <li>mouse is not over a vertex of the current shape</li>
 * </ul> * </p> * @author Jesse
 * @since 1.1.0
 */
public class AddVertexWhileCreatingBehaviour implements EventBehaviour {

    private IEditValidator validator=IEditValidator.TRUE;

    public void setEditValidator( IEditValidator validator ){
        this.validator=validator;
    }
    
    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean legalState=handler.getCurrentState()==EditState.CREATING;
        boolean legalEventType=eventType==EventType.RELEASED || eventType==EventType.DOUBLE_CLICK;
        boolean shapeAndGeomNotNull=handler.getCurrentShape()!=null;
        boolean button1Released=e.button==MapMouseEvent.BUTTON1;
        
        Point point = Point.valueOf(e.x, e.y);
        
        return legalState && legalEventType && shapeAndGeomNotNull && button1Released 
        && !e.buttonsDown() && !e.modifiersDown() && isNotDuplicated(handler, point);
    }

    /**
     * This code checks to see if we are already *over* an existing
     * point in this shape.
     * <p>
     * The only point we are allowed to duplicate is the first point
     * (in order to close a LineString or Polygon). This method
     * is used to catch other cases, preventing us from two points
     * by accident.
     * <p>
     * 
     * @param handler
     * @param e
     * @return true If this point is allowed 
     */
    protected boolean isNotDuplicated(EditToolHandler handler, Point point) {
        PrimitiveShape currentShape = handler.getCurrentShape();
        if (currentShape.getNumPoints() == 0 ) {
            // no points to be over            
            return true; 
        }
        
        ILayer editLayer = handler.getEditLayer();
        
        EditBlackboard editBlackboard = handler.getEditBlackboard(editLayer);
        final int vertexRadius = PreferenceUtil.instance().getVertexRadius();
        
        Point vertexOver=editBlackboard.overVertex(point, vertexRadius);
        
        if( vertexOver == null ) {
            // we are not over any points
            return true;             
        }
        if( currentShape.getNumPoints() > 2 && vertexOver.equals( currentShape.getPoint(0) )) {
            // forms a closed linestring or polygon
            return true;
        }
        return !currentShape.hasVertex( vertexOver ) ;        
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        
        String reasonForFaliure = validator.isValid(handler, e, eventType);
        if (reasonForFaliure !=null ){
            AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), new MessageBubble(e.x, e.y, 
                    Messages.AddVertexWhileCreatingBehaviour_illegal+"\n"+reasonForFaliure, PreferenceUtil.instance().getMessageDisplayDelay())); //$NON-NLS-1$
            return null;
        }
        
        Point valueOf = Point.valueOf(e.x, e.y);
        EditBlackboard editBlackboard = handler.getEditBlackboard(handler.getEditLayer());
        Point destination = handler.getEditBlackboard(handler.getEditLayer()).overVertex(valueOf, PreferenceUtil.instance().getVertexRadius());
        if( destination==null )
            destination=valueOf;
        
        AddVertexCommand addVertexCommand = new AddVertexCommand(handler, editBlackboard, destination);
        try {
            addVertexCommand.setMap(handler.getContext().getMap());
            addVertexCommand.run(new NullProgressMonitor());
        } catch (Exception e1) {
            throw (RuntimeException) new RuntimeException( ).initCause( e1 );
        }
        return new UndoRedoCommand(addVertexCommand);
    }
    

}
