package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.temporal.BegunBy;

/**
 * Adapting wrapper for BegunBy temporal filter.
 * @author Jody
 * @since 1.2.2
 */
public class AdaptingBegunBy extends AdaptingFilter<BegunBy> implements BegunBy {

    AdaptingBegunBy( BegunBy filter ) {
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
