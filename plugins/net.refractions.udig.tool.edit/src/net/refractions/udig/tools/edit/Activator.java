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

/**
 * A "callback" object used to activate and deactivate functionality
 * associated with an EditTool - use is similar to a Runnable.
 * <p>
 * An EditToolHandler is provided for context information; an Activator
 * is called
 * @author jones
 * @since 1.1.0
 */
public interface Activator {

    /**
     * The action to be performed by when activating.
     *
     * @param handler
     */
    public void activate( EditToolHandler handler );
    /**
     * The action to be performed by when deactivating.
     *
     * @param handler
     */
    public void deactivate( EditToolHandler handler );
    
    /**
     * This method is called if an exception occurs during the execution of the activate method.  
     * <p>
     * This method should 
     * <ol>
     * <li>Rollback the changes made during the run method</li>
     * <li>Log the error in the plugin's log</li>
     * </ol>
     *
     * @param error Error that occurred
     * @param activating Indicates whether activator is being activated.
     */
    public void handleActivateError( EditToolHandler handler, Throwable error );
    
    /**
     * This method is called if an exception occurs during the execution of the deactivate method.  
     * <p>
     * This method should 
     * <ol>
     * <li>Rollback the changes made during the run method</li>
     * <li>Log the error in the plugin's log</li>
     * </ol>
     *
     * @param error Error that occurred
     * @param activating Indicates whether activator is being activated.
     */
    public void handleDeactivateError( EditToolHandler handler, Throwable error );


}
