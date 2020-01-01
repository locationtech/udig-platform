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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.locationtech.udig.internal.ui.UiPlugin;

import org.eclipse.ui.part.IPageBookViewPage;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;

/**
 * Defines the changing functionality of the AOI (Area of Interest) service.
 * 
 * @author paul.pfeiffer
 */
public abstract class IAOIStrategy {

    /**
     * Returns the extent of the current AOI. 
     * Should return null for an "All" extent
     * 
     * @return ReferencedEnvelope
     */
    public abstract ReferencedEnvelope getExtent();

    /**
     * Returns a geometry of the current AOI selected.
     * Returning a null geometry specifies no AOI and by default will
     * be treated as a world extent
     * 
     * @return Geometry
     */
    public abstract Geometry getGeometry();

    /**
     * Returns the CRS of the current AOI selected
     * 
     * @return
     */
    public abstract CoordinateReferenceSystem getCrs();

    /**
     * Returns the name of the AOI strategy. This is used when adding to the combo to select
     * from.
     * 
     * @return String
     */
    public abstract String getName();

    /**
     * A list of listeners to be notified when the Strategy changes
     */
    protected Set<AOIListener> listeners = new CopyOnWriteArraySet<AOIListener>();
    
    /**
     * Allows notification of changes to the AOI represented by the
     * strategy. This is used for dynamic boundaries that change over time
     * (perhaps in response to a user using a tool to specifiy a new
     * clipping area).
     * <p>
     * The AOIServiceImpl will register a single listener in order
     * to track what is going on.
     * 
     * @param listener
     */
    public void addListener( AOIListener listener ) {
        if( listener == null ){
            throw new NullPointerException("AOIService listener required to be non null");
        }
        if( !listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    /**
     * Remove a listener for AOI chages.
     * 
     * @param listener
     */
    public void removeListener( AOIListener listener ) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies listener that the value of the filter has changed.
     * <p>
     * 
     * @param data Geometry supplied to listeners using event.data
     */
    protected void notifyListeners(AOIListener.Event event) {
        for( AOIListener listener : listeners ) {
            if( event == null ){
                event = new AOIListener.Event(this);
            }
            try {
                if( listener != null ){
                    listener.handleEvent( event );
                }
            } catch (Exception e) {
                UiPlugin.trace(UiPlugin.ID, listener.getClass(), e.getMessage(), e );
            }
        }
    }
    
    /**
     * Creates a Page (used for extra selection like the bookmark strategy).
     * <p>
     * Please note this is provided by the extension point information via AOIProxy.
     * As such this method is expected to return null.
     * 
     * @return Page if it exists otherwise null
     */
    public IPageBookViewPage createPage() {
        return null;
    }
}
