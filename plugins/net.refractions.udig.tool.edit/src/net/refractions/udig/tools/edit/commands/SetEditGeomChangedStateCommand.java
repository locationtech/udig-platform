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
import net.refractions.udig.tools.edit.support.EditGeom;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Sets the changed state of the editGeom
 * @author jones
 * @since 1.1.0
 */
public class SetEditGeomChangedStateCommand extends AbstractCommand implements UndoableMapCommand {

    private boolean newState;
    private EditGeom geom;
    private boolean oldState;

    /**
     * @param currentGeom
     * @param newState
     */
    public SetEditGeomChangedStateCommand( EditGeom currentGeom, boolean newState ) {
        this.geom=currentGeom;
        this.newState=newState;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        this.oldState=geom.isChanged();
        geom.setChanged(newState);
    }

    public String getName() {
        return Messages.SetEditGeomChangedStateCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        geom.setChanged(oldState);
    }

}
