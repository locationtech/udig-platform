package net.refractions.udig.boundary;

import org.eclipse.swt.widgets.Listener;
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
	 * @return Geometry
	 */
	public Geometry getBoundary();
	
	/**
	 * Returns the Coordinate Reference System
	 * @return CoordinateReferenceSystem
	 */
	public CoordinateReferenceSystem getCrs();
	
	/**
	 * Returns true if the tool should be enabled under the current strategy
	 * @param toolId
	 * @return Boolean
	 */
	public Boolean enableTool(String toolId);

	/**
	 * Used to update listeners with the name of the current strategy (example "All").
	 * <p>
	 * The listener is an SWT listener with the widget making the change; and the data
	 * value being the name of the current strategy.
	 * @param watcher
	 */
    public void addListener( Listener listener );
    /**
     * Remove the provided listener.
     * @param watcher
     */
    public void removeListener( Listener listener );
	
}
