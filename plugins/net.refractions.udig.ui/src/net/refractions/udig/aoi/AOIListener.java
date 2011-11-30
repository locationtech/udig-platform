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
package net.refractions.udig.aoi;

import java.util.EventObject;

import org.geotools.geometry.jts.ReferencedEnvelope;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Quick listener to <code>AOIService</code> (Area of Interest) providing notification
 * of changes to the extent represented (bounds, geometry, crs).
 * <p>
 * @see java.util.EventListener
 */
public interface AOIListener {
    /**
     * Captures a change to the bounds published by AOIService.
     * @author paul.pfeiffer
     */
    public static class Event extends EventObject{
        private static final long serialVersionUID = -3438046080794276022L;
        public IAOIStrategy source;
        public Geometry geometry;
        public ReferencedEnvelope bounds;
        public Event( IAOIStrategy source ){
            super(source);
        }
    }
    
    /**
     * AOIListener event notification.
     * 
     * @param event the event which occurred
     */
    void handleEvent( Event event );
}
