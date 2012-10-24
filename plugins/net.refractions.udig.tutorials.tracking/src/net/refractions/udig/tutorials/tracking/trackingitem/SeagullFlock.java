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

import java.util.Iterator;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

import com.vividsolutions.jts.geom.Coordinate;

import net.refractions.udig.mapgraphic.MapGraphicContext;

public class SeagullFlock extends AbstractTrackingItem {
    
    public static final String BLACKBOARD_KEY = 
        "net.refractions.udig.tutorials.tracking.trackingitem.SeagullFlock"; //$NON-NLS-1$
    
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
