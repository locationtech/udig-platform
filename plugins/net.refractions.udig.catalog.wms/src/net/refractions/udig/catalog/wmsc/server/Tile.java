package net.refractions.udig.catalog.wmsc.server;

import java.awt.image.BufferedImage;

import org.eclipse.core.runtime.IProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Represents one tile in a tileset.  Has a unique id, bounds, scale and
 * a BufferedImage.  All other data such as the format, crs, etc are
 * stored as part of the tileset.
 * 
 * @author GDavis
 * @since 1.2.0
 */
public interface Tile extends Comparable<Tile> {

	public abstract String getId();

	public abstract Envelope getBounds();

	public abstract double getScale();

	public abstract void setBufferedImage(BufferedImage im);

	public abstract BufferedImage getBufferedImage();
	
	public TileSet getTileSet();
	
	/**
	 * Get the tile's lock object so outside callers can lock on it
	 * 
	 * @return this tile's lock object
	 */
	public Object getTileLock();

	public abstract int getTileState();

	public abstract void setTileState(int state);

	/**
	 * Position is the x/y position of the tile within the tile range grid for its scale.
	 * Position is stored as a String in the format "x_y", eg:  0_3 is the first tile on the
	 * left and the 4th one down from the top (0_0 is the top left).
	 */
	public abstract void setPosition(String pos);

	public abstract String getPosition();

	/**
	 * Load the tile from whatever source it comes from (ie WMS server).  Because locking 
	 * and blocking is going on with the callers, we want to return true if the tile 
	 * successfully loads or if the tile loaded a place holder image (only return false if 
	 * the image is in a failed state).
	 * @param monitor
	 * 
	 * @return true on successful load of image or successful load of place holder
	 */
	public abstract boolean loadTile(IProgressMonitor monitor);

}