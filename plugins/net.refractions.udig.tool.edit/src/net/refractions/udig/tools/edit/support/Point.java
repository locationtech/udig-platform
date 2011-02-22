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



/**
 * A point implementation.  Each point for a given location is a singleton and immutable so points can be
 * compared with ==.
 *
 * @author jones
 * @since 1.1.0
 */
public class Point{

    private Point(int x, int y){
        this.x=x;
        this.y=y;
        hashCode=(17+x)*37+y;
    }

    private final int x;
    private final int y;
    private final int hashCode;

    public int getX(){ return x; }
    public int getY(){ return y; }

    private static final Point[][] cache=new Point[256][256];
    /**
     * Get a point for location x,y
     *
     * @return a point for location x,y
     */
    public synchronized static Point valueOf(int x, int y){
        if( x>-1 && x<256 &&
                y>-1 && y<256 ){
            if( cache[x][y]==null ){
                cache[x][y]=new Point(x,y);
            }
            return cache[x][y];
        }

        return new Point(x,y);

    }

    @Override
    public boolean equals( Object obj ) {
        if( obj==this)
            return true;

        if( !(obj instanceof Point) )
            return false;
        Point p = (Point) obj;
        return x==p.x&&y==p.y;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
    @Override
    public String toString() {
        return "("+x+","+y+")";   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
    }
}
