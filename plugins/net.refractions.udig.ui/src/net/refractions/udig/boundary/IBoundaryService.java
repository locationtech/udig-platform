package net.refractions.udig.boundary;

import java.util.List;

import org.eclipse.swt.widgets.Listener;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A service that can get the area extent, future: listen for updates to the area and notify
 * listeners of the update
 * 
 * @author Paul Pfeiffer
 */
public interface IBoundaryService {

    /**
     * Get the extent of the current set area
     */
    public ReferencedEnvelope getExtent();

    /**
     * Sets the current Boundary Strategy that will be used to get extent
     * 
     * @param boundaryStrategy
     */
    public void setStrategy( BoundaryProxy boundaryStrategy );

    /**
     * Get the current Boundary Strategy
     * 
     * @return BoundaryProxy
     */
    public BoundaryProxy getProxy();

    /**
     * Returns the Geometry of the Boundary
     * 
     * @return Geometry
     */
    public Geometry getGeometry();

    /**
     * Returns the Coordinate Reference System
     * 
     * @return CoordinateReferenceSystem
     */
    public CoordinateReferenceSystem getCrs();

    /**
     * Used to update listeners with the name of the current strategy (example "All").
     * <p>
     * The listener is an SWT listener with the widget making the change; and the data value being
     * the name of the current strategy.
     * 
     * @param watcher
     */
    public void addListener( Listener listener );
    /**
     * Remove the provided listener.
     * 
     * @param watcher
     */
    public void removeListener( Listener listener );

    /**
     * The boundary strategy to use by default (defaults to "All").
     * 
     * @return the strategy to use by default
     */
    public BoundaryProxy getDefault();

    /**
     * Get the list of BoundaryProxy (you can use getProxy() to access the implementation used if
     * you are interested).
     * 
     * @return the list of BoundaryProxy
     */
    public List<BoundaryProxy> getProxyList();

    /**
     * Returns the IBoundaryStrategy with the supplied id
     * 
     * @param id
     * @return IBoundaryStrategy or null if it cannot be found
     */
    public BoundaryProxy findProxy( String id );

}
