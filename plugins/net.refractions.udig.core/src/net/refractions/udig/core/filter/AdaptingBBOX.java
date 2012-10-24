/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.BoundingBox;

@SuppressWarnings("deprecation")
class AdaptingBBOX extends AdaptingFilter<BBOX> implements BBOX {

    AdaptingBBOX( BBOX filter ) {
        super(filter);
    }

    public double getMaxX() {
        return wrapped.getMaxX();
    }

    public double getMaxY() {
        return wrapped.getMaxY();
    }

    public double getMinX() {
        return wrapped.getMinX();
    }

    public double getMinY() {
        return wrapped.getMinY();
    }

    public String getPropertyName() {
        return wrapped.getPropertyName();
    }

    public String getSRS() {
        return wrapped.getSRS();
    }

    public Expression getExpression1() {
        return wrapped.getExpression1();
    }

    public Expression getExpression2() {
        return wrapped.getExpression2();
    }

    public MatchAction getMatchAction() {
        return wrapped.getMatchAction();
    }

    @Override
    public BoundingBox getBounds() {
        return wrapped.getBounds();
    }    
}