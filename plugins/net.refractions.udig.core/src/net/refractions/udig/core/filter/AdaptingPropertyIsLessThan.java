package net.refractions.udig.core.filter;

import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsLessThan interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsLessThan extends AdaptingFilter implements PropertyIsLessThan {

    AdaptingPropertyIsLessThan( PropertyIsLessThan filter ) {
        super(filter);
    }
    public Expression getExpression1() {
        return ((PropertyIsLessThan)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((PropertyIsLessThan)wrapped).getExpression2();
    }

    public boolean isMatchingCase() {
        return ((PropertyIsLessThan)wrapped).isMatchingCase();
    }
}
