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

import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.UndoRedoCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.commands.DeselectEditGeomCommand;
import org.locationtech.udig.tools.edit.commands.StartEditingCommand;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.ShapeType;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Creates a new Geometry and feature
 * <p>Requirements: * <ul> * <li>current state is NONE</li>
 * <li>eventType is RELEASED</li>
 * <li>no modifiers</li>
 * <li>button1 released</li>
 * <li>no buttons down</li>
 * </ul> * </p> * <p>Action: * <ul> * <li>Sets the currentGeom to be the default geom on the black board</li> * <li>Sets the state to CREATING</li>
 * <li>Adds a point to the geom</li>
 * </ul> * </p>
 * @author jones
 * @since 1.1.0
 */
public class StartEditingBehaviour implements EventBehaviour {

    private ShapeType type;

    public StartEditingBehaviour(ShapeType type){
        this.type=type;
    }
    
    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean goodState = handler.getCurrentState()!=EditState.NONE && handler.getCurrentState()==EditState.MODIFYING 
            || handler.getCurrentState()==EditState.NONE && handler.getCurrentState()!=EditState.MODIFYING;
        boolean releasedEvent = eventType==EventType.RELEASED;
        boolean noModifiers =  !(e.modifiersDown());
        boolean button1 = e.button==MapMouseEvent.BUTTON1;
        boolean noButtonsDown = !e.buttonsDown();
        return goodState && releasedEvent && noButtonsDown && noModifiers && button1;
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        if( !isValid(handler, e, eventType))
            throw new IllegalArgumentException("Current State is not valid for behaviour"); //$NON-NLS-1$
        List<UndoableMapCommand> commands=new ArrayList<UndoableMapCommand>();
        commands.add(handler.getContext().getEditFactory().createNullEditFeatureCommand());
        ILayer editLayer = handler.getEditLayer();
        EditBlackboard bb = handler.getEditBlackboard(editLayer);
        commands.add(new DeselectEditGeomCommand(handler, bb.getGeoms())); 
        commands.add(new StartEditingCommand(handler, e, type));
        
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
        EditPlugin.log(""+handler, error); //$NON-NLS-1$
    }

}
