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

import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsLike interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsLike extends AdaptingFilter<PropertyIsLike> implements PropertyIsLike {

    AdaptingPropertyIsLike( PropertyIsLike filter ) {
        super(filter);
    }

    public String getEscape() {
        return wrapped.getEscape();
    }

    public Expression getExpression() {
        return wrapped.getExpression();
    }

    public String getLiteral() {
        return wrapped.getLiteral();
    }

    public String getSingleChar() {
        return wrapped.getSingleChar();
    }

    public String getWildCard() {
        return wrapped.getWildCard();
    }

    public boolean isMatchingCase() {
        return wrapped.isMatchingCase();
    }
    
    public MatchAction getMatchAction() {
        return wrapped.getMatchAction();
    }
}
