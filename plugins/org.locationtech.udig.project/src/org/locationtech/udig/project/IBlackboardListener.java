/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project;


/**
 * Listener for listening to a {@link org.locationtech.udig.project.IBlackboard} objects.
 * 
 * @author jones
 * @since 1.1.0
 */
public interface IBlackboardListener {
    /**
     * Called after the blackboard has been changed. This will not be called if the blackboard
     * is cleared.
     *
     * @param event The event containing the changed data.
     */
    void blackBoardChanged(BlackboardEvent event);

    /**
     * Called before the blackboard has been cleared.
     * 
     * @param source the blackboard that has been cleared.
     */
    void blackBoardCleared( IBlackboard source );
}
