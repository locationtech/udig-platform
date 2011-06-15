package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.temporal.TOverlaps;

/**
 * Adapting wrapper for TOverlaps temporal filter.
 * @author Jody
 * @since 1.2.2
 */
public class AdaptingTOverlaps extends AdaptingFilter<TOverlaps> implements TOverlaps {

    AdaptingTOverlaps( TOverlaps filter ) {
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
