package net.refractions.udig.core.filter;

import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsEqualTo interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsNotEqualTo extends AdaptingFilter implements PropertyIsNotEqualTo  {

    AdaptingPropertyIsNotEqualTo( PropertyIsNotEqualTo filter ) {
        super(filter);
    }

    public Expression getExpression1() {
        return ((PropertyIsNotEqualTo)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((PropertyIsNotEqualTo)wrapped).getExpression1();
    }

    public boolean isMatchingCase() {
        return ((PropertyIsNotEqualTo)wrapped).isMatchingCase();
    }
    
}
