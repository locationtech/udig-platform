package net.refractions.udig.core.filter;

import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsGreaterThanOrEqualTo interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsGreaterThanOrEqualTo extends AdaptingFilter<PropertyIsGreaterThanOrEqualTo> implements PropertyIsGreaterThanOrEqualTo{

    AdaptingPropertyIsGreaterThanOrEqualTo( PropertyIsGreaterThanOrEqualTo filter ) {
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
