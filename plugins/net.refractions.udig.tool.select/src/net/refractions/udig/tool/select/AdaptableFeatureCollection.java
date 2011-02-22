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
package net.refractions.udig.tool.select;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IAdaptable;
import org.geotools.data.FeatureReader;
import org.geotools.feature.CollectionListener;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureList;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.visitor.FeatureVisitor;
import org.geotools.filter.Filter;
import org.geotools.filter.SortBy;
import org.geotools.util.ProgressListener;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * A feature collection that adapts to other objects.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class AdaptableFeatureCollection implements FeatureCollection, IAdaptable{

    protected Set<Object> adapters = new CopyOnWriteArraySet<Object>();
    private final FeatureCollection wrapped;
    public AdaptableFeatureCollection( final FeatureCollection wrapped ) {
        super();
        this.wrapped = wrapped;
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter ) {
        for( Object obj : adapters ) {
            if( adapter.isAssignableFrom(obj.getClass()) )
                return obj;
        }
        return null;
    }

    public void addAdapter( Object adapter ) {
        adapters.add(adapter);
    }

    public boolean removeAdapter( Object adapter ) {
        return adapters.remove(adapter);
    }

    public void clearAdapters() {
        adapters.clear();
    }

    public void accepts( FeatureVisitor arg0, ProgressListener arg1 ) throws IOException {
        wrapped.accepts(arg0, arg1);
    }
    public void addListener( CollectionListener arg0 ) throws NullPointerException {
        wrapped.addListener(arg0);
    }
    public void close( FeatureIterator arg0 ) {
        wrapped.close(arg0);
    }
    public void close( Iterator arg0 ) {
        wrapped.close(arg0);
    }
    public FeatureIterator features() {
        return wrapped.features();
    }
    public FeatureType getFeatureType() {
        return wrapped.getFeatureType();
    }
    public FeatureType getSchema() {
        return wrapped.getSchema();
    }
    public void removeListener( CollectionListener arg0 ) throws NullPointerException {
        wrapped.removeListener(arg0);
    }
    public FeatureList sort( SortBy arg0 ) {
        return wrapped.sort(arg0);
    }
    public FeatureCollection subCollection( Filter arg0 ) {
        return wrapped.subCollection(arg0);
    }
    public Iterator iterator() {
        return wrapped.iterator();
    }
    public void purge() {
        wrapped.purge();
    }
    @SuppressWarnings("unchecked")
    public boolean add( Object o ) {
        return wrapped.add(o);
    }
    @SuppressWarnings("unchecked")
    public boolean addAll( Collection c ) {
        return wrapped.addAll(c);
    }
    public void clear() {
        wrapped.clear();
    }
    public boolean contains( Object o ) {
        return wrapped.contains(o);
    }
    @SuppressWarnings("unchecked")
    public boolean containsAll( Collection c ) {
        return wrapped.containsAll(c);
    }
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }
    public boolean remove( Object o ) {
        return wrapped.remove(o);
    }
    @SuppressWarnings("unchecked")
    public boolean removeAll( Collection c ) {
        return wrapped.removeAll(c);
    }
    @SuppressWarnings("unchecked")
    public boolean retainAll( Collection c ) {
        return wrapped.retainAll(c);
    }
    public int size() {
        return wrapped.size();
    }
    public Object[] toArray() {
        return wrapped.toArray();
    }
    @SuppressWarnings("unchecked")
    public Object[] toArray( Object[] a ) {
        return wrapped.toArray(a);
    }
    @SuppressWarnings("deprecation")
    public FeatureCollection collection() throws IOException {
        return wrapped.collection();
    }
    public Envelope getBounds() {
        return wrapped.getBounds();
    }
    @SuppressWarnings("deprecation")
    public int getCount() throws IOException {
        return wrapped.getCount();
    }
    @SuppressWarnings("deprecation")
    public FeatureReader reader() throws IOException {
        return wrapped.reader();
    }
    public Object getAttribute( String arg0 ) {
        return wrapped.getAttribute(arg0);
    }
    public Object getAttribute( int arg0 ) {
        return wrapped.getAttribute(arg0);
    }
    public Object[] getAttributes( Object[] arg0 ) {
        return wrapped.getAttributes(arg0);
    }
    public Geometry getDefaultGeometry() {
        return wrapped.getDefaultGeometry();
    }
    public String getID() {
        return wrapped.getID();
    }
    public int getNumberOfAttributes() {
        return wrapped.getNumberOfAttributes();
    }
    @SuppressWarnings("deprecation")
    public FeatureCollection getParent() {
        return wrapped.getParent();
    }
    public void setAttribute( int arg0, Object arg1 ) throws IllegalAttributeException, ArrayIndexOutOfBoundsException {
        wrapped.setAttribute(arg0, arg1);
    }
    public void setAttribute( String arg0, Object arg1 ) throws IllegalAttributeException {
        wrapped.setAttribute(arg0, arg1);
    }
    public void setDefaultGeometry( Geometry arg0 ) throws IllegalAttributeException {
        wrapped.setDefaultGeometry(arg0);
    }
    public void setParent( FeatureCollection arg0 ) {
        wrapped.setParent(arg0);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((adapters == null) ? 0 : adapters.hashCode());
        result = PRIME * result + ((wrapped == null) ? 0 : wrapped.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AdaptableFeatureCollection other = (AdaptableFeatureCollection) obj;
        if (adapters == null) {
            if (other.adapters != null)
                return false;
        } else if (!adapters.equals(other.adapters))
            return false;
        if (wrapped == null) {
            if (other.wrapped != null)
                return false;
        } else if (!wrapped.equals(other.wrapped))
            return false;
        return true;
    }
}
