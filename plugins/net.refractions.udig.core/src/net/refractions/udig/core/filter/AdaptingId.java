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
class AdaptingId extends AdaptingFilter implements Id {

    AdaptingId( Id filter ) {
        super(filter);
    }
    
    public Set<Object> getIDs() {
        return ((Id)wrapped).getIDs();
    }

    @SuppressWarnings("unchecked")
    public Set<Identifier> getIdentifiers() {
        return ((Id)wrapped).getIdentifiers();
    }
    public String toString() {
        Id id = (Id) wrapped;
        return "Adapting:"+id.getIDs();
    }
}
