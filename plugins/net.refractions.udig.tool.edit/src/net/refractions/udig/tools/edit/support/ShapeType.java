/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.tools.edit.support;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Enumerates the different types of Geometry types that a EditGeom can be.
 *
 * @author jones
 * @since 1.1.0
 */
public enum ShapeType {
    UNKNOWN, POINT, LINE, POLYGON;

    public static ShapeType valueOf( Geometry geom ) {
        if( geom instanceof Point || geom instanceof MultiPoint)
            return POINT;
        if( geom instanceof LineString || geom instanceof LinearRing || geom instanceof MultiLineString)
            return LINE;
        if( geom instanceof Polygon || geom instanceof MultiPolygon)
            return POLYGON;

        return UNKNOWN;
    }

    public static ShapeType valueOf( Class type ) {
        if( Point.class.isAssignableFrom(type) || MultiPoint.class.isAssignableFrom(type))
            return POINT;
        if( LineString.class.isAssignableFrom(type) || LinearRing.class.isAssignableFrom(type)
                || MultiLineString.class.isAssignableFrom(type))
            return LINE;
        if( Polygon.class.isAssignableFrom(type) || MultiPolygon.class.isAssignableFrom(type) )
            return POLYGON;

        return UNKNOWN;
    }
}
