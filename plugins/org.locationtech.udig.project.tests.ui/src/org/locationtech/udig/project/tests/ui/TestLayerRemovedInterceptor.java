/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests.ui;

import org.junit.Ignore;

import org.locationtech.udig.project.interceptor.LayerInterceptor;
import org.locationtech.udig.project.internal.Layer;

/**
 * Prints a message when a layer is removed
 * 
 * @author Jesse
 * @since 1.1.0
 */
@Ignore
public class TestLayerRemovedInterceptor implements LayerInterceptor {

    public void run( Layer layer ) {
        System.out.println(layer.getName()+" has been removed from a map. This is a test interceptor. "); //$NON-NLS-1$
    }

}
