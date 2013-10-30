/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.workflow;


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
