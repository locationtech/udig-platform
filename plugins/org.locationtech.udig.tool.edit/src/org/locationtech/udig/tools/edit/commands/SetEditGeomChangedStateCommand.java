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
import org.locationtech.udig.tools.edit.support.EditGeom;

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
