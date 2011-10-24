/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2006, Refractions Research Inc.
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
import org.tcat.citd.sim.udig.bookmarks.internal.MapReference;

/**
 * Bookmark of the current MapViewport
 * <p>
 * A <code>Bookmark</code> consists of the <code>IMap</code> object it is associated with, the
 * <code>Envelope</code> that decscribes the bounds, and a user-defined name.
 * </p>
 * 
 * @author cole.markham
 * @since 1.0.0
 */
public class Bookmark implements IBookmark {
    private ReferencedEnvelope envelope;
    private MapReference mapID;
    private String name;

    /**
     * Construct a new bookmark with the given center and envelope.
     * 
     * @param envelope
     * @param map
     * @param name
     */
    public Bookmark( ReferencedEnvelope envelope, MapReference map, String name ) {
        this.envelope = envelope;
        this.mapID = map;
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.tcat.citd.sim.udig.bookmarks.IBookmark#getEnvelope()
     */
    @Override
    public ReferencedEnvelope getEnvelope() {
        return envelope;
    }
    /* (non-Javadoc)
     * @see org.tcat.citd.sim.udig.bookmarks.IBookmark#setEnvelope(org.geotools.geometry.jts.ReferencedEnvelope)
     */
    @Override
    public void setEnvelope( ReferencedEnvelope envelope ) {
        this.envelope = envelope;
    }

    /**
     * @return Returns the map.
     */
    public MapReference getMap() {
        return mapID;
    }

    /**
     * @param map The map to set.
     */
    public void setMap( MapReference map ) {
        this.mapID = map;
    }

    /* (non-Javadoc)
     * @see org.tcat.citd.sim.udig.bookmarks.IBookmark#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /* (non-Javadoc)
     * @see org.tcat.citd.sim.udig.bookmarks.IBookmark#setName(java.lang.String)
     */
    @Override
    public void setName( String name ) {
        this.name = name;
    }

}
