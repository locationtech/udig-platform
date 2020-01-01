/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2007-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.internal.PreferenceConstants;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.ows.AbstractOpenWebService;

import org.locationtech.jts.geom.Envelope;

/**
 * A TileRangeOnDisk represents a set of Tiles in a given bounds and stores them on disk.  
 * This TileRange is responsible for fetching the individual tiles if they are not yet
 * "complete" and ready to paint, which it does by first checking if they are stored on
 * disk, and then through a TiledWebMapServer.  The  TileRange really only lives for the 
 * cycle of fetching all the tiles.  Once then TileRange has finished loading (when all the 
 * tiles are complete), its use is pretty much done. 
 * 
 * Every time a tile is "completed", all listeners will be notified.
 * 
 * @author GDavis
 * @since 1.2.0
 */
public class TileRangeOnDisk extends AbstractTileRange {

	private TileImageReadWriter tileReadWriter;
	private TileWorkerQueue writeTileWorkQueue;
	/**
	 * TileRange implementation that saves tiles out to disk.
	 * 
     * @param server The Server to fetch the tiles from
     * @param tileset TileSet of rendered content
     * @param bounds Optional bounds (may be null) that contain the provided tiles
     * @param tiles The tiles that we wish to fetch; must be from the provided tileset
     * @param requestTileWorkQueue Queue of worker threads that can be used to fetch tiles
     * @param writeTileWorkQueue Queue used to write tiles out to disk
	 */
	public TileRangeOnDisk(AbstractOpenWebService<?,?> server, TileSet tileset,
			Envelope bounds, Map<String, Tile> tiles, TileWorkerQueue requestTileWorkQueue,
			TileWorkerQueue writeTileWorkQueue) {
		this(server, tileset, bounds, tiles, requestTileWorkQueue, writeTileWorkQueue,
                new TileImageReadWriter(server, CatalogPlugin.getDefault().getPreferenceStore()
                        .getString(PreferenceConstants.P_WMSCTILE_DISKDIR)));
	}
    
    /**
     * This constructor allows you to use your own
     * implementation of TileImageReadWriter.
     * 
     * @param server
     * @param tileset
     * @param bounds
     * @param tiles
     * @param requestTileWorkQueue
     * @param writeTileWorkQueue
     * @param tileImageReadWriter
     */
    public TileRangeOnDisk(AbstractOpenWebService<?,?> server, TileSet tileset,
            Envelope bounds, Map<String, Tile> tiles, TileWorkerQueue requestTileWorkQueue,
            TileWorkerQueue writeTileWorkQueue, TileImageReadWriter tileImageReadWriter) {
        super(server, tileset, bounds, tiles, requestTileWorkQueue);
        
        if (writeTileWorkQueue == null) {
            using_threadpools = false;
            //this.writeTileWorkQueue = new TileWorkerQueue(TileWorkerQueue.defaultWorkingQueueSize);
        }
        else {
            this.writeTileWorkQueue = writeTileWorkQueue;
        }       
                
        tileReadWriter = tileImageReadWriter;
        
        // the super's constructor will have built the list of tiles not loaded, so
        // now remove any tiles that we can find already on disk from before
        checkDiskForLoadedTiles();
    }
    
	/**
	 * Check the disk for any tiles that have not yet been loaded, load them, and
	 * remove them from the not-loaded list
	 */
	private void checkDiskForLoadedTiles() {
        try {
            tilesWaitingToLoad_lock.writeLock().lock();      	
		
			String filetype = getFileType();
			Map<String, Tile> tilesToRemove = new HashMap<String, Tile>();
	        for( Iterator<Entry<String, Tile>> iterator = tilesWaitingToLoad.entrySet().iterator(); iterator.hasNext(); ) {
	            Entry<String, Tile> tileentry = (Entry<String, Tile>) iterator.next();
	            Tile tile = tileentry.getValue();
	            if (tileReadWriter.tileFileExists(tile, filetype) && !tileReadWriter.isTileStale(tile, filetype)) {
	            	boolean success = tileReadWriter.readTile(tile, filetype);
	            	if (success) {
	            		tilesToRemove.put(tileentry.getKey(), tile);
	            	}
	            }
	        }
	        // Remove any tiles we were able to load
	        for( Iterator<Entry<String, Tile>> iterator = tilesToRemove.entrySet().iterator(); iterator.hasNext(); ) {
	            Entry<String, Tile> tileentry = (Entry<String, Tile>) iterator.next();
	            tilesWaitingToLoad.remove(tileentry.getKey());
	        }
	        
        } finally {
            // unlock the write lock
            tilesWaitingToLoad_lock.writeLock().unlock();
        }	
	}

	/**
	 * Try caching the tile on disk if it is valid (ie: not in error)
	 */
	public void cacheTile(Tile tile) {
		if (tile.getTileState() != WMSTile.INERROR) {
			saveTileToDisk(tile, new NullProgressMonitor());
		}
	}
	
    /**
     * This method will attempt to save a Tile's image to disk.  It checks if
     * thread pools are being used, if not it will create
     * a single thread and use it.
     * 
     * @param tile
     * @param monitor
     * @throws Exception
     */
    protected void saveTileToDisk( final Tile tile, final IProgressMonitor monitor ) {

    	if (using_threadpools) {
	    	Runnable r = new Runnable() {
	            public void run() {
	            	internalSaveTileToDisk(tile, monitor);
	            }
	        };
	        writeTileWorkQueue.execute(r);
    	}
    	else {
    		Thread t = new Thread() {
    			@Override
    			public void run() {
    				internalSaveTileToDisk(tile, monitor);
    			}
    		};
    		t.start();
    	}    	
    }   
    
    /**
     * Do the work of saving a tile to disk
     * 
     * @param tile
     * @param monitor
     */
    private void internalSaveTileToDisk(final Tile tile, final IProgressMonitor monitor) {
        // get a lock on the tile and only write it to disk if it has a valid
    	// buffered image and it is NOT in error
        Object lock = tile.getTileLock();
        if (testing) {
            System.out.println("getting lock for disk write: "+tile.getId()); //$NON-NLS-1$
        }
        synchronized (lock) {
            if (testing) {
                System.out.println("got lock for disk write: "+tile.getId()); //$NON-NLS-1$
            }
            // try to write the tile's image to disk
            if (tile.getBufferedImage() != null && tile.getTileState() != WMSTile.INERROR) {
            	String filetype = getFileType();
            	try {
            		tileReadWriter.writeTile(tile, filetype);
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            
        } // end synchronized block
        
        if (testing) {
            System.out.println("REMOVING lock for disk write: "+tile.getId()); //$NON-NLS-1$
        }
    }
    
    /**
     * Get the file type of the tiles (this is in the format of png, or jpg, etc)
     * 
     * @return
     */
    protected String getFileType() {
    	// format is like "image\png" so strip out the beginning
    	String format = tileset.getFormat();
    	// remove any numbers from the format (ie: png8, remove 8)
    	format = format.replaceAll("\\d+", "");  //$NON-NLS-1$ //$NON-NLS-2$
    	
    	int indexOf = format.indexOf("\\"); //$NON-NLS-1$
    	if (indexOf < 0) {
    		indexOf = format.indexOf("/"); //$NON-NLS-1$
    		if (indexOf < 0) {
    			return format;
    		}
    	}
    	if (indexOf >= (format.length()-1) ) {
    		return format;
    	}
    	return format.substring(indexOf+1);
    }	

}
