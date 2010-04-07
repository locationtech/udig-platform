package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.DWithin;

/**
 * AdaptingFilter that implements DWithin interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingDWithin extends AdaptingFilter implements DWithin{

    AdaptingDWithin( DWithin filter ) {
        super(filter);
    }
    public double getDistance() {
        return ((DWithin)wrapped).getDistance();
    }

    public String getDistanceUnits() {
        return ((DWithin)wrapped).getDistanceUnits();
    }

    public Expression getExpression1() {
        return ((DWithin)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((DWithin)wrapped).getExpression2();
    }
}
