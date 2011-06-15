package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Touches;

/**
 * AdaptingFilter that implements Touches interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingTouches extends AdaptingFilter<Touches> implements Touches {

    AdaptingTouches( Touches filter ) {
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
