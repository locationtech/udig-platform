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

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.commands.AddVertexCommand;
import net.refractions.udig.tools.edit.commands.CreateAndSelectHoleCommand;
import net.refractions.udig.tools.edit.commands.SetEditStateCommand;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;

/**
 * <p>Requirements: * <ul>
 * <li>EventType==RELEASED</li>
 * <li>Current State == Creating</li>
 * <li>Current Shape != null</li>
 * <li>Button1 is released</li>
 * <li>no buttons are down</li>
 * <li>no modifiers are down</li>
 * <li>current geom is a polygon or unknown</li> * </ul> * </p> * <p>Action: * <ul> * <li></li> * </ul> * </p>
 * @author jones
 * @since 1.1.0
 */
public class StartHoleCuttingBehaviour implements EventBehaviour {

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean legalState = true;
        boolean legalEventType=eventType==EventType.RELEASED;
        boolean shapeAndGeomNotNull=handler.getCurrentShape()!=null;
        boolean button1Released=e.button==MapMouseEvent.BUTTON1;
        if( !shapeAndGeomNotNull )
            return false;
        EditGeom currentGeom = handler.getCurrentGeom();
        boolean selectedShapeIsPolygon=currentGeom.getShapeType()==ShapeType.POLYGON
            || currentGeom.getShapeType()==ShapeType.UNKNOWN;
        
        if ( !( legalState && legalEventType && shapeAndGeomNotNull && button1Released 
        && !e.buttonsDown() && !e.modifiersDown() && selectedShapeIsPolygon) )
            return false;
        
        Point point=Point.valueOf(e.x, e.y);
        
        if( !currentGeom.getShell().contains(point, true) )
            return false;
        
        for( PrimitiveShape shape : currentGeom.getHoles() ) {
            if( shape.contains(point, true) )
                return false;
             }
        return true;
    }
    
    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
        if( !isValid(handler, e, eventType))
            throw new IllegalStateException("Current state is illegal this method should not be called"); //$NON-NLS-1$
        
        List<UndoableMapCommand> commands=new ArrayList<UndoableMapCommand>();
        commands.add(new SetEditStateCommand(handler, EditState.CREATING));
        commands.add(new CreateAndSelectHoleCommand(handler));
        commands.add(new AddVertexCommand(handler, handler.getEditBlackboard(handler.getEditLayer()),
                Point.valueOf(e.x, e.y)));
        return new UndoableComposite(commands);
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
    }

}
