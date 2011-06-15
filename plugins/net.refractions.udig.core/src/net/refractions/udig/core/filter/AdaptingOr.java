package net.refractions.udig.core.filter;

import java.util.List;

import org.opengis.filter.Filter;
import org.opengis.filter.Or;

/**
 * AdaptingFilter that implements Or interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingOr extends AdaptingFilter<Or> implements Or {

    AdaptingOr( Or filter ) {
        super(filter);
    }

    public List<Filter> getChildren() {
        return wrapped.getChildren();
    }
    
}
