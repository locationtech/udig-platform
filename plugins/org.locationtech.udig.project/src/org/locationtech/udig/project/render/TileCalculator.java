/**
 * 
 */
package org.locationtech.udig.project.render;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * A helper class which divides an area into tiles or a minimum size. Provides a
 * number of convenience methods.
 * 
 * @author jones
 */
public class TileCalculator {
	private AffineTransform worldToScreen;

	private Dimension tileSize;

	private Envelope bounds;

	private int numXTiles;

	private int numYTiles;

	private Coordinate worldTileSize;

	private Rectangle rect;

	/**
	 * Creates a new instance, the bounds must be set before it is ready to be used.
	 * 
	 * @param worldToScreen the transform from world coordinates to screen coordinates
	 * @param tileSize the size of the tiles to create.
	 */
	public TileCalculator(AffineTransform worldToScreen, Dimension tileSize) {
		this.worldToScreen = worldToScreen;
		this.tileSize = tileSize;
	}

	/**
	 * Creates a new instance.  It is ready to be queried immediately.
	 * 
	 * @param worldToScreen the transform from world coordinates to screen coordinates
	 * @param tileSize the size of the tiles to create.
	 */
	public TileCalculator(AffineTransform worldToScreen, Dimension tileSize, Envelope bounds) {
		this.worldToScreen = worldToScreen;
		this.tileSize = tileSize;
		this.bounds=bounds;
		reset();
	}

	/**
	 * Returns the number of tiles in the X-direction
	 * @return
	 */
	public int numXTiles() {
		validateState();
		return numXTiles;
	}

	private void validateState() {
		if(bounds == null )
			throw new IllegalStateException( "Bounds must be set before TileCalculator may be used"); //$NON-NLS-1$
		if(tileSize == null )
			throw new IllegalStateException( "Tile Size must be set before TileCalculator may be used"); //$NON-NLS-1$
		if(worldToScreen == null )
			throw new IllegalStateException( "World to Screen transform must be set before TileCalculator may be used"); //$NON-NLS-1$
	}

	/**
	 * Returns the number of tiles in the Y-direction
	 * @return
	 */
	public int numYTiles() {
		validateState();
		return numYTiles;
	}

	/**
	 * Return the Envelope (world coordinates) of the tile indexed by x and y. 
	 * @return the Envelope (world coordinates) of the tile indexed by x and y.
	 */
	public Envelope getWorldTile(int x, int y) {
		validateState();
		double xmin = bounds.getMinX()+worldTileSize.x*x;
		double ymin = bounds.getMinY()+worldTileSize.y*y;
		
		return new Envelope( xmin, Math.min(xmin+worldTileSize.x, bounds.getMaxX()),
				ymin, Math.min(ymin+worldTileSize.y, bounds.getMaxY()) );
	}
	
	/**
	 * Return the Rectangle (screen coordinates) of the tile indexed by x and y. 
	 * @return the Rectangle (screen coordinates) of the tile indexed by x and y.
	 */
	public Rectangle getScreenTile(int x, int y){
		validateState();
		int xmin = rect.x+tileSize.width*x;
		int ymin = rect.y+tileSize.height*y;
		int width= Math.min(tileSize.width, (int)rect.getMaxX()-xmin);
		int height = Math.min(tileSize.height, (int)rect.getMaxY()-ymin);
		return new Rectangle(xmin,ymin,width,height);
	}
	
	/**
	 * Calculates where and how many tiles there are.
	 */
	private void reset(){
		double[] points=new double[]{bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY()};
		worldToScreen.transform(points,0,points,0,2);
		rect=new Rectangle((int)points[0], (int)points[1], Math.abs((int)(points[2]-points[0])), Math.abs((int)(points[3]-points[1])) );
		
		numXTiles=(int) Math.ceil(rect.getWidth()/tileSize.getWidth());
		numYTiles=(int) Math.ceil(rect.getHeight()/tileSize.getHeight());
		
		worldTileSize=new Coordinate(bounds.getWidth()/numXTiles, bounds.getHeight()/numYTiles);
	}
	
	Set<Point> randomMap;
	Random random=new Random();
	/**
	 * Resets so that the getRandom() will clear its cache of visited tiles.
	 */
	public void resetRandomizer(){
		validateState();
		randomMap=new HashSet<Point>();
		for( int x=0; x<numXTiles; x++){
			for( int y=0; y<numYTiles; y++){
				randomMap.add(new Point(x,y));
			}
			
		}
	}
	
	
	/**
	 * Gets a random tile in world coordinates.  Each tile is visited only once and after all the tiles
	 * have been visited null will be returned.  
	 * <p><b>WARNING:</b>
	 * If each tile is visited one and only once regardless of whether {@link #getWorldRandom()} or 
	 * {@link #getScreenRandom()}
	 * is used
	 * </p>
	 * 
	 * @return a random tile in world coordinates.
	 */
	public Envelope getWorldRandom(){
		if( randomMap==null ){
			validateState();
			resetRandomizer();
		}
		if( randomMap.isEmpty() ){
			return null;
		}
		int x = random.nextInt(numXTiles);
		int y = random.nextInt(numXTiles);
		Point point=new Point( x,y );
		while (!randomMap.contains(point)){
			x = random.nextInt(numXTiles);
			y = random.nextInt(numXTiles);
			point=new Point( x,y );
		}
		randomMap.remove(point);
		return getWorldTile(x,y);
	}
	
	/**
	 * Gets a random tile in screen coordinates.  Each tile is visited only once and after all the tiles
	 * have been visited null will be returned.  
	 * <p><b>WARNING:</b>
	 * If each tile is visited one and only once regardless of whether {@link #getWorldRandom()} or 
	 * {@link #getScreenRandom()}
	 * is used
	 * </p>
	 * 
	 * @return a random tile in screen coordinates
	 */
	public Rectangle getScreenRandom(){
		if( randomMap==null ){
			validateState();
			resetRandomizer();
		}
		if( randomMap.isEmpty() ){
			return null;
		}
		int x = random.nextInt(numXTiles);
		int y = random.nextInt(numXTiles);
		Point point=new Point( x,y );
		while (!randomMap.contains(point)){
			x = random.nextInt(numXTiles);
			y = random.nextInt(numXTiles);
			point=new Point( x,y );
		}
		randomMap.remove(point);
		return getScreenTile(x,y);
	}
	
	
	
	
	/**
	 * @return Returns the bounds.
	 */
	public Envelope getBounds() {
		return bounds;
	}

	/**
	 * @param bounds The bounds to set.
	 */
	public void setBounds(Envelope bounds) {
		this.bounds = bounds;
		reset();
	}

	/**
	 * @return Returns the tileSize.
	 */
	public Dimension2D getTileSize() {
		return tileSize;
	}

	/**
	 * @param tileSize The tileSize to set.
	 */
	public void setTileSize(Dimension tileSize) {
		this.tileSize = tileSize;
		reset();
	}

	/**
	 * @return Returns the worldToScreen.
	 */
	public AffineTransform getWorldToScreen() {
		return worldToScreen;
	}

	/**
	 * @param worldToScreen The worldToScreen to set.
	 */
	public void setWorldToScreen(AffineTransform worldToScreen) {
		this.worldToScreen = worldToScreen;
		reset();
	}
	
	
}
