package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Disjoint;

/**
 * AdaptingFilter that implements Disjoint interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingDisjoint extends AdaptingFilter implements Disjoint {

    AdaptingDisjoint( Disjoint filter ) {
        super(filter);
    }
    public Expression getExpression1() {
        return ((Disjoint)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((Disjoint)wrapped).getExpression2();
    }
}
