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

