package net.refractions.udig.core.filter;

import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.MultiValuedFilter.MatchAction;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsEqualTo interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsEqualTo extends AdaptingFilter implements PropertyIsEqualTo{

    AdaptingPropertyIsEqualTo( PropertyIsEqualTo filter ) {
        super(filter);
    }
    public Expression getExpression1() {
        return ((BinaryComparisonOperator)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((BinaryComparisonOperator)wrapped).getExpression2();
    }

    public boolean isMatchingCase() {
        return ((BinaryComparisonOperator)wrapped).isMatchingCase();
    }
    public MatchAction getMatchAction() {
        return ((BinaryComparisonOperator)wrapped).getMatchAction();
    }
}
