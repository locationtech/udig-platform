package net.refractions.udig.core.filter;

import org.opengis.filter.Filter;
import org.opengis.filter.Not;

/**
 * AdaptingFilter that implements Not interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingNot extends AdaptingFilter<Not> implements Not {

    AdaptingNot( Not filter ) {
        super(filter);
    }

    public Filter getFilter() {
        return wrapped.getFilter();
    }

}
