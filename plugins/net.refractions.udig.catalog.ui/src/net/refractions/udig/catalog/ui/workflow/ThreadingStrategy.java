/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
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
 * Allows workflows to be run in different manners. For example one might want some sort of UI
 * indication of what is going on. The requirement is that all runnables must execute in order. If
 * there is a seperate thread or the same thread as the calling thread, that is up to the developer.
 * 
 * @author jesse
 * @since 1.1.0
 */
public interface ThreadingStrategy {

    /**
     * Ensures that the strategy is prepared to call runnable. This method is called often. If the
     * strategy is already prepared then that is fine.
     */
    void init();

    /**
     * Run a runnable. If run is called twice the first must execute first
     */
    void run( final Runnable runnable );

    /**
     * Allows the rest of the previous commands to complete but signals the strategy that no more
     * jobs can execute
     */
    void shutdown();

}
