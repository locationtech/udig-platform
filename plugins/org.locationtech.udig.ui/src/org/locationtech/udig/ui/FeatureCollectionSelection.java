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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Adapt a feature collection as a Selection so it can be used with StructedViewers.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class FeatureCollectionSelection implements IStructuredSelection, IBlockingSelection {
    
    Collection<Iterator<Feature>> openIterators=new ArrayList<Iterator<Feature>>(); 
    FeatureCollection<SimpleFeatureType, SimpleFeature> wrapped;
    private volatile SimpleFeature firstElement;
    
    public FeatureCollectionSelection( FeatureCollection<SimpleFeatureType, SimpleFeature> selectedFeatures ) {
        this.wrapped=selectedFeatures;
    }
    @Override
    protected void finalize() throws Throwable {
       

        for (Iterator<Feature> iterator : openIterators) {
            if (iterator instanceof Closeable) {
                ((Closeable) iterator).close();
            }
        }
        super.finalize();
    }
    
    public Object getFirstElement() {
        if( isEmpty() ){
            throw new NoSuchElementException("SimpleFeature Collection is empty, there is no first element"); //$NON-NLS-1$
        }
        if( firstElement==null ){
            synchronized (this) {
                if( firstElement==null ){
                    FeatureIterator<SimpleFeature> iter = wrapped.features();
                    try{
                        firstElement=iter.next();
                    }finally{
                        iter.close();
                    }
                }
            }
        }
        return firstElement;
    }
    
    class SelectionIterator implements Iterator<Feature>,Closeable {
        FeatureIterator<SimpleFeature> delegate = null;
        public SelectionIterator(FeatureIterator<SimpleFeature> features) {
            delegate = features;
        }

        @Override
        public boolean hasNext() {
            if( delegate == null ) return false;
            boolean hasNext = delegate.hasNext();
            
            if(!hasNext){ // autoclose!
                delegate.close();
                delegate = null;
            }
            return hasNext;
        }

        @Override
        public Feature next() {
            if( delegate == null ){
                throw new NoSuchElementException();
            }
            return delegate.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
            if( delegate != null ){
                delegate.close();
                delegate = null;
            }
        }
        
    }    

    @SuppressWarnings("rawtypes")
    public Iterator iterator() {
        
        if( wrapped instanceof Collection){
            return ((Collection)wrapped).iterator();
        } else {
            Iterator iter=new SelectionIterator( wrapped.features() );
            openIterators.add(iter);            
            return iter;
        }
    }
    
    public int size() {
        return wrapped.size();
    }

    public Object[] toArray() {
        return toList().toArray();
    }

    @SuppressWarnings("unchecked")
    public List toList() {
        
        if (wrapped instanceof List){
            return (List) wrapped;
        }
        LinkedList<SimpleFeature> arrayList = new LinkedList<SimpleFeature>();
        FeatureIterator<SimpleFeature> iter = wrapped.features();
        try {
            while (iter.hasNext()) {
                arrayList.add(iter.next());
            }
        } finally {
            iter.close();
        }
        return arrayList;
    }

    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

}
