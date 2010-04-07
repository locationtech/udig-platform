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
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;

/**
 * This is a Strategy object for the (@link net.refractions.udig.tools.edit.latest.EditToolHandler}
 * behaviour. Each EventBehavior is valid in a particular context and will be run by the (@link
 * net.refractions.udig.tools.edit.latest.EditToolHandler} if the isValid method returns true.
 * <p>
 * An example is a SelectGeometryBehaviour.
 * <p>
 * Context: EditState is MODIFYING, EventType is RELEASED, mouse button is button 1 and no keys are
 * pressed.
 * </p>
 * <p>
 * Action: If mouse is over a feature add the Geometry to the EditBlackBoard and set the
 * EditToolHandler's current Geometry and Shape.
 * </p>
 * <p>
 * Error handling: If exception occurs reset the EditBlackBoard and current Geometry and shape
 * </p>
 * </p>
 * 
 * @author jones
 * @since 1.1.0
 */
public interface EventBehaviour{

    /**
     * Called to determine whether this EventBehaviour is applicable and should be run.
     * 
     * @param handler handler that calls this Behaviour
     * @param e mouse event that just occurred.
     * @param eventType the type of event that just occurred
     * @return true if this mode is applicable and should be run.
     */
    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType );

    /**
     * The action to be performed by this EventBehaviour.  This action takes place in the event thread so it must
     * perform VERY quickly.
     *
     * @param handler handler that calls this Behaviour
     * @param e Event that occurred.
     * @param eventType The type of event that has occurred
     * @return Command that will be executed in order to perform the behaviour 
     */
    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e, EventType eventType );

    /**
     * This method is called if an exception occurs during the execution of the run method.  
     * <p>
     * This method should 
     * <ol>
     * <li>Rollback the changes made during the run method</li>
     * <li>Log the error in the plugin's log</li>
     * </ol>
     *
     * @param handler handler that calls this Behaviour
     * @param error Error that occurred
     * @param command  Command retrieved from getCommandMethod.  May be null if exception occurred while
     * executing getCommand();
     */
    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command );
}
