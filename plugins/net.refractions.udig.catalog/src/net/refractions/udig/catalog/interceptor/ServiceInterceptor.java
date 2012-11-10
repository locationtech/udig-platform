/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.interceptor;

import net.refractions.udig.catalog.IService;

/**
 * An interceptor that is ran on a service.
 * 
 * @author Jody
 * @since 1.2.0
 */
public interface ServiceInterceptor {
    /**
     * Extension Point ID of Service interceptors
     */
    String EXTENSION_ID = "net.refractions.udig.catalog.serviceInterceptor"; //$NON-NLS-1$
    
    /**
     * Attribute name of layer created interceptors; called when service is created
     */
    String CREATED_ID = "serviceCreated"; //$NON-NLS-1$
    
    /**
     * Attribute name of service added interceptors, called when service is added to the local repository
     */
    String ADDED_ID = "serviceAdded"; //$NON-NLS-1$
    
    /**
     * Attribute name of service removed interceptors
     */
    String REMOVED_ID = "serviceRemoved"; //$NON-NLS-1$

    /**
     * Performs an action on the service.
     *
     * @param service
     */
    public void run(IService service);
    
}