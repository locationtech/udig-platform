/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.interceptor;

import org.locationtech.udig.catalog.IGeoResource;

/**
 * An interceptor that is ran on a GeoResource.
 * 
 * @author Jody
 * @since 1.2.0
 */
public interface GeoResourceInterceptor {
    /**
     * Attribute name of service added interceptors, called when service is added to the local repository
     */
    String ADDED_ID = "resourceAdded"; //$NON-NLS-1$
    
    /**
     * Attribute name of service removed interceptors
     */
    String REMOVED_ID = "resourceRemoved"; //$NON-NLS-1$

    /**
     * Performs an action on the layer.
     *
     * @param layer 
     */
    public void run(IGeoResource resource);
    
}
