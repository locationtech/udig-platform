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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Iterates through all the points in an {@link EditGeom} starting with the shell
 * and then doing the holes in order
 *
 * @author Jesse
 * @since 1.1.0
 */
public class EditGeomPointIterator implements Iterator<Point> {
        private final Iterator<PrimitiveShape> currentShape;
        private Iterator<Point> current;
        private Point next;
        private final Collection<Point> selectedPoints;

        /**
         * New instance
         *
         * @param geom Geometry to draw
         * @param selectedPoints points <em>NOT</em> to draw.
         */
        public EditGeomPointIterator(EditGeom geom, Collection<Point> selectedPoints) {
            currentShape=geom.iterator();
            current=currentShape.next().iterator();
            this.selectedPoints=selectedPoints;
        }

        public EditGeomPointIterator( EditGeom geom ) {
            this( geom, Collections.<Point>emptySet());
        }

        public boolean hasNext() {
            if( next!=null )
                return true;
            do{
                next=getNext();
            }while( next!=null && selectedPoints.contains(next)  );

            return next!=null;
        }

        private Point getNext() {
            if( current.hasNext() ){
                return current.next();
            }

            if( currentShape.hasNext() ){
                current=currentShape.next().iterator();
                return getNext();
            }

            return null;
        }

        public Point next() {
            Point result = next;
            next=null;
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

}
