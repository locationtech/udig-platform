/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.behaviour;

import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.commands.RemoveSelectedVerticesCommand;
import net.refractions.udig.tools.edit.commands.SelectVertexCommand;
import net.refractions.udig.tools.edit.commands.SelectVertexCommand.Type;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Point;

/**
 * A behaviour that deletes a vertex from an editGeom.
 * 
 * <p>Requirements:
 * <ul>
 * <li>event type == RELEASE</li>
 * <li>edit state == MODIFYING </li>
 * <li>no modifiers down</li>
 * <li>button 1 released</li>
 * <li>no buttons down</li>
 * <li>current shape and geom are set</li>
 * <li>mouse is not over a vertex of the current shape</li>
 * <li>mouse is over an edge </li>
 * </ul>
 * </p>
 * 
 * @author jones
 * @since 1.1.0
 */
public class RemoveVertexBehaviour implements EventBehaviour {

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean legalEventType=eventType==EventType.RELEASED;
        boolean shapeAndGeomNotNull=handler.getCurrentShape()!=null;
        boolean button1Released=e.button==MapMouseEvent.BUTTON1;
        
        return legalEventType && shapeAndGeomNotNull && button1Released 
        && !e.buttonsDown() && !e.modifiersDown() && overGeomVertex(handler, e);
    }

    private boolean overGeomVertex(EditToolHandler handler, MapMouseEvent e) {
        
        Point vertexOver=handler.getEditBlackboard(handler.getEditLayer()).overVertex(Point.valueOf(e.x, e.y), 
                PreferenceUtil.instance().getVertexRadius());
        
        return handler.getCurrentGeom().hasVertex( vertexOver );
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
        if( !isValid(handler, e, eventType) )
            throw new IllegalStateException("isValid() return false"); //$NON-NLS-1$

        Point vertexOver=handler.getEditBlackboard(handler.getEditLayer()).overVertex(Point.valueOf(e.x, e.y), 
                PreferenceUtil.instance().getVertexRadius());
        
        UndoableComposite command=new UndoableComposite();
        EditBlackboard bb = handler.getCurrentShape().getEditBlackboard();
        command.getCommands().add(new SelectVertexCommand(bb, vertexOver, Type.SET));
        command.getCommands().add(new RemoveSelectedVerticesCommand(handler));
        
        return command;
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

}
