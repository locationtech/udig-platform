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

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.collection.DecoratingSimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

/**
 * This is a single use collection because the monitor is used by the iterators and
 * since iterator doesn't take one then it can only be used once.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ProgressFeatureCollection extends DecoratingSimpleFeatureCollection {

    protected IProgressMonitor monitor;
    protected int progress = -1;
    protected int size = -1;    
    
    
    public ProgressFeatureCollection( SimpleFeatureCollection delegate, IProgressMonitor monitor ) {
    	super(delegate);
        this.monitor = monitor;
    }

    @Override
    public SimpleFeatureIterator features() {
        final SimpleFeatureIterator iterator = delegate.features();
        
        if( size == -1 ){
            size = delegate.size();
            monitor.beginTask(delegate.getID(), size );
        }
        return new SimpleFeatureIterator(){
            int index = 0;
            public boolean hasNext() {
                return iterator.hasNext();
            }
            public SimpleFeature next() {
                index++;
                if( index > progress){
                    progress = index;
                    monitor.worked(1);
                }
                return iterator.next();
            }
            public void close() {
                monitor.done();
                iterator.close();                
            }            
        };
    }

}
