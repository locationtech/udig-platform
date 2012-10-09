/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.tutorials.tracking.trackingitem;

import java.util.List;

import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.internal.render.impl.CompositeContextListener;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * 
 * A TrackingItem represents one item that is tracked.  It can contain a list
 * of children and a parent, so that a structure of tracking items can be created.  For
 * instance, a top level tracking item could represent a Fleet and its children are its
 * vehicles.  Or a top level tracking item could be a school which contains fish, etc.
 * 
 * The top level parent should have its draw() method called, which should in turn draw
 * all of its children.
 * 
 * @author GDavis
 * @since 1.1.0
 */
public interface TrackingItem {
    
    /**
     * Return the parent of this tracking item.
     * Returns null if there is no parent
     *
     * @return parent or null
     */
    public TrackingItem getParent();
    
    /**
     * Return the list of children belonging to this tracking item.
     * Returns null if it has no children
     *
     * @return list of children or null
     */
    public List<TrackingItem> getChildren();
    
    /**
     * Get the unique identifier
     *
     * @return identifier
     */
    public String getID();        
    
    /**
     * Get the coordinate of this item (in the item's CRS)
     *
     * @return coordinate of this item in its CRS
     */
    public Coordinate getCoordinate();
    
    /**
     * Get the bounds of this item (if it is a parent, typically it will
     * return the bounds that encapsulates all its children)
     *
     * @return Envelope of this item
     */
    public Envelope getBounds();    
    
    /**
     * Set the coordinate of this item
     * 
     * @param coord
     */
    public void setCoordinate(Coordinate coord);    
    
    /**
     * Get the CoordinateReferenceSystem of this item (often returns its parent's CRS)
     *
     * @return CoordinateReferenceSystem of item
     */
    public CoordinateReferenceSystem getCRS();    
    
    /**
     * Get the human readable name
     *
     * @return name
     */
    public InternationalString getDisplayName();    
    
    /**
     * Draw this item on the given context.  This should also draw all of its
     * children.  Only the top level parent's draw method should ever be called.
     *
     * @return void
     */
    public void draw(MapGraphicContext context);   
    
    /**
     * Removes a listener
     *
     * @param trackingitemlistener to remove
     */
    public void removeListener( TrackingItemListener listener );
    
    /**
     * Adds a listener
     *
     * @param trackingitemlistener to add
     */
    public void addListener( TrackingItemListener listener );    
    
    /**
     * Notifies any listeners and parents that the item has changed
     */
    public void notifyChanged();
}
