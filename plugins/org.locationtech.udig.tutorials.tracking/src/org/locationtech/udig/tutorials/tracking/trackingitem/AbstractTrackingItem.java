/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.tracking.trackingitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

public abstract class AbstractTrackingItem implements TrackingItem {

    protected CoordinateReferenceSystem crs;
    protected Coordinate coordinate;
    protected String id;
    protected InternationalString displayname;
    protected TrackingItem parent;
    protected List<TrackingItem> children;
    protected final Set<TrackingItemListener> listeners = 
        new CopyOnWriteArraySet<TrackingItemListener>();
    
    protected AbstractTrackingItem(TrackingItem parent, String id, 
            InternationalString displayname, CoordinateReferenceSystem crs, Coordinate coordinate) {
        this.parent = parent;
        this.id = id;
        this.displayname = displayname;
        this.crs = crs;
        this.coordinate = coordinate;
    }

    public CoordinateReferenceSystem getCRS() {
        return crs;
    }

    public List<TrackingItem> getChildren() {
        return children;
    } 
    
    public void addChild(TrackingItem child) {
        if (children == null) {
            children = new ArrayList<TrackingItem>();
        }
        children.add(child);
        
        // add the listeners to the child
        for (TrackingItemListener listener: listeners) {
            child.addListener(listener);
        }
        
        // notify the listeners that this item has changed
        notifyChanged();
    }
    
    public void removeChild(TrackingItem child) {
        if (children == null) {
            return;
        }
        children.remove(child);
        
        // remove listeners from the child
        for (TrackingItemListener listener: listeners) {
            child.removeListener(listener);
        }
        
        // notify the listeners that this item has changed
        notifyChanged();
    }     

    
    public Coordinate getCoordinate() {
        return coordinate;
    }
    
    public Envelope getBounds() {
        // if this item has children, return the bounds of them all
        if (children != null) {
            Envelope envelope = new Envelope();
            for(TrackingItem child: children) {
                Coordinate coord = child.getCoordinate();
                if (coord != null) {
                    envelope.expandToInclude(coord);
                }
            }
            return envelope;
        }
        else if (coordinate != null) {
            return new Envelope(coordinate);
        }
        return null;
    }
    
    public void setCoordinate(Coordinate coord) {
        this.coordinate = coord;
        // notify the listeners that this item has changed
        notifyChanged();
    }    

    public InternationalString getDisplayName() {
        return displayname;
    }

    public String getID() {
        return id;
    }

    public TrackingItem getParent() {
        return parent;
    }
    
    public void removeListener( TrackingItemListener listener ) {
        listeners.remove(listener);
    }

    public void addListener( TrackingItemListener listener ) {
        listeners.add(listener);
    }
    
    /**
     * This method will notify all of the item's parents that it changed (since
     * typically only the top-level items will have listeners and they will be
     * responsible for knowing when their children change) and it will also
     * notify any listeners for this item of the change.
     * 
     */
    public void notifyChanged() {
        if (parent != null) {
            parent.notifyChanged();
        }
        for( TrackingItemListener l : listeners ) {
            l.notifyChanged(this);
        }
    }

}
