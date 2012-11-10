/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.render.internal.wmsc.basic;

/**
 * Indicates the different ways that WMS-C tiles can be cached.
 * @author GDavis
 */
public enum WMSCTileCaching {
    /**
     * In memory only while the application is running, no permanent caching
     */
    INMEMORY,
    /**
     * On disk, all tiles are stored on disk in the set location based on the
     * WMS-C server they were fetched from, the layer name, and the resolution
     */
    ONDISK
    
}

