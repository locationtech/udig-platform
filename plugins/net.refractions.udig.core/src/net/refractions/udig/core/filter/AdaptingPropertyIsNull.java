package net.refractions.udig.core.filter;

import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsNull interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsNull extends AdaptingFilter<PropertyIsNull> implements PropertyIsNull{

    AdaptingPropertyIsNull( PropertyIsNull filter ) {
        super(filter);
    }
    
    public Expression getExpression() {
        return wrapped.getExpression();
    }
}
