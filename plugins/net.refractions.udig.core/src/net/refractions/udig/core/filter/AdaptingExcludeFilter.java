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

import org.geotools.factory.CommonFactoryFinder;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;

/**
 * We cannot represent Filter.EXCLUDE as an AdaptingFilter, using 1 EqualsTo 0 instead.
 * 
 * <p>
 * Using an AdaptingFitler to represent selection is a bit short sited; this is the
 * only case we have to hack away at right now. Our hack is almost successful int that
 * it wont show up for code using a FilterVisitor; but it will be noticed
 * by coding doing an instanceof check.
 * <p>
 * @author Jody Garnett (Refractions Research Inc)
 * @since 1.2.0
 */
class AdaptingExcludeFilter extends AdaptingFilter<ExcludeFilter> implements PropertyIsEqualTo {
    AdaptingExcludeFilter() {
        super( Filter.EXCLUDE );
    }

    public Expression getExpression1() {
        return CommonFactoryFinder.getFilterFactory2(null).literal( 1 );
    }

    public Expression getExpression2() {
        return CommonFactoryFinder.getFilterFactory2(null).literal( 0 );
    }

    public boolean isMatchingCase() {
        return false;
    } 
    public MatchAction getMatchAction() {
        return MatchAction.ANY;
    }
}
