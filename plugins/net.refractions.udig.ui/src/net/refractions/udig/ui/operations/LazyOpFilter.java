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
package net.refractions.udig.ui.operations;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.refractions.udig.internal.ui.UiPlugin;

/**
 * A non-blocking version of the LazyOpFilter. Returns false first then calculates whether it is in
 * fact false or true in a seperate thread and notifies the listeners of the actual state.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class LazyOpFilter implements OpFilter {

    private final OpFilter opFilter;
    private final ILazyOpListener listener;
    private Worker worker;
    final Map<Object, Boolean> cache=new WeakHashMap<Object, Boolean>();
    final boolean blocking, caching;
    final Lock lock =new ReentrantLock();
    
    private IOpFilterListener changeListener = new IOpFilterListener(){

        public void notifyChange( Object changedLayer ) {
            boolean notify=false;
            boolean newResult=false;

            lock.lock();
            try{
                if( !enabled ){
                    UiPlugin.log("Warning listener called even though not enabled", new Exception()); //$NON-NLS-1$
                    return;
                }
                if( opFilter.canCacheResult() ){
                    Boolean removed = cache.get(changedLayer);
                    if (removed != null) {
                        cache.remove(changedLayer);
                        if (listener != null) {
                            newResult = acceptInternal(changedLayer, removed);
                            if (newResult != removed.booleanValue()){
                                notify=true;
                            }
                        }
                    }
                }
                else {
                    notify = true;
                    newResult = accept( changedLayer );
                }
            }finally{
                lock.unlock();
            }
            if( notify )
                listener.notifyResultObtained(newResult);
        }

    };
    private boolean enabled;

    /**
     * Executor used to run enablement calculation in a background thread.
     */
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    /**
     * Default value to use while enablement worker is running.
     */
    public static final boolean DEFAULT_RETURN_VALUE = true;

    public LazyOpFilter( final ILazyOpListener listener, final OpFilter opFilter ) {
        this.listener = listener;
        this.opFilter = opFilter;

        caching=opFilter.canCacheResult();

        blocking = opFilter.isBlocking();
        enabled=false;
    }
    /**
     * Will safely run the enablement worker (protected by a lock).
     */
    public boolean accept( final Object object ) {
        lock.lock();
        try{
            if( !enabled ){
                enabled=true;
                opFilter.addListener(changeListener);
            }
            return acceptInternal(object, DEFAULT_RETURN_VALUE);
        }finally{
            lock.unlock();
        }
    }
    /** Method used by accept; must be protected by lock */
    private boolean acceptInternal( final Object object, boolean defaultReturnValue ) {
        if (worker != null) {
            worker.cancel();
        }

        Boolean result = cache.get(object);
        if (result != null && caching)
            return result;

        if( result==null )
            result=defaultReturnValue;
        
        if (blocking) {
            worker = new Worker(object);
            executor.submit(worker);
        } else {
            result = opFilter.accept(object);
            cache.put(object, result);
        }

        return result;
    }
    /**
     * Internal worker used to check enablement.
     */
    private class Worker implements Runnable {
        private final Object object;
        private volatile boolean cancelled;

        public Worker( final Object object ) {
            this.object = object;
            cancelled = false;
        }

        public void cancel() {
            cancelled = true;
        }

        public void run() {
            boolean result;
            synchronized (LazyOpFilter.this) {
                result = opFilter.accept(object);
                cache.put(object, result);
            }
            if (!cancelled) {
                listener.notifyResultObtained(result);
            }
        }

    }
    /**
     * Listener; used to report on worker progress
     */
    public void addListener( IOpFilterListener listener ) {
        throw new UnsupportedOperationException();
    }
    /**
     * Subclass must override?
     */
    public boolean canCacheResult() {
        throw new UnsupportedOperationException();
    }

    /**
     * Subclass must override?
     */
    public boolean isBlocking() {
        throw new UnsupportedOperationException();
    }

    public void removeListener( IOpFilterListener listener ) {
        throw new UnsupportedOperationException();
    }
    
    public void disable(){
        lock.lock();
        try{
            enabled=false;
            opFilter.removeListener(changeListener);
        }finally{
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "LazyOpFilter "+this.opFilter;
    }
}
