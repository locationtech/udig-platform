package net.refractions.udig.core.filter;

import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.MultiValuedFilter.MatchAction;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsGreaterThan interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsGreaterThan extends AdaptingFilter implements PropertyIsGreaterThan {

    AdaptingPropertyIsGreaterThan( PropertyIsGreaterThan filter ) {
        super(filter);
    }
    public Expression getExpression1() {
        return ((PropertyIsGreaterThan)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((PropertyIsGreaterThan)wrapped).getExpression2();
    }

    public boolean isMatchingCase() {
        return ((PropertyIsGreaterThan)wrapped).isMatchingCase();
    }
    public MatchAction getMatchAction() {
        return ((BinaryComparisonOperator)wrapped).getMatchAction();
    }
}
