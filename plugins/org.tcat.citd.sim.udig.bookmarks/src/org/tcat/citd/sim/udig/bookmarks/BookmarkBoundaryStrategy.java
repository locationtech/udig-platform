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

import net.refractions.udig.boundary.BoundaryListener;
import net.refractions.udig.boundary.IBoundaryStrategy;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Advertise the currently selected bookmark as a Boundary for use in
 * catalog services and others.
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class BookmarkBoundaryStrategy extends IBoundaryStrategy {
    public static String ID = "org.tcat.citd.sim.udig.bookmarks.boundary";
    
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
    public void setCurrentBookmark( IBookmark bookmark ) {
        if (!bookmark.equals(currentBookmark)) {
            currentBookmark = bookmark;
            // notify everything that is listening for a strategy change
            this.notifyListeners( new BoundaryListener.Event(this));
        }
    }

}
