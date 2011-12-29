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
package net.refractions.udig.project.internal;

import org.junit.Ignore;

import net.refractions.udig.project.interceptor.LayerInterceptor;

/**
 * Prints a message when a layer is added to a map.
 * 
 * @author Jesse
 * @since 1.1.0
 */
@Ignore
public class TestLayerAddedInterceptor implements LayerInterceptor {

    public static Layer layerAdded;

    public void run( Layer layer ) {
        layerAdded=layer;
//        System.out.println(layer.getName()+" has been added to map. This is a layer test interceptor. "); //$NON-NLS-1$
    }

}
