/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 20072011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.geotools.data.ows.AbstractOpenWebService;

/**
 * Class to help read and write for Tile images to disk in a structured format that can be tested
 * and loaded each time the program is run.
 * 
 * @author GDavis
 */
public class TileImageReadWriter {

    private String baseTileFolder = ""; //$NON-NLS-1$
    private static final String baseSubTileFolder = "tilecache"; //$NON-NLS-1$
    private URL server;

    public TileImageReadWriter( AbstractOpenWebService< ? , ? > service, String baseDir ) {
        try {
            server = service.getInfo().getSource().toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.baseTileFolder = baseDir + File.separator + baseSubTileFolder;
    }

    /**
     * Get a file representing the given tile on disk. Will create the file and any parent
     * directories if they don't yet exist.
     * 
     * @param tile
     * @param filetype
     * @return
     */
    public File getTileFile( Tile tile, String filetype ) {
        String filename = getTileFileName(tile, filetype);
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        // System.out.println("file: "+file.getAbsolutePath());
        return file;
    }

    /**
     * Fetch the name of the file for the given tile
     * 
     * @param tile
     * @param filetype
     * @return
     */
    public String getTileFileName( Tile tile, String filetype ) {
        return getTileDirectoryPath(tile) + tile.getPosition() + "." + filetype; //$NON-NLS-1$
    }

    /**
     * Fetch the directory path for the given tile. Tile directory structure is as follow: <tile
     * folder>\<server>\<layer names>_<EPSG code>_<image format>\<scale>\
     * 
     * @param tile
     * @return
     */
    public String getTileDirectoryPath( Tile tile ) {
        String serverURL = this.server.getHost() + "_" + this.server.getPath(); //$NON-NLS-1$
        serverURL = serverURL.replace('\\', '_');
        serverURL = serverURL.replace('/', '_');
        String layers = tile.getTileSet().getLayers();
        layers += "_" + tile.getTileSet().getEPSGCode();//$NON-NLS-1$
        layers += "_" + tile.getTileSet().getFormat(); //$NON-NLS-1$
        layers = layers.replace(',', '_');
        layers = layers.replace(':', '_');
        layers = layers.replace('\\', '_');
        layers = layers.replace('/', '_');
        layers = layers.replace(File.separator, "_"); //$NON-NLS-1$
        Double scale = tile.getScale();
        String scaleStr = scale.toString();
        scaleStr = scaleStr.replace('.', '_');
        return baseTileFolder + File.separator + serverURL + File.separator + layers
                + File.separator + scaleStr + File.separator;
    }

    /**
     * Attempt to write the tile to file
     * 
     * @param tile
     * @param filetype
     * @return true on success
     */
    public boolean writeTile( Tile tile, String filetype ) {
        try {
            // lock on the tile so we aren't trying to read it as it is being written to
            Object lock = tile.getTileLock();
            synchronized (lock) {
                // write the image out
                ImageIO.write(tile.getBufferedImage(), filetype, getTileFile(tile, filetype));
                // write the updated metadata file out
                writeTileMetadataFile(tile);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Writes the update sequence file for the given tile
     * 
     * @param tile
     * @throws IOException
     */
    public void writeTileMetadataFile( final Tile tile ) throws IOException {
        if (tile.getMaxCacheAge() != null && !"".equals(tile.getMaxCacheAge())) { //$NON-NLS-1$
            FileUtils.writeStringToFile(new File(getCacheFilename(tile)), tile.getMaxCacheAge());
        }
    }

    /**
     * Check if the given tile exists on disk already
     * 
     * @param tile
     * @return does file already exist
     */
    public boolean tileFileExists( Tile tile, String filetype ) {
        String filename = getTileFileName(tile, filetype);
        File file = new File(filename);
        return file.exists();
    }

    /**
     * Check the tiles last modified time + max cache time v.s. the current time. If its before the
     * current time the tile is considered out-dated and should be pulled from the server.
     * 
     * @param tile The Tile Object
     * @param tileFile the tile image File
     * @return true if the tile is considered out-dated, false otherwise
     */
    public boolean isTileStale( Tile tile, String filetype ) {
        String filename = getTileFileName(tile, filetype);
        File tileFile = new File(filename);

        String cacheFilename = getCacheFilename(tile);
        File cacheFile = new File(cacheFilename);
        if (cacheFile.exists()) {

            try {
                String persistedCacheAge = FileUtils.readFileToString(cacheFile);
                if (persistedCacheAge != null && !"".equals(persistedCacheAge)) { //$NON-NLS-1$
                    Long cacheTimeLong = Long.parseLong(persistedCacheAge);
                    Date cacheTime = new Date(tileFile.lastModified() + (cacheTimeLong * 1000));
                    if (cacheTime.before(new Date(System.currentTimeMillis()))) {
                        return true;
                    }
                } else {
                    /*
                     * if we get to here the cache file might have been written without a max cache
                     * age, or corrupted; lets assume the tile is stale
                     */
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Get the name of the cache max age file
     * 
     * @param tile The Tile object
     * @return String name of the tiles cache file
     */
    public String getCacheFilename( final Tile tile ) {
        return getTileDirectoryPath(tile) + tile.getPosition() + ".txt"; //$NON-NLS-1$
    }

    /**
     * Attempt to read given tile's image if it exists, and store it in the tile's bufferedImage.
     * 
     * @param tile
     * @param filetype
     * @return true on success
     */
    public boolean readTile( Tile tile, String filetype ) {
        BufferedInputStream bis = null;
        BufferedImage image = null;
        try {
            // lock on the tile so we aren't trying to write to it as it is being read
            Object lock = tile.getTileLock();
            synchronized (lock) {
                bis = new BufferedInputStream(new FileInputStream(getTileFile(tile, filetype)));
                image = ImageIO.read(bis);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (image != null) {
            tile.setBufferedImage(image);
            return true;
        }
        return false;
    }

    /**
     * Clear the entire tile cache for for the base dir of these tiles
     * 
     * @return true on success
     */
    public boolean clearCache() {
        File file = new File(baseTileFolder);
        return deleteDir(file);
    }

    /**
     * Recursively deletes all subdirs and files of a directory and then deletes the given
     * directory. Returns false at the moment any attempts to delete fail.
     * 
     * @param dir
     * @return true on success
     */
    private static boolean deleteDir( File dir ) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for( int i = 0; i < children.length; i++ ) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

}
