package net.refractions.udig.limit;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Defines the changing functionality of the limit service.  
 * @author pfeiffp
 *
 */
public interface ILimitStrategy {
	
	/**
	 * Returns the extent 
	 * Should return an empty envelope for an "All" extent
	 * @return ReferencedEnvelope
	 */
	public ReferencedEnvelope getExtent();
	
	public Geometry getLimit();
	
	public CoordinateReferenceSystem getCrs();

	/**
	 * Returns the name of the limit strategy. 
	 * This is used when adding to the combo to select from.
	 * @return String
	 */
	public String getName();
	
}
