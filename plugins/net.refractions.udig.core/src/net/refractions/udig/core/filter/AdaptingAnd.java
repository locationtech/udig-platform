package net.refractions.udig.core.filter;

import java.util.List;

import org.opengis.filter.And;
import org.opengis.filter.Filter;

/**
 * AdaptingFilter that implements And interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingAnd extends AdaptingFilter<And> implements And {

    AdaptingAnd( And filter ) {
        super(filter);
    }

    public List<Filter> getChildren() {
        return wrapped.getChildren();
    }
    
}
