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
package net.refractions.udig.catalog.internal.wmt.tile;

import java.io.File;

import net.refractions.udig.catalog.wmsc.server.Tile;
import net.refractions.udig.catalog.wmsc.server.TileImageReadWriter;

public class WMTTileImageReadWriter extends TileImageReadWriter {
    private String baseDir;
    public WMTTileImageReadWriter(String baseDir) {
        super(null, baseDir);
        
        this.baseDir = WMTTileImageReadWriter.pathCombine(baseDir, "wmt-tile-cache"); //$NON-NLS-1$
    }
    
    @Override
    public String getTileDirectoryPath(Tile tile) {
        if (tile instanceof WMTTile)
            return getTileDirectoryPath((WMTTile) tile);
        else
            return super.getTileDirectoryPath(tile);
        
    }

    public String getTileDirectoryPath(WMTTile tile) {        
        return WMTTileImageReadWriter.pathCombine(baseDir, tile.getReleatedSourceId());
    }
    
    /**
     * Fetch the name of the file for the given tile
     * 
     * @param tile
     * @param filetype
     * @return
     */
    @Override
    public String getTileFileName(Tile tile, String filetype) {
        return WMTTileImageReadWriter.pathCombine(getTileDirectoryPath(tile),
                tile.getPosition() + "." + filetype); //$NON-NLS-1$
    }
    
    /**
     * Concatenates two paths.
     *
     * @param baseDir
     * @param subDir
     * @return
     */
    public static String pathCombine(String baseDir, String subDir) {
        File base = new File(baseDir);
        File sub = new File(base, subDir);
        
        return sub.getPath();
    }
}
