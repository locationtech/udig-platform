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

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.commands.SelectionBoxCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.LockingBehaviour;
import org.locationtech.udig.tools.edit.commands.SelectVertexCommand;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.Point;

/**
 * <p>
 * Requirements:
 * <ul>
 * <li> Mouse is dragged </li>
 * <li> Current State != Creating </li>
 * <li> CurrentShape != null </li>
 * <li> no modifiers or shift down</li>
 * <li> only mouse 1 down </li>
 * </ul>
 * </p>
 * <p>
 * Action:
 * <ul>
 * <li>draw rectangle</li>
 * <li>add behaviour that will select vertices on mouse Released</li>
 * <li>Locks the EditTool handler until mouse is released so other behaviours won't interfere</li>
 * </ul>
 * </p>
 * 
 * @author jones
 * @since 1.1.0
 */
public class SelectVerticesWithBoxBehaviour implements LockingBehaviour {

    private SelectionBoxCommand drawShapeCommand;
    private SelectBehaviour selectBehaviour;

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean legalState = handler.getCurrentState() != EditState.CREATING;
        boolean legalEventType = eventType == EventType.DRAGGED;
        boolean shapeAndGeomNotNull = handler.getCurrentShape() != null;
        boolean legalModifiers = e.isShiftDown() || !e.modifiersDown();

        return legalState && legalEventType && shapeAndGeomNotNull 
                && legalModifiers && e.buttons==MapMouseEvent.BUTTON1;
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {

        if (selectBehaviour == null) {
            handler.lock(this);
            selectBehaviour=new SelectBehaviour();
            handler.getBehaviours().add(selectBehaviour);
            drawShapeCommand = new SelectionBoxCommand();
            
            handler.getContext().getViewportPane().addDrawCommand(drawShapeCommand);
        }
        Point start = handler.getMouseTracker().getDragStarted();
        drawShapeCommand.setShape(new Rectangle(Math.min(start.getX(), e.x), Math.min(
                start.getY(), e.y), Math.abs(start.getX() - e.x), Math.abs(start.getY()
                - e.y)));
        
        handler.repaint();
        
        return null;
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    class SelectBehaviour implements LockingBehaviour {

        public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
           
            boolean legalEventType = eventType == EventType.RELEASED;
            
            return legalEventType;
        }

        public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
                EventType eventType ) {
                List<EventBehaviour> behaviours = handler.getBehaviours();
                behaviours.remove(selectBehaviour);

                drawShapeCommand.setValid(false);
                
                Set<Point> points = new HashSet<Point>();
                Rectangle rect=(Rectangle) drawShapeCommand.getShape();
                EditGeom geom = handler.getCurrentGeom();
                for( int x=rect.x, maxx=(int) rect.getMaxX(); x<maxx; x++){
                    for( int y=rect.y, maxy=(int) rect.getMaxY(); y<maxy; y++){
                        Point valueOf = Point.valueOf(x,y);
                        if( geom.hasVertex(valueOf))
                            points.add(valueOf);
                    }
                }
                
                handler.unlock(this);
                selectBehaviour=null;
                drawShapeCommand=null;
                handler.repaint();
                if( e.isShiftDown() )
                    return new SelectVertexCommand(handler.getCurrentGeom().getEditBlackboard(), points,
                            SelectVertexCommand.Type.ADD);
                else{
                    return new SelectVertexCommand(handler.getCurrentGeom().getEditBlackboard(), points,
                            SelectVertexCommand.Type.SET);
                }

        }

        public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
            EditPlugin.log("", error); //$NON-NLS-1$
        }

        public Object getKey( EditToolHandler handler ) {
            return SelectVerticesWithBoxBehaviour.this;
        }

    }

    public Object getKey( EditToolHandler handler ) {
        return this;
    }

}
