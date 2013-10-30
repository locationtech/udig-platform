/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
