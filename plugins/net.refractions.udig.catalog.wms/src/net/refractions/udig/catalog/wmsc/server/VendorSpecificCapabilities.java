/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.catalog.wmsc.server;

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
