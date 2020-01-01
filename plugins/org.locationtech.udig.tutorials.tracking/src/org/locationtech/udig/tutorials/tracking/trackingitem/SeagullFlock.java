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

import java.util.Iterator;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

import org.locationtech.jts.geom.Coordinate;

import org.locationtech.udig.mapgraphic.MapGraphicContext;

public class SeagullFlock extends AbstractTrackingItem {
    
    public static final String BLACKBOARD_KEY = 
        "org.locationtech.udig.tutorials.tracking.trackingitem.SeagullFlock"; //$NON-NLS-1$
    
    public SeagullFlock(String flockId, InternationalString displayname, CoordinateReferenceSystem crs) {
        this(null, flockId, displayname, crs, null);
    }

    protected SeagullFlock( TrackingItem parent, String id, InternationalString displayname,
            CoordinateReferenceSystem crs, Coordinate coordinate ) {
        super(parent, id, displayname, crs, coordinate);
    }

    /**
     * Draw all the children (seagulls)
     */
    public void draw( MapGraphicContext context ) {
        if (children == null) {
            return;
        }
        Iterator<TrackingItem> iterator = children.iterator();
        while (iterator.hasNext()) {
            TrackingItem seagull = iterator.next();
            seagull.draw(context);
        }
    }

}
