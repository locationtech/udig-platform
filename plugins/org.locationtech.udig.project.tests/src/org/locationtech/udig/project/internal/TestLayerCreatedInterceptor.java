/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal;

import org.junit.Ignore;
import org.locationtech.udig.project.interceptor.LayerInterceptor;

@Ignore
public class TestLayerCreatedInterceptor implements LayerInterceptor {
    public static Layer layerCreated;

    public void run( Layer layer ) {
        layerCreated=layer;
//        System.out.println(layer.getName()+" has been created. This is a test interceptor. "); //$NON-NLS-1$
    }
}
