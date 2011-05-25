package net.refractions.udig.core.filter;

import org.opengis.filter.MultiValuedFilter.MatchAction;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Crosses;

/**
 * AdaptingFilter that implements Crosses interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingCrosses extends AdaptingFilter implements Crosses {

    AdaptingCrosses( Crosses filter ) {
        super(filter);
    }
    public Expression getExpression1() {
        return ((Crosses)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((Crosses)wrapped).getExpression2();
    }
    public MatchAction getMatchAction() {
        return ((Crosses)wrapped).getMatchAction();
    }
}
