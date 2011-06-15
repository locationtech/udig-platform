package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.temporal.Begins;

/**
 * Adapting wrapper for temporal filter Begins.
 * 
 * @author Jody
 * @since 1.2.2
 */
public class AdaptingBegins extends AdaptingFilter<Begins> implements Begins {
    AdaptingBegins( Begins filter ) {
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
