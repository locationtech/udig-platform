package org.tcat.citd.sim.udig.bookmarks;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.tcat.citd.sim.udig.bookmarks.internal.MapReference;

/**
 * Representation of a bookmark.
 * <p>
 * A <code>Bookmark</code> consists of the <code>IMap</code> object it is associated with, the
 * <code>Envelope</code> that decscribes the bounds, and a user-defined name.
 * </p>
 * 
 * @author cole.markham
 * @since 1.0.0
 */
public class Bookmark {
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

    /**
     * @return Returns the envelope.
     */
    public ReferencedEnvelope getEnvelope() {
        return envelope;
    }
    /**
     * @param envelope The envelope to set.
     */
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

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * @param name The name to set.
     */
    public void setName( String name ) {
        this.name = name;
    }

}
