package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Within;

/**
 * AdaptingFilter that implements Within interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingWithin extends AdaptingFilter<Within> implements Within {

    AdaptingWithin( Within filter ) {
        super(filter);
    }

    public Expression getExpression1() {
        return wrapped.getExpression1();
    }

    public Expression getExpression2() {
        return wrapped.getExpression1();
    }
    public MatchAction getMatchAction() {
        return wrapped.getMatchAction();
    }
}
