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

import java.util.List;
import java.util.Map;

import org.geotools.data.ows.AbstractOpenWebService;
import org.geotools.ows.wms.CRSEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

/**
 * A TileSet represents a group of tiles that belong together (ie: they are all from
 * the same WMS server and layer, etc).  
 *  
 * @author Graham Davis (Refractions Research, Inc.)
 * @since 1.2.0
 */
public interface TileSet {

	/**
	 * Sets the tile set epsg code and computes a corresponding coordinate reference system
	 * 
	 * @param epsg
	 */
	public void setCoorindateReferenceSystem(String epsg);

	/**
	 * @return the tileset coordinate system
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem();

	/**
	 * Sets the tile bounding box
	 * 
	 * @param bbox
	 */
	public void setBoundingBox(CRSEnvelope bbox);

	/**
	 * Sets the tile widths
	 * 
	 * @param width
	 */
	public void setWidth(int width);

	/**
	 * Sets the tile styles
	 * 
	 * @param styles
	 */
	public void setStyles(String styles);

	/**
	 * Sets the tile heights
	 * 
	 * @param height
	 */
	public void setHeight(int height);

	/**
	 * Sets the format
	 * 
	 * @param format
	 */
	public void setFormat(String format);

	/**
	 *Sets the tile set layers
	 * 
	 * @param layers
	 */
	public void setLayers(String layers);

	/**
	 * Sets the resolutions supported by the tile set.
	 * 
	 * @param res A space-separate string representing all the zoom levels (for example
	 *        "0.5 0.25 0.125")
	 */
	public void setResolutions(String res);

	/**
	 * Sets the server for this tileset
	 * @param server
	 */
	public void setServer(AbstractOpenWebService<?,?> server);

	/**
	 * @return the number of zoom levels supported by the tile set
	 */
	public int getNumLevels();

	/**
	 * Return a map of tiles in this tileset that are visible within the given bounds.
	 * This will build the actual tiles within the given bounds and load them if necessary.
	 * 
	 * <p>The map is from a tile id to the tile</p>
	 * 
	 * @param bounds
	 * @param scale
	 * @return map of tiles
	 */
	public Map<String, Tile> getTilesFromViewportScale(Envelope bounds,
			double viewportScale);
	
	/**
	 * Return a map of tiles in this tileset that are visible within the given bounds.
	 * This will build the actual tiles within the given bounds and load them if necessary.
	 * 
	 * <p>The map is from a tile id to the tile</p>
	 * 
	 * @param bounds
	 * @param zoom level resolution
	 * @return map of tiles
	 */
	public Map<String, Tile> getTilesFromZoom(Envelope bounds,
			double zoom);	
	
    /**
     *  Break up the bounds for this zoom level into a list of bounds so that no single
     *  bounds has more than 1024 tiles in it.
	 */
	public List<Envelope> getBoundsListForZoom( Envelope bounds, double zoom );
	
    /**
     *  Return the number of tiles that are within the given bounds at the zoom
	 */
	public long getTileCount( Envelope bounds, double zoom );

	/**
	 * Get the list of layers as a string that this tileset belong to
	 * 
	 * @return layers
	 */
	public String getLayers();

	/**
	 * @return the styles associated with the tile set
	 */
	public String getStyles();

	/**
	 * @return the tile set format
	 */
	public String getFormat();

	/**
	 * @return the tile set epsg code
	 */
	public String getEPSGCode();

	/**
	 * @return the tile widths
	 */
	public int getWidth();

	/**
	 * @return the tile heights
	 */
	public int getHeight();

	/**
	 * @return the bounding box of the tile set
	 */
	public ReferencedEnvelope getBounds();

	/**
	 * A unique identifier for a tile set
	 * 
	 * @return
	 */
	public int getId();

	/**
	 * the actual resolutions defined in the getCapabilities document
	 *
	 * @return
	 */
	public double[] getResolutions();
	
	/**
	 * @return server
	 */
	public AbstractOpenWebService<?,?> getServer();
}
