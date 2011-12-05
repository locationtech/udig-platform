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
package net.refractions.udig.project.tests.ui;

import org.junit.Ignore;

import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;

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
