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
package org.tcat.citd.sim.udig.bookmarks;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import net.refractions.udig.boundary.IBoundaryStrategy;

/**
 * 
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class BoundaryStrategyBookmark extends IBoundaryStrategy {

    
    //IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();
    private IBookmark currentBookmark = null;
    
    @Override
    public ReferencedEnvelope getExtent() {
        if (currentBookmark != null) {
            return currentBookmark.getEnvelope();
        }
        return null;
    }

    @Override
    public Geometry getGeometry() {
        if (getExtent() != null) {
            return new GeometryFactory().toGeometry(getExtent());
        }
        return null;
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        if (getExtent() != null) {
            return getExtent().getCoordinateReferenceSystem();
        }
        return null;
    }

    @Override
    public String getName() {
        if (currentBookmark != null) {
            return currentBookmark.getName();
        }
        return null;
    }

    /**
     * @return the currentBookmark
     */
    public IBookmark getCurrentBookmark() {
        return currentBookmark;
    }

    /**
     * @param currentBookmark the currentBookmark to set
     */
    public void setCurrentBookmark( IBookmark currentBookmark ) {
        this.currentBookmark = currentBookmark;
    }

}
