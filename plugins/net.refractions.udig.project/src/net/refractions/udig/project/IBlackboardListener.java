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
package net.refractions.udig.project;


/**
 * Listener for listening to a {@link net.refractions.udig.project.IBlackboard} objects.
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
