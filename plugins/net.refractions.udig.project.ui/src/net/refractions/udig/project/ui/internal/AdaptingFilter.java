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
package net.refractions.udig.project.ui.internal;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.project.ILayer;

import org.eclipse.core.runtime.IAdaptable;
import org.geotools.feature.Feature;
import org.geotools.filter.AbstractFilter;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterVisitor;

/**
 * Wraps a filter and can adapt to the wrapped filter or to possibly a layer if it is associated with a layer.
 * For example if it is the selection filter of a layer.
 *
 * @author jones
 * @since 1.1.0
 */
public class AdaptingFilter extends AbstractFilter implements Filter, IAdaptable {

    final private Filter wrapped;

    protected Set<Object> adapters = new CopyOnWriteArraySet<Object>();

    public AdaptingFilter(Filter filterA, ILayer layerA) {
        this(filterA);
        addAdapter(layerA);
    }

    public AdaptingFilter(Filter filterA) {
        if( filterA==null )
            throw new NullPointerException("filterA cannot be null"); //$NON-NLS-1$
        wrapped=filterA;
    }


    @SuppressWarnings("unchecked")
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

    public boolean contains( Feature feature ) {
        return wrapped.contains(feature);
    }

    public Filter and( Filter filter ) {
        return wrapped.and(filter);
    }

    public Filter or( Filter filter ) {
        return wrapped.or(filter);
    }

    public Filter not() {
        return wrapped.not();
    }

    public short getFilterType() {
        return wrapped.getFilterType();
    }

    public void accept( FilterVisitor visitor ) {
        wrapped.accept(visitor);
    }
    @Override
    public String toString() {
        return wrapped.toString();
    }

}
