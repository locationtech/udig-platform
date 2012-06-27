/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to license under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.arc.internal.beahaviour;

import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventType;

/**
 * Allows to control the user interface related feedback actions that take action while an EditTool
 * is being used.
 * <p>
 * This is needed as most of the time, feedback actions require to set up some
 * {@link IDrawCommand draw command} on the {@link ViewportPane}, and thus the life cycle of the
 * command is suck to the life cycle of the tool in use, so it is needed a shared access to the draw
 * command at the different tool actions, like accept, cancel, or other events.
 * </p>
 * <p>
 * Thus, implementations of this interface allows a single access point to an specific tool set of
 * feedback actions by setting up {@link EditToolFeedbackBehaviour}s,
 * {@link CancelFeedbakBehaviour}s and {@link AcceptFeedbackBehaviour}s sharing a common
 * EditToolFeedbackManager.
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 0.1.0
 * @see EditToolFeedbackBehaviour
 * @see CancelFeedbakBehaviour
 * @see AcceptFeedbackBehaviour
 */
public interface EditToolFeedbackManager {

    /**
     * Implementations shall evaluate the current state of the editting tool in use and return
     * whether the feedback command shall be ran or not.
     * 
     * @param handler the edit tool handler holding the edit state for the tool in use
     * @param event the mouse event that originates the call
     * @param eventType the type of mouse event being treated
     * @return <code>true</code> if it is ok to call the
     *         {@link #getFeedbackCommand(EditToolHandler, MapMouseEvent, EventType)} method,
     *         <code>false</code> otherwise.
     */
    public boolean isValid( EditToolHandler handler, MapMouseEvent event, EventType eventType );

    /**
     * Runs a specific feedback action (for example, drawing a shape) and returns an undoable map
     * command only if it makes sense.
     * <p>
     * Upon a call to this method, it is assumed that
     * {@link #isValid(EditToolHandler, MapMouseEvent, EventType)} has been called previously, it
     * returned <code>true</code> and the <code>handler</code>, <code>event</code>, and
     * <code>eventType</code> states have not changed.
     * </p>
     * 
     * @param handler the edit tool handler holding the edit state for the tool in use
     * @param event the mouse event that originates the call
     * @param eventType the type of mouse event being treated
     * @return and UndoableMapCommand if needed, <code>null</code> otherwise.
     */
    public UndoableMapCommand getFeedbackCommand( EditToolHandler handler, MapMouseEvent event,
                                                  EventType eventType );

    /**
     * Executes the feedback action to be ran when the edit tool in course has been cancelled.
     * <p>
     * Most of the time it'll account just to clear the shape being drwan as the tool's feedback, or
     * any other resource being used, like the status bar or so.
     * </p>
     * 
     * @param handler
     * @return
     */
    public UndoableMapCommand getCancelCommand( EditToolHandler handler );

    /**
     * Executes the feedback action to be ran when the edit tool in course has been cancelled.
     * 
     * @param handler
     * @return
     */
    public UndoableMapCommand getAcceptCommand( EditToolHandler handler );
}