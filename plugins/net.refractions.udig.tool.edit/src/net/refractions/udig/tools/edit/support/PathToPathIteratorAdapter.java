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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;

/**
 * Wraps a Path and allows it to be traversed like a PathIterator.  
 * <p><em>WARNING: This class takes a snap shot of the path upon creation so any changes after creation will be missed</em></p>
 * @author Jesse
 * @since 1.1.0
 */
public class PathToPathIteratorAdapter implements PathIterator {

    private PathData data;
    private int pointIndex=0, typeIndex=0;;

    /**
     * Creates a new instance.
     * 
     * <p><em>WARNING: This class takes a snap shot of the path upon creation so any changes after creation will be missed</em></p>
     */
    public PathToPathIteratorAdapter( Path path ) {
        this.data=path.getPathData();
    }

    public int currentSegment( float[] coords ) {
        int num = getNumPoints();
        System.arraycopy(data.points, pointIndex, coords, 0, num);
        return getSegType();
    }

    private int getSegType() {
        switch(data.types[typeIndex]){
        case SWT.PATH_MOVE_TO: // moveTo
            return SEG_MOVETO;
        case SWT.PATH_LINE_TO: // lineTo
            return SEG_LINETO;
        case SWT.PATH_QUAD_TO: // quadTo
           return SEG_QUADTO;
        case SWT.PATH_CUBIC_TO: // cubeTo
            return SEG_CUBICTO;
        case SWT.PATH_CLOSE: // close
            return SEG_CLOSE;
        }
        throw new IllegalArgumentException(data.types[typeIndex]+" is an unknown value"); //$NON-NLS-1$
    }

    public int currentSegment( double[] coords ) {
        int num = getNumPoints();
        for( int i=0; i<num; i++){
            coords[i]=data.points[i+pointIndex];
        }
        return getSegType();
    }

    public int getWindingRule() {
        return WIND_EVEN_ODD;
    }

    public boolean isDone() {
        return typeIndex==data.types.length;
    }

    public void next() {
        pointIndex+=getNumPoints();
        typeIndex++;
    }

    private int getNumPoints() {
        switch(data.types[typeIndex]){
        case SWT.PATH_MOVE_TO: // moveTo
            return 2;
        case SWT.PATH_LINE_TO: // lineTo
            return 2;
        case SWT.PATH_QUAD_TO: // quadTo
           return 4;
        case SWT.PATH_CUBIC_TO: // cubeTo
            return 6;
        case SWT.PATH_CLOSE: // close
            return 0;
        }
        throw new IllegalArgumentException(data.types[typeIndex]+" is an unknown value"); //$NON-NLS-1$
    }

}
