/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.aoi;

 
import java.util.List;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;

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
 * AOIProxy proxy = service.findProxy("org.locationtech.udig.ui.aoiAll");
 * service.setProxy( proxy );</pre>
 * 
 * @author Paul Pfeiffer
 */
public interface IAOIService {

    /**
     * Get the extent of the current set area.
     * @return Extent of area of interest, or null if not defined
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
