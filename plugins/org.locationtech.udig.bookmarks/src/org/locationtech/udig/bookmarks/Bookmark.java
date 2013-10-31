/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2006, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.bookmarks;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.bookmarks.internal.MapReference;

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
     * @see org.locationtech.udig.bookmarks.IBookmark#getEnvelope()
     */
    @Override
    public ReferencedEnvelope getEnvelope() {
        return envelope;
    }
    /* (non-Javadoc)
     * @see org.locationtech.udig.bookmarks.IBookmark#setEnvelope(org.geotools.geometry.jts.ReferencedEnvelope)
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
     * @see org.locationtech.udig.bookmarks.IBookmark#getName()
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
     * @see org.locationtech.udig.bookmarks.IBookmark#setName(java.lang.String)
     */
    @Override
    public void setName( String name ) {
        this.name = name;
    }

}
