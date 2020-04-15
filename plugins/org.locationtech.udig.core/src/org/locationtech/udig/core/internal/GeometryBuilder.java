/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.core.internal;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * Provides methods for conveniently recreating JTS geometries.
 * 
 * @author jEICHAR
 * @since 0.3
 */
public class GeometryBuilder {
    /**
     * 
     */
    public GeometryFactory factory = new GeometryFactory();

    private GeometryBuilder() {
        // do nothing
    }

    /**
     * create a geometry builder object.
     * 
     * @return GeometryBuilder
     */
    public static GeometryBuilder create() {
        return new GeometryBuilder();
    }

    /**
     * Creates a Geometry of type type from the coordinates in the coordinate array.
     * 
     * @param type
     * @param coords
     * @return Geometry
     */
    public Geometry createGeometry( Class type, Coordinate[] coords ) {
        if (LinearRing.class.isAssignableFrom(type)) {
            return factory.createLinearRing(coords);
        }
        if (LineString.class.isAssignableFrom(type)) {
            return factory.createLineString(coords);
        }
        return null;
    }
    /** 
     * Creates a Geometry (or related class) from the coordinates with a few extra
     * sanity checks (such as closing rings and so on).
     * @param <T>
     * @param type
     * @param coords
     * @return Geometry
     */
    public <T extends Geometry> T safeCreateGeometry( Class<T> type, Coordinate[] coords ) {
        if (LinearRing.class.isAssignableFrom(type)) {
            LinearRing ring = safeCreateLinearRing( coords );
			return type.cast(ring);
        }
        if (LineString.class.isAssignableFrom(type)) {
            if (coords.length < 2) {
                Coordinate[] tmp = new Coordinate[2];
                System.arraycopy(coords, 0, tmp, 0, coords.length);
                for( int i = coords.length; i < tmp.length; i++ ) {
                    tmp[i] = new Coordinate(coords[coords.length - 1]);
                }
                coords = tmp;
            }
            return type.cast(factory.createLineString(coords));
        }
        if (Coordinate.class.isAssignableFrom(type)) {
            return type.cast(factory.createPoint(coords[0]));
        }

        if (Point.class.isAssignableFrom(type)) {
            return type.cast(factory.createPoint(coords[0]));
        }
        if (MultiPoint.class.isAssignableFrom(type)) {
            return type.cast(factory.createMultiPoint(coords));
        }

        if (Polygon.class.isAssignableFrom(type)) {
            return type.cast(factory.createPolygon((LinearRing) safeCreateGeometry(LinearRing.class, coords),
                    new LinearRing[]{}));
        }

        if (MultiPolygon.class.isAssignableFrom(type)) {
            return type.cast(factory.createMultiPolygon(new Polygon[]{(Polygon) safeCreateGeometry(
                    Polygon.class, coords)}));
        }

        if (MultiLineString.class.isAssignableFrom(type)) {
            return type.cast(factory.createMultiLineString(new LineString[]{(LineString) safeCreateGeometry(
                    LineString.class, coords)}));
        }

        return null;
    }

	public LinearRing safeCreateLinearRing(Coordinate[] coords) {
        if (coords.length < 4) {
            Coordinate[] tmp = new Coordinate[4];
            System.arraycopy(coords, 0, tmp, 0, coords.length);
            for( int i = coords.length; i < tmp.length; i++ ) {
                tmp[i] = new Coordinate(coords[0]);
            }
            coords = tmp;
        }
        if( !coords[0].equals(coords[coords.length-1]) ){
            Coordinate[] tmp = new Coordinate[coords.length+1];
            System.arraycopy(coords, 0, tmp,0, coords.length);
            tmp[tmp.length-1]=tmp[0];
            coords=tmp;
        }
        return factory.createLinearRing(coords);
	}

}
