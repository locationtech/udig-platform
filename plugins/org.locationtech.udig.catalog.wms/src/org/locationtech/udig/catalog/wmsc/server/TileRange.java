/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import org.locationtech.jts.geom.Envelope;

/**
 * A TileRange represents a set of Tiles that can be fetched.  A TileRange
 * is responsible for fetching the individual tiles if they are not yet
 * "complete" and ready to paint. 
 * 
 * Every time a tile is "completed", all listeners will be notified.
 *  
 * An example of two different implementations could be one that caches the tiles
 * in memory and another that caches them on disk. 
 * 
 * A TileRange is often used to represent the Tiles that are required to draw
 * on the screen or are required by an offline process.
 * 
 * @author GDavis
 *
 */
public interface TileRange {
    
    /**
     * This is the entry point of this class once it has been created.  It will
     * ensure all the tiles are loaded for the range. 
     * 
     * @param monitor
     */
    public void loadTiles(IProgressMonitor monitor);
    
    /**
     * Fetch a tile by its tileid from the range
     * 
     * @param tileId
     * @return tile
     */
    public Tile getTile(String tileId);
    
    /**
     * Returns the tiles in the given range as an unmodifiable
     * map.
     *
     * @return
     */
    public Map<String, Tile> getTiles();
    
    /**
     * Get the total count of tiles in this range.
     * 
     * @return count
     */
    public int getTileCount();
    
    /**
     * Returns whether every tile in the range is complete and ready
     * to paint.
     *
     * @return boolean
     */
    public boolean isComplete();
    
    /**
     * Fetch the bounds of the entire range.
     * 
     * @return bounds
     */
    public Envelope getRangeBounds();
    
    /**
     * Add a listener to be notified when tiles are completed.
     * 
     * @param listener
     */
    public void addListener(TileListener listener);
    
    /**
     * Remove a TileListener
     * 
     * @param listener
     */    
    public void removeListener(TileListener listener);
    
    /**
     * Cache the given tile in the form that is setup (in memory, on disk, etc).
     * 
     * @param tile
     */
    public void cacheTile(Tile tile);    
    
    /**
     * Notify any listeners and anyone else that the given tile has just been loaded without
     * blocking or locking on anything (this should be a quick call to notify anyone that is
     * listening).
     * 
     * @param tile
     */
    public void tileLoaded( Tile tile );
    
    /**
     * Used during shutdown to remove listeners and other things.
     */
    public void dispose();
    
}
