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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.swt.widgets.Display;

class UDIGDisplaySafeCondition implements Condition{
    /** SafeCondition owningLock field */
    private final UDIGDisplaySafeLock owningLock;
    private final Condition nonDisplayCondition;
    private final Condition displayCondition;
    private volatile Set<Thread> displayNotified=new HashSet<Thread>();
    
    UDIGDisplaySafeCondition( UDIGDisplaySafeLock lock ) {
        if( lock==null )
            throw new NullPointerException("Lock cannot be null"); //$NON-NLS-1$
        owningLock = lock;
        
        nonDisplayCondition=owningLock.internalLock.newCondition();
        displayCondition=owningLock.internalLock.newCondition();
    }
    
    public void await() throws InterruptedException {
        doAwait( -1, null, true);
    }

    /**
     * @param wait
     * @param unit
     * @param allowInterrupts
     * @return see {@linkplain Condition#await(long, TimeUnit)}  
     * @throws InterruptedException
     */
    boolean doAwait(  long wait, TimeUnit unit, boolean allowInterrupts ) throws InterruptedException {
        owningLock.internalLock.lock();
        try{
            checkState();
            owningLock.unlock();
            
            if ( Display.getCurrent()==null ){
                if( !allowInterrupts ){
                    // findbugs note:  this is correct behaviour.  I know its not in a while loop
                    nonDisplayCondition.awaitUninterruptibly();
                    return true;
                }else{
                    if( unit==null ){
                        nonDisplayCondition.await();
                        return true;
                    }else
                        return nonDisplayCondition.await(wait, unit);
                }
            }else{
                return displayAwait(wait, unit, allowInterrupts)<=0;
            }
        }finally{
            // findbugs note:  this is correct behaviour.
            owningLock.lock();
            owningLock.internalLock.unlock();
        }
    }

    private long displayAwait(long time, TimeUnit unit, boolean allowInterrupts) throws InterruptedException {
        long remaining;
        displayNotified.add(Thread.currentThread());
        long start=System.nanoTime();
        
        if( unit!=null ){
            remaining=TimeUnit.NANOSECONDS.convert(time, unit);
        }else{
            remaining=-1;
        }
        
        long nextInterval=TimeUnit.NANOSECONDS.convert(100, TimeUnit.MILLISECONDS);;
        Display current = Display.getCurrent();
        while( nextInterval>0 && displayNotified.contains(Thread.currentThread())){
            if( unit!=null ){
                nextInterval=Math.min(nextInterval, remaining);
            }
            // unlock while running display events.
            owningLock.internalLock.unlock();
            boolean readAndDispatch = true;
            try{
				readAndDispatch = current.readAndDispatch();
            }catch (Throwable e) {
            	UiPlugin.log("error occurred in a display event", e);
			}

            // findbugs note:  this is correct behaviour.  It is closed outside this method
            owningLock.internalLock.lock();
            if( !readAndDispatch ){
                try{
                    displayCondition.await(nextInterval, TimeUnit.NANOSECONDS);
                }catch (InterruptedException e) {
                    if( allowInterrupts )
                        throw e;
                }
            }
            remaining-=System.nanoTime()-start;
        }
        return remaining;
    }

    public boolean await( long time, TimeUnit unit ) throws InterruptedException {
            return doAwait(time, unit, true);
    }

    public long awaitNanos( long nanosTimeout ) throws InterruptedException {
            long remaining;
            if( Display.getCurrent()==null ){
                remaining=nonDisplayCondition.awaitNanos(nanosTimeout);
            }else{
                remaining=displayAwait(nanosTimeout, TimeUnit.NANOSECONDS, true);
            }
            return remaining;
    }

    public void awaitUninterruptibly() {
            try {
                doAwait(-1, null, false);
            } catch (InterruptedException e) {
                throw new Error("This should not be permitted to happen", e); //$NON-NLS-1$
            }
    }

    public boolean awaitUntil( Date deadline ) throws InterruptedException {
            checkState();
            long waitTime=deadline.getTime()-System.currentTimeMillis();
            
            return doAwait(waitTime, TimeUnit.MILLISECONDS, true);
    }

    public void signal() {
        owningLock.internalLock.lock();
        try{
            checkState();
            
            if( !displayNotified.isEmpty() ){
                Thread next = displayNotified.iterator().next();
                displayNotified.remove(next);
                displayCondition.signal();
            }else{
                nonDisplayCondition.signal();
            }
        }finally{
            owningLock.internalLock.unlock();
        }
    }

    public void signalAll() {
        owningLock.internalLock.lock();
        try{
            checkState();
            if( !displayNotified.isEmpty() ){
                displayNotified.clear();
                displayCondition.signalAll();
            }
            nonDisplayCondition.signalAll();
        }finally{
            owningLock.internalLock.unlock();
        }
   }

    private void checkState() {
        if(  !owningLock.isHeldByCurrentThread() )
            throw new IllegalStateException("current thread does not own lock!!!"); //$NON-NLS-1$
    }

    public int getWaitQueueLength() {
        
        int count=displayNotified.size();
        
        int i = count+owningLock.internalLock.getWaitQueueLength(this.nonDisplayCondition);
        return i;
    }

    public boolean isOwner( UDIGDisplaySafeLock lock ) {
        return owningLock==lock;
    }
    
}