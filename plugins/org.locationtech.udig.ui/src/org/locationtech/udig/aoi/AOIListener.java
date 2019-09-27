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

import java.util.EventObject;

import org.geotools.geometry.jts.ReferencedEnvelope;

import org.locationtech.jts.geom.Geometry;

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
