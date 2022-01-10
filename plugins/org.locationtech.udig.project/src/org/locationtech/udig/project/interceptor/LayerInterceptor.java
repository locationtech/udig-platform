/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.interceptor;

import org.locationtech.udig.project.internal.Layer;

/**
 * An interceptor that is ran on a layer. See the org.locationtech.udig.mapInterceptor extension
 * point for more details.
 *
 * @author Jessegit
 * @since 1.1.0
 */
public interface LayerInterceptor {
    /**
     * Extension Point ID of Layer interceptors
     */
    String EXTENSION_ID = "org.locationtech.udig.project.layerInterceptor"; //$NON-NLS-1$

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
