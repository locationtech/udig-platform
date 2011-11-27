/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.boundary;

 
import java.util.List;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Service for the boundary extent with events to indicate when this value is updated by the user.
 * <p>
 * To determien the current boundary:
 * <pre>
 * IBoundaryService service = PlatformGIS.getBoundaryService();
 * ReferencedEnvelope extent = service.getExtent();</pre>
 * 
 * Example of changing the strategy uesd for determining the boundary extent:
 * <pre>
 * BoundaryProxy proxy = service.findProxy("net.refractions.udig.ui.boundaryAll");
 * service.setProxy( proxy );</pre>
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
    public void setProxy( BoundaryProxy boundaryStrategy );

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
    public void addListener( BoundaryListener listener );
    /**
     * Remove the provided listener.
     * 
     * @param watcher
     */
    public void removeListener( BoundaryListener listener );

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
     * Returns the IBoundaryStrategy with the supplied id.
     * 
     * @param id
     * @return IBoundaryStrategy or null if it cannot be found
     */
    public BoundaryProxy findProxy( String id );

}
