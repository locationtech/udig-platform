/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */

package org.locationtech.udig.catalog.wmsc.server;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.collections.MapUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.ows.AbstractOpenWebService;

import org.locationtech.jts.geom.Envelope;

/**
 * An AbstractTileRange represents a set of Tiles in a given bounds.  
 * This TileRange is responsible for fetching the individual tiles if they are not yet
 * "complete" and ready to paint, which it does through a TiledWebMapServer.  The 
 * TileRange really only lives for the cycle of fetching all the tiles.  Once the 
 * TileRange has finished loading (when all the tiles are complete), its use is 
 * pretty much done.
 * 
 * Every time a tile is "completed", all listeners will be notified.
 * 
 * @author GDavis
 * @since 1.2.0
 */
public abstract class AbstractTileRange implements TileRange {

    protected Map<String, Tile> allTiles;
    protected Envelope bounds;
    protected AbstractOpenWebService<?,?> server;
    protected TileSet tileset;
    protected TileWorkerQueue requestTileWorkQueue; // queue of threads for requesting tiles
    protected boolean using_threadpools = false;  // set in the constructor
    
    protected final static boolean testing = false;  // for testing output
 
    /** keep track of all tile listeners **/
    protected Set<TileListener> tileListeners = new HashSet<TileListener>();    
    
    /**
     * This lock controls access to tilesWaitingToLoad.
     */
    protected ReentrantReadWriteLock tilesWaitingToLoad_lock = new ReentrantReadWriteLock();
    
    /**
     * Map of tiles that are not yet loaded; may be empty.
     */
    protected volatile Map<String, Tile> tilesWaitingToLoad = new HashMap<String, Tile>();
     
    /**
     * A TileRange implementation where you can fill in the fetching protocol.
     * @param server The Server to fetch the tiles from
     * @param tileset TileSet of rendered content
     * @param bounds Optional bounds (may be null) that contain the provided tiles
     * @param tiles The tiles that we wish to fetch; must be from the provided tileset
     * @param requestTileWorkQueue Queue of worker threads that can be used to fetch tiles
     */
    public AbstractTileRange(AbstractOpenWebService<?,?> server, TileSet tileset, Envelope bounds, 
            Map<String, Tile> tiles, TileWorkerQueue requestTileWorkQueue) {
        this.server = server;
        this.tileset = tileset;
        if (tiles != null) {
            this.allTiles = tiles;
        }
        else {
            this.allTiles = new HashMap<String, Tile>();
        }
        if (bounds != null) {
            this.bounds = bounds;
        }
        else {
            this.bounds = calculateBounds();
        }
		if (requestTileWorkQueue == null) {
			using_threadpools = false;
			//this.requestTileWorkQueue = new TileWorkerQueue(TileWorkerQueue.defaultWorkingQueueSize);
		}
		else {
			using_threadpools = true;
			this.requestTileWorkQueue = requestTileWorkQueue;
		}        
        
        // calculate what tiles are not yet loaded
        setTilesNotLoaded();
    }
    
    /**
     * Go through every tile and pick out the ones that are not yet loaded and set
     * them in the notLoaded list.
     */
    protected void setTilesNotLoaded() {
        try {
            tilesWaitingToLoad_lock.writeLock().lock();      	
	        for( Iterator<Entry<String, Tile>> iterator = allTiles.entrySet().iterator(); iterator.hasNext(); ) {
	            Entry<String, Tile> tileentry = (Entry<String, Tile>) iterator.next();
	            if (tileentry.getValue().getBufferedImage() == null){
	                tilesWaitingToLoad.put(tileentry.getKey(), tileentry.getValue());
	            }   
	        }
        } finally {
            // unlock the write lock
            tilesWaitingToLoad_lock.writeLock().unlock();
        }	        
    }

    /**
     * This is the entry point of this class once it has been created.  It will
     * ensure all the tiles are loaded.  If a tile is not yet loaded it will
     * create a request thread to fetch it, blocking until all the tiles are loaded.
     * 
     * @param monitor
     */
    public void loadTiles(IProgressMonitor monitor) {            
        // load each tile not yet loaded.  Lock on the list of
        // tiles to load.
        Set<String> removeTiles = new HashSet<String>();     
        try {
            tilesWaitingToLoad_lock.writeLock().lock();  
            // now that we have a lock, check if this has been canceled while waiting
            if (monitor.isCanceled()) {
                dispose();
                return;
            }
            
            Set<Entry<String, Tile>> entrySet = tilesWaitingToLoad.entrySet();
            for( Entry<String, Tile> set : entrySet ) {
                String tileid = set.getKey();
                Tile tile = set.getValue();
                
                // only send a request to get the tile if we don't have it already
                if (tile.getBufferedImage() == null) {
                    try {
                        loadTile(tile, new NullProgressMonitor());
                    } catch (Exception e) {
                        // there was some error getting the tile, so instead of blocking
                        // forever because of the error, set the tile to be removed
                    	// from the loading list and continue.
                        e.printStackTrace();
                        removeTiles.add(tileid);
                    }
                }else{
                    //we already have this image; somehow it was filled in for us between when we first
                    //asked for tiles and now.  So we need to notify any listeners 
                    tileLoaded(tile);
                    removeTiles.add(tileid); // set tile to be removed from loading list
                }
            }         
            // remove all successfully completed tiles and any error tiles from the list 
            // of loading tiles to prevent endless blocking.
            for (String key : removeTiles) {
                tilesWaitingToLoad.remove(key);
            }
        } finally {
            // unlock the write lock
            // until this is released any jobs would get stuck waiting to update it
            tilesWaitingToLoad_lock.writeLock().unlock();
        }
    }
    
