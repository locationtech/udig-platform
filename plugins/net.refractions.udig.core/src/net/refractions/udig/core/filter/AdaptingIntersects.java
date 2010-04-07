package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Intersects;

/**
 * AdaptingFilter that implements Intersects interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingIntersects extends AdaptingFilter implements Intersects {

    AdaptingIntersects( Intersects filter ) {
        super(filter);
    }
    public Expression getExpression1() {
        return ((Intersects)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((Intersects)wrapped).getExpression2();
    }
}
