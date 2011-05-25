package net.refractions.udig.core.filter;

import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.MultiValuedFilter.MatchAction;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Touches;

/**
 * AdaptingFilter that implements Touches interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingTouches extends AdaptingFilter implements Touches {

    AdaptingTouches( Touches filter ) {
        super(filter);
    }
    public Expression getExpression1() {
        return ((Touches)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((Touches)wrapped).getExpression1();
    }
    public MatchAction getMatchAction() {
        return ((Touches)wrapped).getMatchAction();
    }
}
