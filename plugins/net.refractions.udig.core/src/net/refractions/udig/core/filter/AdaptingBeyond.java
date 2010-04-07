package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Beyond;

/**
 * AdaptingFilter that implements Beyond interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingBeyond extends AdaptingFilter implements Beyond {

    AdaptingBeyond( Beyond filter ) {
        super(filter);
    }

    public double getDistance() {
        return ((Beyond)wrapped).getDistance();
    }

    public String getDistanceUnits() {
        return ((Beyond)wrapped).getDistanceUnits();
    }

    public Expression getExpression1() {
        return ((Beyond)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((Beyond)wrapped).getExpression2();
    }
    
}
