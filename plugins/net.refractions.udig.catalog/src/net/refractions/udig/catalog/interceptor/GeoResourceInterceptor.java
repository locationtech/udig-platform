/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
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
package net.refractions.udig.catalog.interceptor;

import net.refractions.udig.catalog.IGeoResource;

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