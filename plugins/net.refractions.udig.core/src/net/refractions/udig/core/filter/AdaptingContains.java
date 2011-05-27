package net.refractions.udig.core.filter;

import org.opengis.filter.MultiValuedFilter.MatchAction;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Contains;

/**
 * AdaptingFilter that implements PropertyIsBetween interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingContains extends AdaptingFilter implements Contains{

    AdaptingContains( Contains filter ) {
        super(filter);
    }
    public Expression getExpression1() {
        return ((Contains)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((Contains)wrapped).getExpression2();
    }
    public MatchAction getMatchAction() {
        return ((Contains)wrapped).getMatchAction();
    }
}
