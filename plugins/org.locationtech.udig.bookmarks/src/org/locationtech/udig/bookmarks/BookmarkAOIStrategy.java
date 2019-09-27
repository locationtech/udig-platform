/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.bookmarks;

import org.locationtech.udig.aoi.AOIListener;
import org.locationtech.udig.aoi.IAOIStrategy;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * Advertise the currently selected bookmark as a AOI (Area of Interest) for use in
 * catalog services and others.
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class BookmarkAOIStrategy extends IAOIStrategy {
    public static String ID = "org.locationtech.udig.bookmarks.bookmarkAOIStrategy";
    
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
            this.notifyListeners( new AOIListener.Event(this));
        }
    }

}
