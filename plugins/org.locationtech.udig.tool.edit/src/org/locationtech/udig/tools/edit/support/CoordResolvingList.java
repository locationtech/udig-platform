/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;

import java.util.AbstractList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;

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
