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
package net.refractions.udig.project.command;

/**
 * A Command in uDig describes an action that modifies the system's model.
 * <p>
 * Commands are single fire objects. They cannot be used more than once. This is to allow undoable
 * commands to be a normal command, not a special case command.
 * </p>
 * <p>
 * <p>
 * Commands normally have factories associated with them, but they are also prototypes. The copy
 * method returns a new Command without the undo data. The new Command can safely be executed with
 * no negative side-effects.
 * </p>
 * 
 * @see A set of possible command categories are: zoom, pan, cut, paste, addVertex, etc.. Most
 *      commands are associated with tool whose job is to construct the commands. A Command object
 *      describes an concrete change, for example: setBBox(0,0,1,1); setBBox(2,2,3,3) would be a
 *      separate object.
 * @author jeichar
 */
public interface ProjectCommand {
    /**
     * The method that performs the work of the command.
     * <p>
     * Run is called by UDIG when the command is received.
     * </p>
     * API how is this associated with a Thread? is it a Thread?
     * 
     * @throws Exception
     */
    public void run() throws Exception;

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
     */
    public ProjectCommand copy();

    /**
     * Returns the name of the Command
     * 
     * @return The name of the command.
     */
    public String getName();

}
