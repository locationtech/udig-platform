package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.temporal.TContains;

/**
 * Adapting wrapper for TContains temporal filter.
 * @author Jody
 * @since 1.2.2
 */
public class AdaptingTContains extends AdaptingFilter<TContains> implements TContains {

    AdaptingTContains( TContains filter ) {
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
