package net.refractions.udig.core.filter;

import java.util.Set;

import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

/**
 * AdaptingFilter that implements Id interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingId extends AdaptingFilter<Id> implements Id {

    AdaptingId( Id filter ) {
        super(filter);
    }
    
    public Set<Object> getIDs() {
        return wrapped.getIDs();
    }

    public Set<Identifier> getIdentifiers() {
        return wrapped.getIdentifiers();
    }
    public String toString() {
        return "Adapting:"+wrapped.getIDs();
    }
}
