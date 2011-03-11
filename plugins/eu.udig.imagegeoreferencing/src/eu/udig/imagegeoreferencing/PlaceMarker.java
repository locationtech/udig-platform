/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
package eu.udig.imagegeoreferencing;

import java.awt.Color;
import java.awt.Point;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Represents a placemarker that is put on a geoImage or the basemap.
 * 
 * @author GDavis, Refractions Research
 * @since 1.1.0
 */
public class PlaceMarker {

    public static final int DRAWING_SIZE = 10;
    public static final Color DEFAULT_COLOR = Color.GREEN;

    private Point point; // screen pixel point
    private Coordinate coord; // map coordinate
    private Color color;

    private boolean isDragging = false;

    // image or basemap marker
    private boolean isBasemapMarker = false;

    // associated image (if image marker)
    private GeoReferenceImage image;

    public PlaceMarker( Point point ) {
        this(point, DEFAULT_COLOR);
    }

    public PlaceMarker( Point point, Color color ) {
        this.point = point;
        this.color = color;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint( Point point ) {
        this.point = point;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord( Coordinate coord ) {
        this.coord = coord;
    }

    public Color getColor() {
        return color;
    }

    public void setColor( Color color ) {
        this.color = color;
    }

    public boolean isBasemapMarker() {
        return isBasemapMarker;
    }

    public void setBasemapMarker( boolean isBasemapMarker ) {
        this.isBasemapMarker = isBasemapMarker;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void setDragging( boolean isDragging ) {
        this.isDragging = isDragging;
    }

    public GeoReferenceImage getImage() {
        return image;
    }

    public void setImage( GeoReferenceImage image ) {
        this.image = image;
    }
}
