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
package net.refractions.udig.project.interceptor;

import net.refractions.udig.project.internal.Layer;

/**
 * An interceptor that is ran on a layer.  See the net.refractions.udig.mapInterceptor extension point for more details.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface LayerInterceptor {

    /**
     * Extension Point ID of Layer interceptors
     */
    String EXTENSION_ID = "net.refractions.udig.project.layerInterceptor"; //$NON-NLS-1$
    /**
     * Attribute name of layer created interceptors
     */
    String CREATED_ID = "layerCreated"; //$NON-NLS-1$
    /**
     * Attribute name of layer added interceptors
     */
    String ADDED_ID = "layerAdded"; //$NON-NLS-1$
    /**
     * Attribute name of layer removed interceptors
     */
    String REMOVED_ID = "layerRemoved"; //$NON-NLS-1$

    /**
     * Performs an action on the layer.
     *
     * @param layer 
     */
    public void run(Layer layer);
}
