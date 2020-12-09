/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.command;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A MapCommand in uDig describes an action that modifies the system's model.
 * <p>
 * Commands are single fire objects. They cannot be used more than once. This is to allow undoable
 * commands to be a normal command, not a special case command.
 * </p>
 * <p>
 * <p>
 * Commands normally have factories associated with them, but they are also prototypes. The copy
 * method returns a new MapCommand without the undo data. The new MapCommand can safely be executed
 * with no negative side-effects.
 * </p>
 * 
 * @see A set of possible command categories are: zoom, pan, cut, paste, addVertex, etc.. Most
 *      commands are associated with tool whose job is to construct the commands. A MapCommand
 *      object describes an concrete change, for example: setBBox(0,0,1,1); setBBox(2,2,3,3) would
 *      be a separate object.
 * @author jeichar
 */
public interface Command {
    /**
     * The method that performs the work of the command.
     * <p>
     * Run is called by UDIG when the command is received. Commands are run in a seperate thread.
     * </p>
     * 
     * @param monitor A progress monitor used by the command to report on its internal state. API
     *        how is this associated with a Thread? is it a Thread?
     * @throws Exception
     */
    public void run( IProgressMonitor monitor ) throws Exception;

    /**
     * Each command has a name that is displayed with the undo/redo buttons.
     * </p>
     * @return The name of the command (often translated)
     */
    public String getName();
}
