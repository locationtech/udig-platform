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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import net.refractions.udig.core.IProvider;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A selection that uses a set of fids to indicate the selection in the collection of features.  The actual selection
 * is the features indicated by the fids.
 * @author Jesse
 * @since 1.1.0
 */
public class FidSelection implements IStructuredSelection {

    private final Set<String> fids;
    private final IProvider<Collection<SimpleFeature>> provider;
    private volatile SimpleFeature firstElement;
    private volatile LinkedList<SimpleFeature> list;
    private volatile Object[] array;

    /**
     * New Instance.  
     * @param fids
     * @param provider
     */
    public FidSelection( Set<String> fids, IProvider<Collection<SimpleFeature>> provider ) {
        this.fids=new HashSet<String>(fids);
        this.provider=provider;
    }

    public Object getFirstElement() {
        if( isEmpty() )
            throw new NoSuchElementException("Selection is empty."); //$NON-NLS-1$
        
        return doFirstElement();
    }

    private Object doFirstElement() {
        if( firstElement==null ){
            synchronized (this) {
                if( firstElement==null ){
                    for( SimpleFeature feature : provider.get() ) {
                        if( fids.contains(feature.getID() )){
                            firstElement=feature;
                            break;
                        }
                    }
                }
                
            }
        }
        return firstElement;
    }

    public Iterator iterator() {
        final HashSet<String> fids=new HashSet<String>(this.fids);
        final Iterator<SimpleFeature> featureIter=this.provider.get().iterator();
        
        return new Iterator(){
            private SimpleFeature next;
            private SimpleFeature live;
            
            public boolean hasNext() {
                if( next!=null )
                    return true;
                
                while( featureIter.hasNext() && !fids.isEmpty() && next==null ){
                    SimpleFeature current=featureIter.next();
                    if( fids.remove(current.getID()) ){
                        next=current;
                    }
                }
                
                return next!=null;
            }

            public Object next() {
                if( !hasNext() ){
                    throw new NoSuchElementException("No more elements in iterator"); //$NON-NLS-1$
                }
                live=next;
                next=null;
                return live;
            }

            public void remove() {
                FidSelection.this.fids.remove(live.getID());
            }
            
        };
    }

    public int size() {
        return toList().size();
    }

    public Object[] toArray() {
        if( array==null ){
            synchronized (this) {
                if( array==null ){
                    array=toList().toArray();
                }
            }
        }
        return array;
    }

    public List toList() {
        if( list==null ){
            synchronized (this) {
                if( list==null ){
                    list=new LinkedList<SimpleFeature>();
                    Iterator iter=iterator();
                    while( iter.hasNext() ){
                        list.add((SimpleFeature) iter.next());
                    }
                }
            }
        }

        return list;
    }

    public boolean isEmpty() {
        return doFirstElement()==null;
    }

}
