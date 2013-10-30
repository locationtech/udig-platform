/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */

package org.locationtech.udig.project.interceptor;


import org.locationtech.udig.project.internal.Map;

/**
 * An interceptor that is ran on a map.  The org.locationtech.udig.mapInterceptor extension point has more information.
 * 
 * @author Jesse
 */
public interface MapInterceptor {
    static final String MAP_INTERCEPTOR_EXTENSIONPOINT = "org.locationtech.udig.project.mapInterceptor"; //$NON-NLS-1$

    public static final String CLOSING_ID = "mapClosing";
    
    public static final String OPENING_ID = "mapOpening";
    /**
     * Performs an action on or with the intercepted map.
     *
     * @param map
     */
    public void run(Map map);
}
