/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.wmt.wmtsource;

import java.util.Arrays;


public class OSMOsmarenderSource extends OSMSource {
    public static String NAME = "Osmarender"; //$NON-NLS-1$
    
    protected OSMOsmarenderSource() {
        setName(NAME); 
    }

    @Override
    public String getBaseUrl() {
        return "http://tah.openstreetmap.org/Tiles/tile/"; //$NON-NLS-1$
    }

    //region Zoom-level
    // zoom-level range for Osmarender
    private static double[] scaleList = Arrays.copyOfRange(OSMSource.scaleList, 0, 18);
    
    /**
     * As Osmarender has only tiles from zoom-level 0-17, 
     * we have to return a stripped-down mapping list.
     * 
     * see: http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Tiles
     *
     * @return mapping between OSM zoom-levels and map scale
     */
    @Override
    public double[] getScaleList() {
        return OSMOsmarenderSource.scaleList;
    }
    //endregion
    

}
