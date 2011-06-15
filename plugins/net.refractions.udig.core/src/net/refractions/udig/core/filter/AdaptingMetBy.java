package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.temporal.MetBy;

/**
 * Adapting wrapper for MetBy temporal filter.
 * @author Jody
 * @since 1.2.2
 */
public class AdaptingMetBy extends AdaptingFilter<MetBy> implements MetBy {

    AdaptingMetBy( MetBy filter ) {
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
