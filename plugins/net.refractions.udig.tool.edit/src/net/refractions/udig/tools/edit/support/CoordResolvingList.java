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

import java.util.AbstractList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A list that will wraps a List<LazyCoord> and resolves the LazyCoords into coordinate when
 * requested.
 * 
 * @author jones
 * @since 1.1.0
 */
public class CoordResolvingList extends AbstractList<Coordinate> {
    final List<LazyCoord> wrapped;
    private final Point current;

    public CoordResolvingList( List<LazyCoord> list2, Point current2 ) {
        this.wrapped = list2;
        this.current = current2;
    }

    @Override
    public Coordinate get( int index ) {
        return this.wrapped.get(index).get(current);
    }

    @Override
    public int size() {
        return this.wrapped.size();
    }
    
    
}
