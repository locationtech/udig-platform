/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, 2015 Refractions Research Inc. and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

import java.util.Collection;
import java.util.concurrent.locks.Lock;

import org.locationtech.udig.ui.UDIGDisplaySafeLock;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;

/**
 * Synchronizes reads and writes. When iterating its recommend to use {@link #syncedIteration(IEListVisitor)} otherwise 
 * ConcurrentModificationExceptions are potential possible
 * 
 * @author Jesse
 * @author Frank Gasdorf
 * @author Erdal Karaca
 * 
 * @since 1.1.0
 */
public class SynchronizedEObjectWithInverseResolvingEList<E> extends EObjectWithInverseResolvingEList<E> implements ISynchronizedEListIteration<E> {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -7051345525714825128L;

    private transient final Lock              lock             = new UDIGDisplaySafeLock();

    public SynchronizedEObjectWithInverseResolvingEList( Class<E> dataClass, InternalEObject owner,
            int featureID, int inverseFeatureID ) {
        super(dataClass, owner, featureID, inverseFeatureID);
    }

    @Override
    protected E assign( int index, E object ) {
        lock.lock();
        try {
            return super.assign(index, object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected E doRemove( int index ) {
        lock.lock();
        try {
            return super.doRemove(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void doClear() {
        lock.lock();
        try {
            super.doClear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E get( int index ) {
        lock.lock();
        try {
            return super.get(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains( Object object ) {
        lock.lock();
        try {
            return super.contains(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsAll( Collection<?> collection ) {
        lock.lock();
        try {
            return super.containsAll(collection);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals( Object object ) {
        lock.lock();
        try {
            return super.equals(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int hashCode() {
        lock.lock();
        try {
            return super.hashCode();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void syncedIteration(IEListVisitor<E> visitor) {
        lock.lock();
        try {
            for( final E object : this ) {
                visitor.visit(object);
            }
        } finally {
            lock.unlock();
        }
    }
}
