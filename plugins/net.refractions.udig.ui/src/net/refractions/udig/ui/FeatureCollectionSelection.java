/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Adapt a feature collection as a Selection so it can be used with StructedViewers.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class FeatureCollectionSelection implements IStructuredSelection, IBlockingSelection {
    Collection<Iterator> openIterators=new ArrayList<Iterator>(); 
    FeatureCollection<SimpleFeatureType, SimpleFeature> wrapped;
    private volatile SimpleFeature firstElement;
    public FeatureCollectionSelection( FeatureCollection<SimpleFeatureType, SimpleFeature> selectedFeatures ) {
        this.wrapped=selectedFeatures;
    }
    @Override
    protected void finalize() throws Throwable {
        for( Iterator iterator : openIterators ) {
            wrapped.close(iterator);
        }
        super.finalize();
    }
    public Object getFirstElement() {
        if( isEmpty() )
            throw new NoSuchElementException("SimpleFeature Collection is empty, there is no first element"); //$NON-NLS-1$
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

    public Iterator iterator() {
        Iterator iter=wrapped.iterator();
        openIterators.add(iter);
        return iter;
    }

    public int size() {
        return wrapped.size();
    }

    public Object[] toArray() {
        return toList().toArray();
    }

    @SuppressWarnings("unchecked")
    public List toList() {
        if( wrapped instanceof List)
            return (List) wrapped;
        LinkedList arrayList = new LinkedList();
        Iterator iter=wrapped.iterator();
        try{
            while(iter.hasNext()){
                arrayList.add(iter.next());
            }
        }finally{
            wrapped.close(iter);
        }
        return arrayList;
    }

    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

}
