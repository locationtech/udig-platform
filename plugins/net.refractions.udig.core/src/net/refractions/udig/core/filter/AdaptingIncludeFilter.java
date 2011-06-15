package net.refractions.udig.core.filter;

import org.geotools.factory.CommonFactoryFinder;
import org.opengis.filter.Filter;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;

/**
 * We cannot represent Filter.INCLUDE as an AdaptingFilter (since the class is final), using 1 EqualsTo 1 instead.
 * <p>
 * Using an AdaptingFitler to represent selection is a bit short sited; this is the
 * only case we have to hack away at right now. Our hack is almost successful int that
 * it wont show up for code using a FilterVisitor; but it will be noticed
 * by coding doing an instanceof check.
 * <p>
 * @author Jody Garnett (Refracitons Research Inc)
 * @since 1.2.0
 */
class AdaptingIncludeFilter extends AdaptingFilter<IncludeFilter> implements PropertyIsEqualTo {
    
    AdaptingIncludeFilter() {
        super( Filter.INCLUDE );
    }

    public Expression getExpression1() {
        return CommonFactoryFinder.getFilterFactory2(null).literal( 1 );
    }

    public Expression getExpression2() {
        return CommonFactoryFinder.getFilterFactory2(null).literal( 1 );
    }

    public boolean isMatchingCase() {
        return false;
    } 
    public MatchAction getMatchAction() {
        return MatchAction.ANY;
    }
}