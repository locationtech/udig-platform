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


import net.refractions.udig.project.internal.Map;

/**
 * An interceptor that is ran on a map.  The net.refractions.udig.mapInterceptor extension point has more information.
 * 
 * @author Jesse
 */
public interface MapInterceptor {
    static final String MAP_INTERCEPTOR_EXTENSIONPOINT = "net.refractions.udig.project.mapInterceptor"; //$NON-NLS-1$

    /**
     * Performs an action on or with the intercepted map.
     *
     * @param map
     */
    public void run(Map map);
}
