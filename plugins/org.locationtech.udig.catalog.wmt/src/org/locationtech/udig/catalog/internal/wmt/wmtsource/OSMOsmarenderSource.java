/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt.wmtsource;

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
