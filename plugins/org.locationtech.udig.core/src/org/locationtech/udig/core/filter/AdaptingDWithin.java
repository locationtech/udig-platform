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
package org.locationtech.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.DWithin;

/**
 * AdaptingFilter that implements DWithin interface.
 * 
 * @author Jody
 * @since 1.1.0
 * @version 1.2.2
 */
class AdaptingDWithin extends AdaptingFilter<DWithin> implements DWithin{

    AdaptingDWithin( DWithin filter ) {
        super(filter);
    }
    public double getDistance() {
        return wrapped.getDistance();
    }

    public String getDistanceUnits() {
        return wrapped.getDistanceUnits();
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
}
