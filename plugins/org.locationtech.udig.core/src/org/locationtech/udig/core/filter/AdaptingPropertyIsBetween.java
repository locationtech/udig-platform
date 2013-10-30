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

import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsBetween interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsBetween extends AdaptingFilter<PropertyIsBetween> implements PropertyIsBetween {

    AdaptingPropertyIsBetween( PropertyIsBetween filter ) {
        super(filter);
    }

    public Expression getExpression() {
        return wrapped.getExpression();
    }

    public Expression getLowerBoundary() {
        return wrapped.getLowerBoundary();
    }

    public Expression getUpperBoundary() {
        return wrapped.getUpperBoundary();
    }
    
    public MatchAction getMatchAction() {
        return wrapped.getMatchAction();
    }
}
