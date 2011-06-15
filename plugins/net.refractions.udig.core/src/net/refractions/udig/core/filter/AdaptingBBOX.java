package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BBOX;

@SuppressWarnings("deprecation")
class AdaptingBBOX extends AdaptingFilter<BBOX> implements BBOX {

    AdaptingBBOX( BBOX filter ) {
        super(filter);
    }

    public double getMaxX() {
        return wrapped.getMaxX();
    }

    public double getMaxY() {
        return wrapped.getMaxY();
    }

    public double getMinX() {
        return wrapped.getMinX();
    }

    public double getMinY() {
        return wrapped.getMinY();
    }

    public String getPropertyName() {
        return wrapped.getPropertyName();
    }

    public String getSRS() {
        return wrapped.getSRS();
    }

    public Expression getExpression1() {
        return wrapped.getExpression1();
    }

    public Expression getExpression2() {
        return wrapped.getExpression2();
    }

    public MatchAction getMatchAction() {
        return wrapped.getMatchAction();
    }    
}