/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.simple.SimpleFeatureCollection;
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
public class ProgressFeatureCollection extends AdaptorFeatureCollection {

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
