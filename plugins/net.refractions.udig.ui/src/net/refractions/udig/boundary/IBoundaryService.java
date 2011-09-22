package net.refractions.udig.boundary;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A service that can get the area extent, 
 * future: listen for updates to the area and notify listeners of the update
 * @author Paul Pfeiffer
 *
 */
public interface IBoundaryService {
	
	/**
	 * Get the extent of the current set area
	 */
	public ReferencedEnvelope getExtent();

	/**
	 * Sets the current Boundary Strategy that will be used to get extent
	 * @param boundaryStrategy
	 */
	public void setStrategy(IBoundaryStrategy boundaryStrategy);
	
	/**
	 * Get the current Boundary Strategy
	 * @return boundaryStrategy
	 */
	public IBoundaryStrategy currentStrategy();
	
	/**
	 * Returns the Geometry of the Boundary
	 * @return
	 */
	public Geometry getBoundary();
	
	/**
	 * Returns the Coordinate Reference System
	 * @return crs
	 */
	public CoordinateReferenceSystem getCrs();
	
}
