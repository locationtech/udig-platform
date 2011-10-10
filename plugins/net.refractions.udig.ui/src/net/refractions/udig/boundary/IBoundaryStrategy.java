package net.refractions.udig.boundary;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Defines the changing functionality of the Boundary service.  
 * @author pfeiffp
 *
 */
public interface IBoundaryStrategy {
	
	/**
	 * Returns the extent 
	 * Should return an empty envelope for an "All" extent
	 * @return ReferencedEnvelope
	 */
	public ReferencedEnvelope getExtent();
	
	/**
	 * Returns a geometry of the current boundary selected
	 * @return Geometry
	 */
	public Geometry getBoundary();
	
	/**
	 * Returns the CRS of the current Boundary selected
	 * @return
	 */
	public CoordinateReferenceSystem getCrs();

	/**
	 * Returns the name of the Boundary strategy. 
	 * This is used when adding to the combo to select from.
	 * @return String
	 */
	public String getName();
	
	/**
	 * Returns true if the tool should be enabled when this strategy is selected. 
	 * By default this should return true. Set this to false if a function will not be affected by this strategy.
	 * An example of this is Zoom to extent when the boundary is screen.
	 * @return Boolean
	 */
	public Boolean enableTool(String toolId);
	
}
