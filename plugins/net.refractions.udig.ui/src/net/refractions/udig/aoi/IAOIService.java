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
package net.refractions.udig.aoi;

 
import java.util.List;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Service for the AOI (Area of Interest) extent with events to indicate when this value is updated by the user.
 * <p>
 * To determien the current AOI:
 * <pre>
 * IAOIService service = PlatformGIS.getAOIService();
 * ReferencedEnvelope extent = service.getExtent();</pre>
 * 
 * Example of changing the strategy uesd for determining the AOI extent:
 * <pre>
 * AOIProxy proxy = service.findProxy("net.refractions.udig.ui.aoiAll");
 * service.setProxy( proxy );</pre>
 * 
 * @author Paul Pfeiffer
 */
public interface IAOIService {

    /**
     * Get the extent of the current set area
     */
    public ReferencedEnvelope getExtent();

    /**
     * Sets the current AOI Strategy that will be used to get extent
     * 
     * @param aoiStrategy
     */
    public void setProxy( AOIProxy aoiStrategy );

    /**
     * Get the current AOI Strategy
     * 
     * @return AOIProxy
     */
    public AOIProxy getProxy();

    /**
     * Returns the Geometry of the AOI
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
    public void addListener( AOIListener listener );
    /**
     * Remove the provided listener.
     * 
     * @param watcher
     */
    public void removeListener( AOIListener listener );

    /**
     * The AOI strategy to use by default (defaults to "All").
     * 
     * @return the strategy to use by default
     */
    public AOIProxy getDefault();

    /**
     * Get the list of AOIProxy (you can use getProxy() to access the implementation used if
     * you are interested).
     * 
     * @return the list of AOIProxy
     */
    public List<AOIProxy> getProxyList();

    /**
     * Returns the IAOIStrategy with the supplied id.
     * 
     * @param id
     * @return IAOIStrategy or null if it cannot be found
     */
    public AOIProxy findProxy( String id );

}
