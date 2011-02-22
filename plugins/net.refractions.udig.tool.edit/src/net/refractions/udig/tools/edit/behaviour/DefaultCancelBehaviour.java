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
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.commands.DefaultCancelEditingCommand;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * This behaviour sets the current state to NONE, the Current Edit Feature to null, the Current Shape to null and
 * clears the current map's blackboards.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class DefaultCancelBehaviour implements Behaviour {

    public boolean isValid( EditToolHandler handler ) {
        return true;
    }

    public UndoableMapCommand getCommand( EditToolHandler handler ) {
        DefaultCancelEditingCommand defaultCancelEditingCommand = new DefaultCancelEditingCommand(handler);
        defaultCancelEditingCommand.setMap(handler.getContext().getMap());
        try {
            defaultCancelEditingCommand.run(new NullProgressMonitor());
        } catch (Exception e) {
            throw (RuntimeException) new RuntimeException( e );
        }
        return new UndoRedoCommand(defaultCancelEditingCommand);
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

}
