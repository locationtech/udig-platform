/**
 * 
 */
package net.refractions.udig.limit;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author pfeiffp
 *
 */
public interface ILimitStrategy {
	
	/**
	 * Returns the extent 
	 * Should return an empty envelope for an "All" extent
	 */
	public ReferencedEnvelope getExtent();
	
	public Geometry getLimit();
	
	public CoordinateReferenceSystem getCrs();

}
