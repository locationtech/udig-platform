package net.refractions.udig.core.filter;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BBOX;

class AdaptingBBOX extends AdaptingFilter implements BBOX {

    AdaptingBBOX( BBOX filter ) {
        super(filter);
    }

    public double getMaxX() {
        return ((BBOX)wrapped).getMaxX();
    }

    public double getMaxY() {
        return ((BBOX)wrapped).getMaxY();
    }

    public double getMinX() {
        return ((BBOX)wrapped).getMinX();
    }

    public double getMinY() {
        return ((BBOX)wrapped).getMinY();
    }

    public String getPropertyName() {
        return ((BBOX)wrapped).getPropertyName();
    }

    public String getSRS() {
        return ((BBOX)wrapped).getSRS();
    }

    public Expression getExpression1() {
        return ((BBOX)wrapped).getExpression1();
    }

    public Expression getExpression2() {
        return ((BBOX)wrapped).getExpression2();
    }    
}