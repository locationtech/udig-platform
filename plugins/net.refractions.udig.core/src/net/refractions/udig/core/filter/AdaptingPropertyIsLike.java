package net.refractions.udig.core.filter;

import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.expression.Expression;

/**
 * AdaptingFilter that implements PropertyIsLike interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingPropertyIsLike extends AdaptingFilter<PropertyIsLike> implements PropertyIsLike {

    AdaptingPropertyIsLike( PropertyIsLike filter ) {
        super(filter);
    }

    public String getEscape() {
        return wrapped.getEscape();
    }

    public Expression getExpression() {
        return wrapped.getExpression();
    }

    public String getLiteral() {
        return wrapped.getLiteral();
    }

    public String getSingleChar() {
        return wrapped.getSingleChar();
    }

    public String getWildCard() {
        return wrapped.getWildCard();
    }

    public boolean isMatchingCase() {
        return wrapped.isMatchingCase();
    }
    
    public MatchAction getMatchAction() {
        return wrapped.getMatchAction();
    }
}
