/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

/**
 * Listener that is notified when a drop is completed.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IDropHandlerListener {
    /**
     * Called when an action is about to be ran.
     *
     * @param action action about to be ran.
     */
    public void starting(IDropAction action);
    /**
     * Called when no drop action could be found that can process the data.
     *
     * @param data data that was dropped.
     */
    public void noAction(Object data);
    /**
     * Called when an action is complete.
     *
     * @param action completed action.
     * @param error Null unless an error occurred during the execution of
     * the drop event;
     */
    public void done(IDropAction action, Throwable error);
}
