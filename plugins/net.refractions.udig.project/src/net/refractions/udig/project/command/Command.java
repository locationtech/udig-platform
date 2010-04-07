/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.command;

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
     * Instantiates a new copy of the command that will operate in the same manner as the original
     * command. API isn't this cloneable?
     * 
     * @return A copy of the current command. The new command must run the same way as the current
     *         object.
     *         <p>
     *         If the current command has already executed it cannot be used again, but a copy may
     *         because a copy should contain none of the state side-effect that execution has on a
     *         command
     *         </p>
     * @deprecated
     */
    public Command copy();

    /**
     * Each command has a name that is displayed with the undo/redo buttons.
     * </p>
     * @return The name of the command (often translated)
     */
    public String getName();

}
