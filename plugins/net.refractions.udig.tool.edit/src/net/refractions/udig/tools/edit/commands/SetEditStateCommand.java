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
package net.refractions.udig.tools.edit.commands;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;

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

}
