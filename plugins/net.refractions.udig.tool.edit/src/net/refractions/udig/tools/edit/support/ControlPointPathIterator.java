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

import java.awt.geom.PathIterator;
import java.util.Iterator;

/**
 * Draws all the Control handles for a {@link EditGeom}.  This may not be possible to implement because if two vertices are
 * overlapping there will be a hole where the overlap but try it out it may be acceptable.
 *
 * <p><b>Warning.  this class is not fully tested yet so use with care</b></p>
 * @author Jesse
 * @since 1.1.0
 */
public class ControlPointPathIterator implements PathIterator {

    int count=0,x=1,y=1, rectPos=0;
    private final Iterator<Point> points;
    private final int xRadius;
    private final int yRadius;

    public ControlPointPathIterator(final EditGeom geom, boolean selection, int width, int height ) {
        final Selection selectionCollection = geom.createSelection();

        int xRadius=width/2;
        int yRadius=height/2;
        if( xRadius<1 )
            xRadius=1;
        if( yRadius<1 )
            yRadius=1;

        this.xRadius=xRadius;
        this.yRadius=yRadius;

        if( selection ){
            points=selectionCollection.iterator();
        }else{
            points = new EditGeomPointIterator(geom, selectionCollection);
        }

        if( points.hasNext() ){
            Point next = points.next();
            x=next.getX();
            y=next.getY();
        }else{
            rectPos=5;
        }
    }

    public int currentSegment( float[] coords ) {
        double[] c=new double[2];
        int result = currentSegment(c);
        coords[0]=(float) c[0];
        coords[1]=(float) c[1];
        return result;
    }

    public int currentSegment( double[] coords ) {
        switch( rectPos ) {
        case 0:
            coords[0]=x-xRadius;
            coords[1]=y-yRadius;
            break;
        case 1:
            coords[0]=x+xRadius;
            coords[1]=y-yRadius;
            break;
        case 2:
            coords[0]=x+xRadius;
            coords[1]=y+yRadius;
            break;
        case 3:
            coords[0]=x-xRadius;
            coords[1]=y+yRadius;
            break;
        case 4:
            coords[0]=x-xRadius;
            coords[1]=y-yRadius;
            break;
        case 5:
            return PathIterator.SEG_CLOSE;
        default:
            break;
        }

        if( rectPos==0 ){
            return PathIterator.SEG_MOVETO;
        }
        return PathIterator.SEG_LINETO;
    }

    public int getWindingRule() {
        return WIND_EVEN_ODD;
    }

    public boolean isDone() {
        return !points.hasNext() && rectPos>5;
    }

    public void next() {
        rectPos++;
        if( rectPos==5 && points.hasNext() ){
            rectPos=0;
            count++;

            Point next=points.next();
            x=next.getX();
            y=next.getY();
        }
    }

}
