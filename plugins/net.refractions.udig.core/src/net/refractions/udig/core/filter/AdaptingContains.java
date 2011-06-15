package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Contains;

/**
 * AdaptingFilter that implements PropertyIsBetween interface.
 * 
 * @author Jody
 * @since 1.1.0
 * @version 1.2.2
 */
class AdaptingContains extends AdaptingFilter<Contains> implements Contains{

    AdaptingContains( Contains filter ) {
        super(filter);
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
