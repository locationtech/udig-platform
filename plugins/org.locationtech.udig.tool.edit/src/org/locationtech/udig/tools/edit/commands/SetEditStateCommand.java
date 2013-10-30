/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Sets the current state in the handler.
 * 
 * @author jones
 * @since 1.1.0
 */
public class SetEditStateCommand extends AbstractCommand implements UndoableMapCommand {

    private EditToolHandler handler;
    private EditState oldState;
    private EditState state;
    
    public SetEditStateCommand( EditToolHandler handler2, EditState newState ) {
        this.handler=handler2;
        this.state=newState;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        this.oldState=handler.getCurrentState();
        handler.setCurrentState(state);
    }

    public String getName() {
        return Messages.SetEditStateCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        handler.setCurrentState(oldState);
    }
    @Override
    public String toString() {
        return state.name();
    }

}
