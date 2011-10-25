/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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
package net.refractions.udig.tools.internal;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

/**
 * This service is set by the Boundary Navigation Tool and provides a filter service based on the
 * selection of boundary layer features.
 * 
 * @author leviputna
 * @since 1.2.3
 */
public class BoundaryLayerStrategy extends IBoundaryStrategy {

    private static String name = "Layer";
    private CoordinateReferenceSystem crs;
    private Geometry geometry;
    private ILayer activeLayer;

    /**
     * Set the CRS of the current boundary
     * 
     * @param crs of the current boundary
     */
    public void setCrs( CoordinateReferenceSystem crs ) {
        this.crs = crs;
    }

    /**
     * @return the activeLayer
     */
    public ILayer getActiveLayer() {
        return activeLayer;
    }

    /**
     * @param activeLayer the activeLayer to set
     */
    public void setActiveLayer( ILayer activeLayer ) {
        this.activeLayer = activeLayer;
    }

    /**
     * Set the geometry to be used as the boundary
     * 
     * @param geometry The geometry to use as the boundary must be type Polygon or Multy Polygon
     */
    public void setGeometry( Geometry geometry ) {
        this.geometry = geometry;
        notifyListeners(this);
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getExtent()
     */
    @Override
    public ReferencedEnvelope getExtent() {
        if (geometry != null) return (ReferencedEnvelope) geometry.getEnvelopeInternal();
        return null;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getBoundary()
     */
    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getCrs()
     */
    @Override
    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getName()
     */
    @Override
    public String getName() {
        return name;
    }

}