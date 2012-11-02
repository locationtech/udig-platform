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
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.collection.AdaptorFeatureCollection;
import org.geotools.feature.collection.BaseFeatureCollection;
import org.geotools.feature.collection.DecoratingSimpleFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

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
        super( delegate );
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
