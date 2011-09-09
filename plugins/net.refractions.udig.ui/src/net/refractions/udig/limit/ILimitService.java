package net.refractions.udig.limit;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A service that can get the area extent, 
 * future: listen for updates to the area and notify listeners of the update
 * @author Paul Pfeiffer
 *
 */
public interface ILimitService {
	
	/**
	 * Get the extent of the current set area
	 */
	public ReferencedEnvelope getExtent();

	/**
	 * Sets the current Limit Strategy that will be used to get extent
	 * @param limitStrategy
	 */
	public void setStrategy(ILimitStrategy limitStrategy);
	
	/**
	 * Returns the Geometry of the Limit
	 * @return
	 */
	public Geometry getLimit();
	
	public CoordinateReferenceSystem getCrs();
	

}
