package net.refractions.udig.core.filter;

import org.opengis.filter.PropertyIsNil;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsNull interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsNil extends AdaptingFilter<PropertyIsNil> implements PropertyIsNil{

    AdaptingPropertyIsNil( PropertyIsNil filter ) {
        super(filter);
    }
    
    @Override
    public Expression getExpression() {
        return wrapped.getExpression();
    }

    @Override
    public Object getNilReason() {
        return wrapped.getNilReason();
    }
}
