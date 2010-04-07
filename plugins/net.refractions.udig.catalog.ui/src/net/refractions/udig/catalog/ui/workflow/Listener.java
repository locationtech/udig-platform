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
package net.refractions.udig.catalog.ui.workflow;


/**
 * A listener that needs to be notified when a {@link Workflow} changes.
 * 
 * @author jesse
 * @since 1.1.0
 */
public interface Listener {

    /**
     * Event thrown when the pipe moves to a new state in the forward direction.
     * 
     * @param current The current state.
     * @param prev The state before the current state.
     */
    void forward( State current, State prev );

    /**
     * Event thrown when the pipe moves to a new state in a backward direction.
     * 
     * @param current The curent state.
     * @param next The state after the current state.
     */
    void backward( State current, State next );

    /**
     * Event thrown when a state successfully completes its job.
     * 
     * @param state The current state.
     */
    void statePassed( State state );

    /**
     * Event thrown when a state can not complete its job.
     * 
     * @param state The current state.
     */
    void stateFailed( State state );

    /**
     * Event thrown when the workflow is started.
     * 
     * @param first The first state of the pipe
     */
    void started( State first );

    /**
     * Event thrown when workflow is finished.
     * 
     * @param last The last state of the pipe
     */
    void finished( State last );
}