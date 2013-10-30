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

import org.locationtech.udig.project.command.UndoRedoCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.commands.DefaultCancelEditingCommand;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * This behaviour sets the current state to NONE, the Current Edit SimpleFeature to null, the Current Shape to null and 
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
