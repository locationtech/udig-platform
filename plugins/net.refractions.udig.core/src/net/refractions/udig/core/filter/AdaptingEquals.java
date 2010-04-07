package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Equals;

/**
 * AdaptingFilter that implements Equals interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingEquals extends AdaptingFilter {

    AdaptingEquals( Equals filter ) {
        super(filter);
    }
    public Expression getExpression1() {
        return ((Equals)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((Equals)wrapped).getExpression2();
    }
}
