/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.core.filter;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IAdaptable;
import org.geotools.filter.text.cql2.CQL;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;

/**
 * Wraps a filter and can adapt to the wrapped filter or to possibly
 * a layer if it is associated with a layer.
 * <p>
 * This class does not implement Filter directly; subclasses
 * are used in order to implement specific subclasses of Filter that
 * have meaning (ie AdaptingId, AdaptingBetween, AdaptingEquals).
 * 
 * @author jones
 * @since 1.1.0
 */
public class AdaptingFilter<F extends Filter> implements Filter, IAdaptable {
    protected F wrapped;
    protected Set<Object> adapters = new CopyOnWriteArraySet<Object>();
    
    AdaptingFilter(F filter) {
        if( filter==null )
            throw new NullPointerException("filterA cannot be null"); //$NON-NLS-1$
    	wrapped = filter;
	}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object getAdapter( Class adapter ) {
        for( Object obj : adapters ) {
            if( adapter.isAssignableFrom(obj.getClass()) )
                return obj;
        }
        if( Filter.class.isAssignableFrom(adapter)){
            return wrapped;
        }
        return null;
    }

    public void addAdapter( Object adapter ) {
        if( adapter==null )
            throw new NullPointerException("adapter cannont be null"); //$NON-NLS-1$
        adapters.add(adapter);
    }

    public boolean removeAdapter( Object adapter ) {
        if( adapter==null )
            throw new NullPointerException("adapter cannont be null"); //$NON-NLS-1$
        return adapters.remove(adapter);
    }

	public Object accept(FilterVisitor visitor, Object extraData) {
		return wrapped.accept(visitor, extraData);
	}

	public boolean evaluate(Object object) {
		return wrapped.evaluate(object);
	}
	@Override
	public String toString() {
	    return "Adapting:"+CQL.toCQL( wrapped );
	}
}
