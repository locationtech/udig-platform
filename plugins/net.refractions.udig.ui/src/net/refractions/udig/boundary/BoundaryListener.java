package net.refractions.udig.boundary;

import java.util.EventObject;

import org.geotools.geometry.jts.ReferencedEnvelope;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Quick listener to <code>BoundaryService</code> providing notification
 * of changes to the extent represented (bounds, geometry, crs).
 * <p>
 * @see java.util.EventListener
 */
public interface BoundaryListener {
    /**
     * Captures a change to the bounds published by BoundaryService.
     * @author pfeiffp
     */
    public static class Event extends EventObject{
        private static final long serialVersionUID = -3438046080794276022L;
        public IBoundaryStrategy source;
        public Geometry geometry;
        public ReferencedEnvelope bounds;
        public Event( IBoundaryStrategy source ){
            super(source);
        }
    }
    
    /**
     * BoundaryListener event notification.
     * 
     * @param event the event which occurred
     */
    void handleEvent( Event event );
}
