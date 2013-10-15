/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
