/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.workflow;

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
