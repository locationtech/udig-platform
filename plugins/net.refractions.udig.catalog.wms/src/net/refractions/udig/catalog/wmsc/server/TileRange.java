/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.wmsc.server;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

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
