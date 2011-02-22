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

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.LockingBehaviour;
import net.refractions.udig.tools.edit.commands.SelectVertexCommand;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;

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
