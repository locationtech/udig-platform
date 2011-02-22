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

import net.refractions.udig.project.command.UndoRedoCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.animation.MessageBubble;
import net.refractions.udig.tools.edit.commands.AddVertexCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Point;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Add a vertex as the last vertex in the shape
 *
 * <p>Requirements:
 * <ul>
 * <li>event type == RELEASE</li>
 * <li>edit state == CREATING </li>
 * <li>no modifiers down</li>
 * <li>button 1 released</li>
 * <li>no buttons down</li>
 * <li>current shape and geometry are set</li>
 * <li>mouse is not over a vertex of the current shape</li>
 * </ul>
 * </p>
 * @author Jesse
 * @since 1.1.0
 */
public class AddVertexWhileCreatingBehaviour implements EventBehaviour {

    private IEditValidator validator=IEditValidator.TRUE;

    public void setEditValidator( IEditValidator validator ){
        this.validator=validator;
    }

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean legalState=handler.getCurrentState()==EditState.CREATING;
        boolean legalEventType=eventType==EventType.RELEASED;
        boolean shapeAndGeomNotNull=handler.getCurrentShape()!=null;
        boolean button1Released=e.button==MapMouseEvent.BUTTON1;

        return legalState && legalEventType && shapeAndGeomNotNull && button1Released
        && !e.buttonsDown() && !e.modifiersDown() && !overShapeVertex(handler, e);
    }

    private boolean overShapeVertex(EditToolHandler handler, MapMouseEvent e) {

        Point vertexOver=handler.getEditBlackboard(handler.getEditLayer()).overVertex(Point.valueOf(e.x, e.y),
                PreferenceUtil.instance().getVertexRadius());

        return handler.getCurrentShape().hasVertex( vertexOver );
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
