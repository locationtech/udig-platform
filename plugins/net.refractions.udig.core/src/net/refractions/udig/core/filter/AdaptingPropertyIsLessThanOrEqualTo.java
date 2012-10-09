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

import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsBetween interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsLessThanOrEqualTo extends AdaptingFilter<PropertyIsLessThanOrEqualTo> implements PropertyIsLessThanOrEqualTo{

    AdaptingPropertyIsLessThanOrEqualTo( PropertyIsLessThanOrEqualTo filter ) {
        super(filter);
    }
    public Expression getExpression1() {
        return wrapped.getExpression1();
    }
    public Expression getExpression2() {
        return wrapped.getExpression2();
    }
    public boolean isMatchingCase() {
        return wrapped.isMatchingCase();
    }
    public MatchAction getMatchAction() {
        return wrapped.getMatchAction();
    }
}
