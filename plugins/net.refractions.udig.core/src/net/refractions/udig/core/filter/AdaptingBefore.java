package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.temporal.Before;

/**
 * Adapting wrapper for temporal filter Before.
 * @author Jody
 * @since 1.2.2
 */
public class AdaptingBefore extends AdaptingFilter<Before> implements Before {

    AdaptingBefore( Before filter ) {
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
