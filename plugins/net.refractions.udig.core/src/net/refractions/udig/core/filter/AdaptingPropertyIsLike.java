package net.refractions.udig.core.filter;

import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsLike interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsLike extends AdaptingFilter implements PropertyIsLike {

    AdaptingPropertyIsLike( PropertyIsLike filter ) {
        super(filter);
    }

    public String getEscape() {
        return ((PropertyIsLike)wrapped).getEscape();
    }

    public Expression getExpression() {
        return ((PropertyIsLike)wrapped).getExpression();
    }

    public String getLiteral() {
        return ((PropertyIsLike)wrapped).getLiteral();
    }

    public String getSingleChar() {
        return ((PropertyIsLike)wrapped).getSingleChar();
    }

    public String getWildCard() {
        return ((PropertyIsLike)wrapped).getWildCard();
    }

    public boolean isMatchingCase() {
        return ((PropertyIsLike)wrapped).isMatchingCase();
    }
    
}
