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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.refractions.udig.internal.ui.Trace;
import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.swt.widgets.Display;

/**
 * This lock is reentrant and guarantees that a display thread will not block,
 * it will continue to call {@link Display#readAndDispatch()}.  The Display thread
 * gets priority over non-display threads.
 * 
 * API is copied from the {@link ReentrantLock}
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class UDIGDisplaySafeLock implements Lock {
    private static class NullLock extends UDIGDisplaySafeLock{
        public NullLock( ReentrantLock internalLock ) {
            this.internalLock=internalLock;
        }

        @Override
        public boolean isHeldByCurrentThread() {
            return true;
        }
        
        @Override
        public boolean isLocked() {
            return true;
        }
        
        @Override
        public void unlock() {
            internalLock.unlock();
        }
        
        @Override
        public void lock() {
            internalLock.lock();
        }
        
        public void lockInterruptibly() throws InterruptedException {
            internalLock.lockInterruptibly();
        }
        @Override
        protected void init() {
            // do nothing
        }
    };
    
    /**
     * Indicates the number of times the lock has been locked. 
     */
    private AtomicInteger lockCount;
    ReentrantLock internalLock;
    /**
     * Thread that holds the lock.
     */
    volatile Thread lockThread;
    /**
     * The condition that is used for Display thread 
     */
    private UDIGDisplaySafeCondition displayCondition;
    /**
     * condition used for other threads.
     */
    private UDIGDisplaySafeCondition condition;
    private static int ID=0;
    private int id=ID++;;
    
    public UDIGDisplaySafeLock() {
        init();
    }

    protected void init() {
        lockCount=new AtomicInteger(0);
        internalLock=new ReentrantLock();
        condition=new UDIGDisplaySafeCondition(new NullLock(internalLock));
        displayCondition=new UDIGDisplaySafeCondition(new NullLock(internalLock));
    }
    
    /**
     * Queries the number of holds on this lock by the current thread.
     *
     * <p>A thread has a hold on a lock for each lock action that is not 
     * matched by an unlock action.
     *
     * <p>The hold count information is typically only used for testing and
     * debugging purposes. For example, if a certain section of code should
     * not be entered with the lock already held then we can assert that
     * fact:
     *
     * <pre>
     * class X {
     *   ReentrantLock lock = new ReentrantLock();
     *   // ...     
     *   public void m() { 
     *     assert lock.getHoldCount() == 0;
     *     lock.lock();
     *     try {
     *       // ... method body
     *     } finally {
     *       lock.unlock();
     *     }
     *   }
     * }
     * </pre>
     *
     * @return the number of holds on this lock by the current thread,
     * or zero if this lock is not held by the current thread.
     */
    public int getHoldCount() {
        internalLock.lock();
        try{
            return lockCount.get();
        }finally{
            internalLock.unlock();
        }
    }
    
    /**
     * Returns an estimate of the number of threads waiting on the
     * given condition associated with this lock. Note that because
     * timeouts and interrupts may occur at any time, the estimate
     * serves only as an upper bound on the actual number of waiters.
     * This method is designed for use in monitoring of the system
     * state, not for synchronization control.
     * @param condition the condition
     * @return the estimated number of waiting threads.
     * @throws IllegalMonitorStateException if this lock 
     * is not held
     * @throws IllegalArgumentException if the given condition is
     * not associated with this lock
     * @throws NullPointerException if condition null
     */ 
    public int getWaitQueueLength( Condition condition ) {
        internalLock.lock();
        try{
            
            if( condition == null )
                throw new NullPointerException("Condition cannot be null"); //$NON-NLS-1$
            if( !( condition instanceof UDIGDisplaySafeCondition)  )
                    throw new IllegalStateException("Condition is not owned by this lock!"); //$NON-NLS-1$
            UDIGDisplaySafeCondition casted=(UDIGDisplaySafeCondition) condition;
            if(  !casted.isOwner(this) )
                throw new IllegalStateException("Condition is not owned by this lock!"); //$NON-NLS-1$
            return casted.getWaitQueueLength();
            
        }finally{
            internalLock.unlock();
        }
    }
    /**
     * Queries whether any threads are waiting on the given condition
     * associated with this lock. Note that because timeouts and
     * interrupts may occur at any time, a <tt>true</tt> return does
     * not guarantee that a future <tt>signal</tt> will awaken any
     * threads.  This method is designed primarily for use in
     * monitoring of the system state.
     * @param condition the condition
     * @return <tt>true</tt> if there are any waiting threads.
     * @throws IllegalMonitorStateException if this lock 
     * is not held
     * @throws IllegalArgumentException if the given condition is
     * not associated with this lock
     * @throws NullPointerException if condition null
     */ 
    public boolean hasWaiters( Condition condition ) {
        internalLock.lock();
        try{
            return getWaitQueueLength(condition)>0;
        }finally{
            internalLock.unlock();
        }
    }
    /**
     * Queries if this lock is held by the current thread.
     *
     * <p>Analogous to the {@link Thread#holdsLock} method for built-in
     * monitor locks, this method is typically used for debugging and
     * testing. For example, a method that should only be called while
     * a lock is held can assert that this is the case:
     *
     * <pre>
     * class X {
     *   ReentrantLock lock = new ReentrantLock();
     *   // ...
     *
     *   public void m() { 
     *       assert lock.isHeldByCurrentThread();
     *       // ... method body
     *   }
     * }
     * </pre>
     *
     * <p>It can also be used to ensure that a reentrant lock is used
     * in a non-reentrant manner, for example:
     *
     * <pre>
     * class X {
     *   ReentrantLock lock = new ReentrantLock();
     *   // ...
     *
     *   public void m() { 
     *       assert !lock.isHeldByCurrentThread();
     *       lock.lock();
     *       try {
     *           // ... method body
     *       } finally {
     *           lock.unlock();
     *       }
     *   }
     * }
     * </pre>
     * @return <tt>true</tt> if current thread holds this lock and 
     * <tt>false</tt> otherwise.
     */
    public boolean isHeldByCurrentThread() {
        internalLock.lock();
        try{
            return Thread.currentThread()==lockThread;
        }finally{
            internalLock.unlock();
        }

    }
    /**
     * Returns true if locked by a different thread.
     *
     * @return true if locked by a different thread.
     */
    public boolean isLocked() {
        internalLock.lock();
        try{
            return lockThread!=null;
        }finally{
            internalLock.unlock();
        }
    }
    public void lock() {
        try{
            lock(false);
        } catch (InterruptedException e) {
                throw new IllegalStateException("This is illegal, interrupted exception should not occur here."); //$NON-NLS-1$
        }
    }

    private void lock( boolean allowInterrupts ) throws InterruptedException {
        internalLock.lock();
        try{
            while( lockThread!=null && lockThread!=Thread.currentThread() ){
                wait(-1, null, allowInterrupts);
            }
            doLock();

        }finally{
            internalLock.unlock();
        }    
    }

    private void doLock() {
        if( UiPlugin.isDebugging(Trace.UDIG_DISPLAY_SAFE_LOCK) )
            UiPlugin.trace(getClass(), Thread.currentThread().getName()+" is Locking "+id+". Number of entrances: "+(lockCount.get()+1), null); //$NON-NLS-1$ //$NON-NLS-2$
        if( lockCount.compareAndSet(0, 1) ){
            lockThread=Thread.currentThread();
        }else{
            if( lockThread==Thread.currentThread() ){
                lockCount.incrementAndGet();
            }else{
                if( UiPlugin.isDebugging(Trace.UDIG_DISPLAY_SAFE_LOCK) )
                    UiPlugin.trace(getClass(), 
                            "Illegal state.  Trying to increment lock count even though lock is not held by current Thread.  " + //$NON-NLS-1$
                            "\n\tcurrentThread = "+Thread.currentThread()+" Lock thread=="+lockThread, null); //$NON-NLS-1$ //$NON-NLS-2$
                throw new IllegalStateException("Illegal state.  Trying to increment lock count even though lock is not held by current Thread.  " + //$NON-NLS-1$
                        "\n\tcurrentThread = "+Thread.currentThread()+" Lock thread=="+lockThread); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
    private void wait( long timeout, TimeUnit unit, boolean allowInterrupts ) throws InterruptedException {
        if (allowInterrupts && Thread.interrupted() )
            throw new InterruptedException("Thread has been interrupted"); //$NON-NLS-1$
        
        if( UiPlugin.isDebugging(Trace.UDIG_DISPLAY_SAFE_LOCK) )
            UiPlugin.trace(getClass(), Thread.currentThread().getName()+" is waiting for Lock "+id+". Interruptible="+allowInterrupts, null); //$NON-NLS-1$ //$NON-NLS-2$

        if( Display.getCurrent()!=null ){
            displayCondition.doAwait(timeout, unit, allowInterrupts);
        }else
            condition.doAwait(timeout, unit, allowInterrupts);
    }

    public void lockInterruptibly() throws InterruptedException {
        lock(true);
    }
    public Condition newCondition() {
        internalLock.lock();
        try{
            return new UDIGDisplaySafeCondition(this);
        }finally{
            internalLock.unlock();
        }
    }
    public boolean tryLock() {
        internalLock.lock();
        try{
            if( !isLocked() || isHeldByCurrentThread() ){
                lock();
                return true;
            }
            return false;
        }finally{
            internalLock.unlock();
        }
    }
    public boolean tryLock( long timeout, TimeUnit unit ) throws InterruptedException {
        internalLock.lock();
        try{
            if( tryLock() ){
                return true;
            }else{
                this.wait(timeout, unit, true);
                return tryLock();
            }
        }finally{
            internalLock.unlock();
        }
    }
    
    public void unlock() {
        internalLock.lock();
        try{
            if( this.lockThread!=Thread.currentThread() )
                throw new IllegalStateException("Current thread does not own lock!  Lock owner == "+lockThread); //$NON-NLS-1$
            
            if( UiPlugin.isDebugging(Trace.UDIG_DISPLAY_SAFE_LOCK) )
                UiPlugin.trace(getClass(), Thread.currentThread().getName()+" is unlocking Lock "+id+" remaining holds="+(lockCount.get()-1), null); //$NON-NLS-1$ //$NON-NLS-2$

            if( lockCount.get()>0 ){
                if( displayCondition.getWaitQueueLength()>0 ){
                    displayCondition.signal();
                } else{
                    condition.signal();
                }
                if( lockCount.decrementAndGet()==0 ){
                    lockThread=null;
                }
            }
            
        }finally{
            internalLock.unlock();
        }
    }
}
