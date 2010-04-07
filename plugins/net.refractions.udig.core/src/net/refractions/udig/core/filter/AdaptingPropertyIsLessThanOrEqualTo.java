package net.refractions.udig.core.filter;

import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsBetween interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsLessThanOrEqualTo extends AdaptingFilter implements PropertyIsLessThanOrEqualTo{

    AdaptingPropertyIsLessThanOrEqualTo( PropertyIsLessThanOrEqualTo filter ) {
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
    
}
