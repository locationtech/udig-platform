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
package net.refractions.udig.tools.edit;

import net.refractions.udig.project.command.UndoableMapCommand;


/**
 * This is a Stategy object for the (@link net.refractions.udig.tools.edit.latest.EditToolHandler} behaviour.
 * Each Behaviour is valid in a particular context and will be run by the
 * (@link net.refractions.udig.tools.edit.latest.EditToolHandler} if the isValid method returns true.
 *
 * @author jones
 * @since 1.1.0
 */
public interface Behaviour {

    /**
     * Called to determine whether this Behaviour is applicable and should be run.
     * @param handler handler that calls this Behaviour
     * @return true if this mode is applicable and should be run.
     */
    public boolean isValid( EditToolHandler handler );

    /**
     * The action to be performed by this Behaviour.  This action takes place in the event thread so it must
     * perform quickly.
     *
     * @param handler handler that calls this Behaviour
     * @return Command that will be executed in order to perform the behaviour
     */
    public UndoableMapCommand getCommand( EditToolHandler handler );

    /**
     * This method is called if an exception occurs during the execution of the run method.
     * <p>
     * This method should:
     * <ol>
     * <li>Rollback the changes made during the run method</li>
     * <li>Log the error in the plugin's log</li>
     * </ol>
     * @param error Error that occurred
     * @param command Command retrieved from getCommandMethod.  May be null if exception occurred while
     * executing getCommand();
     */
    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command );
}