    /**
     * remove all listeners and do any other cleanup
     */
    public void dispose() {
        // remove all listeners
        for (TileListener listener : tileListeners) {
            removeListener(listener);
        }
    }
    
    /**
     * Notify any listeners and update the tile list that the given 
     * tile is now loaded
     *
     * @param tile
     */
    public void tileLoaded( Tile tile ) {
        // notify any listeners that the tile is ready
        notifyListenersTileReady(tile);
    } 

    /**
     * Remove the given tile from the loading list and ensure the lock is obtained before
     * modifying it.
     * 
     * @return
     */
    protected void removeTileFromLoadingList( Tile tile) {
    	// get a write lock on the tile loading list to remove this tile
        // from the loading list
        try {
        	tilesWaitingToLoad_lock.writeLock().lock();
        	tilesWaitingToLoad.remove(tile.getId());
        } finally {
        	tilesWaitingToLoad_lock.writeLock().unlock();
        }      	
    }

    protected Envelope calculateBounds() {
        Envelope newBounds = new Envelope();
        for (String key : allTiles.keySet()) {
            newBounds.expandToInclude(allTiles.get(key).getBounds());
        }
        return newBounds;
    }
    
    public Tile getTile(String tileId) {
        if (tileId == null) {
            return null;
        }
        return allTiles.get(tileId);
    }
    
    /**
     * Returns the tiles in the given range as an unmodifiable
     * map.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Tile> getTiles() {
        return MapUtils.unmodifiableMap(allTiles);
    }    
    
    public int getTileCount() {
        return allTiles.keySet().size();
    }
    
    /**
     * Returns whether every tile in the range is complete and ready
     * to paint
     *
     * @return boolean
     */
    public boolean isComplete() {
        for( Iterator<Entry<String, Tile>> iterator = allTiles.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<String, Tile> value = (Entry<String, Tile>) iterator.next();
            if (value.getValue().getBufferedImage() == null){
                return false;
            }
        }
        return true;
    }
    
    public Envelope getRangeBounds() {
        return bounds;
    }
    
    /**
     * This method will see if we are using thread pools and either create a runnable 
     * to load the tile using the pool or create a single thread to do it.  It will
     * lock on the tile.  
     * 
     * @param tile
     * @param monitor
     * @throws Exception
     */
    protected void loadTile( final Tile tile, final IProgressMonitor monitor ) throws Exception {

    	if (using_threadpools) {
	    	Runnable r = new Runnable() {
	            public void run() {
	            	internalLoadTile(tile, monitor);
	            }
	        };
	        requestTileWorkQueue.execute(r);
    	}
    	else {
    		Thread t = new Thread() {
    			@Override
    			public void run() {
    				internalLoadTile(tile, monitor);
    			}
    		};
    		t.start();
    	}
    }
    
    /**
     * Do the work of loading a tile
     * 
     * @param tile
     * @param monitor
     */
    private void internalLoadTile(final Tile tile, final IProgressMonitor monitor) {
    	tile.loadTile(monitor);

        // now that the tile lock is unlocked, notify any listeners and
        // update the not loaded list (we want to do this whether we got
        // the tile or not because we don't want the blocking queue
        // in the rendering to wait blocked forever for missing tiles)
        tileLoaded(tile); 
        removeTileFromLoadingList(tile);
        cacheTile(tile);  // cache the tile with whatever method is setup
    }
    
    protected BufferedImage createErrorImage(){
        BufferedImage bf = new BufferedImage(tileset.getWidth(), tileset.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bf.createGraphics();
        g.setColor(Color.RED);
        g.drawLine(0, 0, tileset.getWidth(), tileset.getHeight());
        g.drawLine(0, tileset.getHeight(), tileset.getWidth(), 0);
        return bf;
    }
    
    /**
     * Notify all tile listeners that a tile can now be drawn
     *
     * @param tile to draw
     */
    protected void notifyListenersTileReady( Tile tile ) {
        for (TileListener listener : tileListeners) {
            listener.notifyTileReady(tile);
        }
    }    
    
    public void addListener(TileListener listener) {
        tileListeners.add(listener);
    }
    
    public void removeListener(TileListener listener) {
        tileListeners.remove(listener);
    }       

}
