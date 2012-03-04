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
package net.refractions.udig.project.internal.impl;

import java.util.Collection;
import java.util.concurrent.locks.Lock;

import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;

/**
 * Synchronizes reads and writes but not within synchronization block during notification. When
 * iterating make sure to use: synchronized( list ){ do iterations }
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SynchronizedEObjectWithInverseResolvingEList extends EObjectWithInverseResolvingEList {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -7051345525714825128L;

    private transient final Lock              lock             = new UDIGDisplaySafeLock();

    public SynchronizedEObjectWithInverseResolvingEList( Class dataClass, InternalEObject owner,
            int featureID, int inverseFeatureID ) {
        super(dataClass, owner, featureID, inverseFeatureID);
    }

    @Override
    protected Object assign( int index, Object object ) {
        lock.lock();
        try {
            return super.assign(index, object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected Object doRemove( int index ) {
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
    public Object get( int index ) {
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
    public boolean containsAll( Collection collection ) {
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
}
