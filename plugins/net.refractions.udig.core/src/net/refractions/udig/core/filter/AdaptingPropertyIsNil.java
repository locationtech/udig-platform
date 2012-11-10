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

import org.opengis.filter.PropertyIsNil;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsNull interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsNil extends AdaptingFilter<PropertyIsNil> implements PropertyIsNil{

    AdaptingPropertyIsNil( PropertyIsNil filter ) {
        super(filter);
    }
    
    @Override
    public Expression getExpression() {
        return wrapped.getExpression();
    }

    @Override
    public Object getNilReason() {
        return wrapped.getNilReason();
    }
}
