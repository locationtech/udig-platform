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

import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.commands.SelectHoleCommand;
import net.refractions.udig.tools.edit.support.ShapeType;

/**
 * <p>Requirements: * <ul> * <li>event type == RELEASE</li>
 * <li>edit state == MODIFYING </li>
 * <li>no modifiers down</li>
 * <li>button 1 released</li>
 * <li>no buttons down</li>
 * <li>current shape and geom are set and shape is the shell of the geom</li>
 * </ul> * </p> * <p>Action: * <ul> * <li>Selects the clicked hole or nothing</li>
 * </ul> * </p>
 * @author jones
 * @since 1.1.0
 */
public class SelectHoleBehaviour implements EventBehaviour {

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean legalState=handler.getCurrentState()==EditState.MODIFYING;
        boolean legalEventType=eventType==EventType.RELEASED;
        boolean shapeAndGeomNotNull=handler.getCurrentShape()!=null;
        boolean button1Released=e.button==MapMouseEvent.BUTTON1;
        if( !legalState || !legalEventType || !shapeAndGeomNotNull || !button1Released )
            return false;
        if( !shapeAndGeomNotNull )
            return false;
        boolean selectedShapeIsPolygon=handler.getCurrentGeom().getShapeType()==ShapeType.POLYGON
        || handler.getCurrentGeom().getShapeType()==ShapeType.UNKNOWN;

        return !e.buttonsDown() && !e.modifiersDown() && selectedShapeIsPolygon;
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
        if( !isValid(handler, e, eventType)){
            throw new IllegalArgumentException("Current state is not legal.  This command" + //$NON-NLS-1$
                    "should not be ran"); //$NON-NLS-1$
        }
        return new SelectHoleCommand(handler, e);
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
    }

}
