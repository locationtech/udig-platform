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
package net.refractions.udig.ui;

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.collection.AdaptorFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * This is a single use collection because the monitor is used by the iterators and
 * since iterator doesn't take one then it can only be used once.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ProgressFeatureCollection extends AdaptorFeatureCollection implements FeatureCollection<SimpleFeatureType, SimpleFeature> {

    protected FeatureCollection<SimpleFeatureType, SimpleFeature> delegate;
    protected IProgressMonitor monitor;
    
    public ProgressFeatureCollection( FeatureCollection<SimpleFeatureType, SimpleFeature> delegate, IProgressMonitor monitor ) {
    	super("Progress Listener", delegate.getSchema());
        this.delegate = delegate;
        this.monitor = monitor;
    }

    @Override
    protected void closeIterator( Iterator close ) {
        delegate.close(close);
    }

    @Override
    protected Iterator openIterator() {
        final Iterator iterator = delegate.iterator();
        return new Iterator(){

            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Object next() {
                monitor.worked(1);
                return iterator.next();
            }

            public void remove() {
                iterator.next();
            }
            
        };
    }

    @Override
    public int size() {
        return delegate.size();
    }

}
