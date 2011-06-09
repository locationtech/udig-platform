package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Beyond;

/**
 * AdaptingFilter that implements Beyond interface.
 * 
 * @author Jody
 * @since 1.1.0
 * @version 1.2.2
 */
class AdaptingBeyond extends AdaptingFilter<Beyond> implements Beyond {

    AdaptingBeyond( Beyond filter ) {
        super(filter);
    }

    public double getDistance() {
        return wrapped.getDistance();
    }

    public String getDistanceUnits() {
        return wrapped.getDistanceUnits();
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
