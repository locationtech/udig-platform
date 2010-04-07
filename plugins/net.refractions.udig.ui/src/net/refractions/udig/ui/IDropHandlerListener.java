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
