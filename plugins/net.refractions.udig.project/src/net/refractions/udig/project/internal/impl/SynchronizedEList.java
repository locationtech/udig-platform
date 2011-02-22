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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;

import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.emf.common.util.EList;

/**
 * A decorator that synchronizes on all methods of the list.
 *
 * When iterating make sure to use:
 * list.lock();
 * try{
 *    do iterations
 * }finally{
 *    list.unlock();
 * }
 *
 * @author Jesse
 * @since 1.1.0
 */
public class SynchronizedEList<T> implements EList<T> {
    private EList<T> wrapped;
    Lock lock=new UDIGDisplaySafeLock();

    /**
     * Lock this list.
     */
    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public SynchronizedEList( EList<T> list ){
        wrapped=list;
    }


    public void add( int arg0, T arg1 ) {
        lock.lock();
        try{
            wrapped.add(arg0, arg1);
        }finally{
            lock.unlock();
        }
    }


    public boolean add( T arg0 ) {
        lock.lock();
        try{
            return wrapped.add(arg0);
        }finally{
            lock.unlock();
        }
    }


    public boolean addAll( Collection<? extends T> arg0 ) {
        lock.lock();
        try{
            return wrapped.addAll(arg0);
        }finally{
            lock.unlock();
        }
    }


    public boolean addAll( int arg0, Collection<? extends T> arg1 ) {
        lock.lock();
        try{
            return wrapped.addAll(arg0, arg1);
        }finally{
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try{
            wrapped.clear();
        }finally{
            lock.unlock();
        }
    }

    public boolean contains( Object arg0 ) {
        lock.lock();
        try{
            return wrapped.contains(arg0);
        }finally{
            lock.unlock();
        }
    }


    public boolean containsAll( Collection<?> arg0 ) {
        lock.lock();
        try{
            return wrapped.containsAll(arg0);
        }finally{
            lock.unlock();
        }
    }

    @Override
	public boolean equals( Object arg0 ) {
        lock.lock();
        try{
            return wrapped.equals(arg0);
        }finally{
            lock.unlock();
        }
    }

    public T get( int arg0 ) {
        lock.lock();
        try{
            return wrapped.get(arg0);
        }finally{
            lock.unlock();
        }
    }

    @Override
	public int hashCode() {
        lock.lock();
        try{
            return wrapped.hashCode();
        }finally{
            lock.unlock();
        }
    }

    public int indexOf( Object arg0 ) {
        lock.lock();
        try{
            return wrapped.indexOf(arg0);
        }finally{
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try{
            return wrapped.isEmpty();
        }finally{
            lock.unlock();
        }
    }

    public Iterator<T> iterator() {
            return listIterator();
    }

    public int lastIndexOf( Object arg0 ) {
        lock.lock();
        try{
            return wrapped.lastIndexOf(arg0);
        }finally{
            lock.unlock();
        }
    }

    public ListIterator<T> listIterator() {
        return listIterator(0);
    }


    public ListIterator<T> listIterator(int arg0) {
		return new ListIterator<T>() {
			ListIterator<T> iter = wrapped.listIterator();

			public boolean hasNext() {
				lock();
				try {
					return iter.hasNext();
				} finally {
					unlock();
				}

			}

			public T next() {
				lock();
				try {
					return iter.next();
				} finally {
					unlock();
				}
			}

			public void remove() {
				lock();
				try {
					iter.remove();
				} finally {
					unlock();
				}
			}

			public void add(T o) {
				lock();
				try {
					iter.add(o);
				} finally {
					unlock();
				}
			}

			public boolean hasPrevious() {
				lock();
				try {
					return iter.hasPrevious();
				} finally {
					unlock();
				}
			}

			public int nextIndex() {
				lock();
				try {
					return iter.nextIndex();
				} finally {
					unlock();
				}
			}

			public T previous() {
				lock();
				try {
					return iter.previous();
				} finally {
					unlock();
				}
			}

			public int previousIndex() {
				lock();
				try {
					return iter.previousIndex();
				} finally {
					unlock();
				}
			}

			public void set(T o) {
				lock();
				try {
					iter.set(o);
				} finally {
					unlock();
				}
			}

		};
	}

    public T move( int arg0, int arg1 ) {
        lock.lock();
        try{
            return wrapped.move(arg0, arg1);
        }finally{
            lock.unlock();
        }
    }

    public void move( int arg0, T arg1 ) {
        lock.lock();
        try{
            wrapped.move(arg0, arg1);
        }finally{
            lock.unlock();
        }
    }

    public T remove( int arg0 ) {
        lock.lock();
        try{
            return wrapped.remove(arg0);
        }finally{
            lock.unlock();
        }
    }

    public boolean remove( Object arg0 ) {
        lock.lock();
        try{
            return wrapped.remove(arg0);
        }finally{
            lock.unlock();
        }
    }


    public boolean removeAll( Collection<?> arg0 ) {
        lock.lock();
        try{
            return wrapped.removeAll(arg0);
        }finally{
            lock.unlock();
        }
    }


    public boolean retainAll( Collection<?> arg0 ) {
        lock.lock();
        try{
            return wrapped.retainAll(arg0);
        }finally{
            lock.unlock();
        }
    }


    public T set( int arg0, T arg1 ) {
        lock.lock();
        try{
            return wrapped.set(arg0, arg1);
        }finally{
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try{
            return wrapped.size();
        }finally{
            lock.unlock();
        }

    }

    public List<T> subList( int arg0, int arg1 ) {
        lock.lock();
        try{
            return wrapped.subList(arg0, arg1);
        }finally{
            lock.unlock();
        }

    }

    public Object[] toArray() {
        lock.lock();
        try{
            return wrapped.toArray();
        }finally{
            lock.unlock();
        }

    }


    public<E> E[] toArray( E[] arg0 ) {
        lock.lock();
        try{
            return wrapped.toArray(arg0);
        }finally{
            lock.unlock();
        }
    }

	public static<T> EList<T> create(EList<T> adapters) {
		return new SynchronizedEList<T>(adapters);
	}
}
