/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

import java.util.ArrayList;

/**
 * Represents a VendorSpecificCapabilities element of a WMSC getCapabilities request.
 * <p>
 * http://wiki.osgeo.org/wiki/WMS_Tiling_Client_Recommendation#GetCapabilities_Responses
 * </p>
 * @author Emily Gouge (Refractions Reserach, Inc)
 * @since 1.1.0
 */
public class VendorSpecificCapabilities {

    /** Collection of tilesets */
    private ArrayList<WMSTileSet> tiles;

    /**
     * Creates a new element
     */
    public VendorSpecificCapabilities() {
        tiles = new ArrayList<WMSTileSet>();
    }
    /**
     * Add a tile to the tile set
     * 
     * @param ts
     */
    public void addTile( WMSTileSet ts ) {
        tiles.add(ts);
    }

    /**
     * @return all tiles in the tileset
     */
    public ArrayList<WMSTileSet> getTiles() {
        return tiles;
    }
}
