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

import net.refractions.udig.project.command.UndoRedoCommand;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.commands.SetEditStateCommand;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * A behaviour that will run the accept behaviours registered with the EditToolHandler
 * 
 * <p>Requirements: * <ul> * <li>EventType==RELEASED</li> * <li>Current State == Creating</li>
 * <li>Current Shape != null</li>
 * <li>Button1 is released</li>
 * <li>no buttons are down</li>
 * <li>no modifiers are down</li>
 * </ul> * </p> * <p>Action: * <ul> * <li>Will run accept behaviours</li> *</ul> * </p>
 * @author jones
 * @since 1.1.0
 */
public class AcceptBehaviour implements EventBehaviour {

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean legalState=handler.getCurrentState()==EditState.CREATING;
        boolean legalEventType=eventType==EventType.RELEASED;
        boolean shapeAndGeomNotNull=handler.getCurrentShape()!=null;
        boolean button1Released=e.button==MapMouseEvent.BUTTON1;
        
        return legalState && legalEventType && shapeAndGeomNotNull && button1Released 
        && !e.buttonsDown() && !e.modifiersDown();
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
        if( !isValid(handler, e, eventType) )
            throw new IllegalArgumentException("Current state is not legal"); //$NON-NLS-1$
        List<UndoableMapCommand> commands=new ArrayList<UndoableMapCommand>();
                
        commands.add(handler.getCommand(handler.getAcceptBehaviours()));
        if( handler.getCurrentState()==EditState.CREATING)
            commands.add(new SetEditStateCommand(handler, EditState.MODIFYING));
        UndoableComposite undoableComposite = new UndoableComposite(commands);
        undoableComposite.setMap(handler.getContext().getMap());
        try {
            undoableComposite.run(new NullProgressMonitor());
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
        return new UndoRedoCommand(undoableComposite);
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

}
