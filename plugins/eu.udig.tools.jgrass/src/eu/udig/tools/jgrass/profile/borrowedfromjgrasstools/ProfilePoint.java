/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.udig.tools.jgrass.profile.borrowedfromjgrasstools;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A point representing a position in a raster profile.
 * 
 * <p>The point is sortable by its progressive value.</p>
 * 
 * <p>
 * Note that two {@link ProfilePoint}s are meant to be equal
 * if the position and elevation are. This can be used to find touch points
 * of two different profiles.
 * </p> 
 * <p>
 * The sort order of the {@link ProfilePoint} is handled only through 
 * its progressive value.
 * </p> 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ProfilePoint implements Comparable<ProfilePoint> {
    private static final double DELTA = 0.00001;
    private double progressive;
    private double elevation;
    private Coordinate position;

    public ProfilePoint( double progressive, double elevation, Coordinate position ) {
        this.progressive = progressive;
        this.elevation = elevation;
        this.position = position;
    }

    public ProfilePoint( double progressive, double elevation, double easting, double northing ) {
        this.progressive = progressive;
        this.elevation = elevation;
        this.position = new Coordinate(easting, northing);
    }

    public double getProgressive() {
        return progressive;
    }

    public double getElevation() {
        return elevation;
    }

    public Coordinate getPosition() {
        return position;
    }

    public int compareTo( ProfilePoint o ) {
        if (Math.abs(progressive - o.progressive) < DELTA) {
            return 0;
        } else if (progressive > o.progressive) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return progressive + ", " + elevation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(elevation);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        temp = Double.doubleToLongBits(progressive);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProfilePoint other = (ProfilePoint) obj;

        /*
         * the progressive point is equal if the elevation, x, y are equal.
         * This can be is used to find intersecting profiles.
         */
        Coordinate otherPosition = other.position;
        if (Math.abs(elevation - other.elevation) < DELTA && Math.abs(position.x - otherPosition.x) < DELTA
                && Math.abs(position.y - otherPosition.y) < DELTA) {
            return true;
        } else {
            return false;
        }
    }

}
