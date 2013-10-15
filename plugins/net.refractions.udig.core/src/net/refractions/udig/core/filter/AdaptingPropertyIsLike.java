/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
