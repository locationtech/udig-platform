package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Overlaps;

/**
 * AdaptingFilter that implements Overlaps interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingOverlaps extends AdaptingFilter implements Overlaps {

    AdaptingOverlaps( Overlaps filter ) {
        super(filter);
    }

    public Expression getExpression1() {
        return ((BinarySpatialOperator)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((BinarySpatialOperator)wrapped).getExpression2();
    }
    
}
