/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class UDIGDisplaySafeLockTest {

    int WAIT_LENGTH = 10000;
    private UDIGDisplaySafeLock lock;

    @Before
    public void setUp() throws Exception {
        lock = new UDIGDisplaySafeLock();
    }

    enum State {
        NO_LOCK, THREAD_HAS_LOCK, DISPLAY_WAITING_FOR_LOCK, THREAD_RELEASED_LOCK, DISPLAY_HAS_LOCK, THREAD_WAITING_FOR_LOCK
    }

    @Test
    public void testLock() throws Throwable {
        final Thread displayThread = Thread.currentThread();
        final Set<State> states = new HashSet<State>();
        final Lock lockLock = new ReentrantLock();
        final Throwable[] exception = new Throwable[1];
        final boolean[] passed = new boolean[1];

        Runnable other = new Runnable(){
            public void run() {
                try {
                    lockLock.lock();
                    lock.lock();
                    states.add(State.THREAD_HAS_LOCK);
                    lockLock.unlock();

                    while( !states.contains(State.DISPLAY_WAITING_FOR_LOCK) ) {
                        Thread.sleep(100);
                    }

                    Display.getDefault().asyncExec(new Runnable(){
                        public void run() {
                            passed[0] = true;
                        }
                    });

                    PlatformGIS.wait(200, WAIT_LENGTH, new WaitCondition(){

                        public boolean isTrue() {
                            return passed[0] = true;
                        }

                    }, null);

                    assertTrue(passed[0]);
                    states.remove(State.THREAD_HAS_LOCK);
                    lock.unlock();

                    while( !states.contains(State.DISPLAY_HAS_LOCK) )
                        Thread.sleep(200);

                    lockLock.lock();

                    states.add(State.THREAD_WAITING_FOR_LOCK);
                    lock.lock();
                    states.add(State.THREAD_HAS_LOCK);
                    lockLock.unlock();

                    PlatformGIS.wait(200, WAIT_LENGTH, new WaitCondition(){

                        public boolean isTrue() {
                            return passed[0];
                        }

                    }, null);

                    lock.unlock();
                    states.remove(State.THREAD_HAS_LOCK);
                    states.add(State.THREAD_RELEASED_LOCK);
                } catch (Throwable e) {
                    exception[0] = e;
                    displayThread.interrupt();
                }
            }
        };

        Thread t = new Thread(other, "Lock Test Thread"); //$NON-NLS-1$
        t.start();

        // wait til thread has lock
        UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

            public boolean isTrue() {
                return states.contains(State.THREAD_HAS_LOCK);
            }

        }, false);

        lockLock.lock();
        states.add(State.DISPLAY_WAITING_FOR_LOCK);
        lock.lock();
        states.remove(State.DISPLAY_WAITING_FOR_LOCK);
        states.add(State.DISPLAY_HAS_LOCK);
        lockLock.unlock();

        // make sure thread has released lock.
        if (exception[0] != null)
            throw exception[0];
        assertFalse(states.contains(State.THREAD_HAS_LOCK));
        assertTrue(passed[0]);

        passed[0] = false;

        UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

            public boolean isTrue() {
                return states.contains(State.THREAD_WAITING_FOR_LOCK);
            }

        }, false);

        if (exception[0] != null)
            throw exception[0];

        states.remove(State.DISPLAY_HAS_LOCK);
        lock.unlock();

        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue() {
                return states.contains(State.THREAD_HAS_LOCK);
            }

        }, false);
        if (exception[0] != null)
            throw exception[0];

        passed[0] = true;

        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue() {
                return states.contains(State.THREAD_RELEASED_LOCK);
            }

        }, false);
        if (exception[0] != null)
            throw exception[0];

    }

    @Test
    public void testUnlockOrder() throws Exception {

        final boolean[] canQuit = new boolean[1];
        canQuit[0] = false;

        final boolean[] gotLock = new boolean[1];
        gotLock[0] = false;

        Runnable thread1 = new Runnable(){
            public void run() {
                lock.lock();
                gotLock[0] = true;
                try {
                    PlatformGIS.wait(200, WAIT_LENGTH, new WaitCondition(){

                        public boolean isTrue() {
                            return canQuit[0];
                        }

                    }, null);

                    lock.unlock();
                } catch (InterruptedException e) {
                    throw (RuntimeException) new RuntimeException().initCause(e);
                }
            }
        };

        Thread t1 = new Thread(thread1);
        t1.start();

        final boolean[] waiting = new boolean[1];
        waiting[0] = false;

        final boolean[] gotLock2 = new boolean[1];
        gotLock2[0] = false;
        Runnable thread2 = new Runnable(){
            public void run() {
                waiting[0] = true;
                lock.lock();
                gotLock2[0] = true;
            }
        };

        Thread t2 = new Thread(thread2);
        t2.start();

        UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

            public boolean isTrue() {
                return gotLock[0];
            }

        }, true);

        canQuit[0] = true;
        lock.lock();

        assertFalse(gotLock2[0]);

        lock.unlock();

        UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

            public boolean isTrue() {
                return gotLock2[0];
            }

        }, true);

    }

    @Test
    public void testTryLock() throws Exception {
        assertTrue(lock.tryLock());
        
        assertTrue(lock.isHeldByCurrentThread());
        final boolean[] result=new boolean[1];
        result[0]=true;
        new Thread(){
            @Override
            public void run() {
                result[0]=lock.tryLock();
            }
        }.start();
        
        UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

            public boolean isTrue() {
                return !result[0];
            }
            
        }, true);
    }

    @Test
    public void testTryLockTimout() throws Throwable {
        assertTrue(lock.tryLock(100, TimeUnit.MICROSECONDS));
        
        assertTrue(lock.isHeldByCurrentThread());
        final boolean[] ready=new boolean[1];
        ready[0]=false;
        
        final boolean[] obtained=new boolean[1];
        obtained[0]=false;
        final Throwable[] exception=new Throwable[1];
        new Thread(){
            @Override
            public void run() {
                ready[0]=true;
                try {
                    obtained[0]=lock.tryLock(100, TimeUnit.SECONDS);
                } catch (Throwable e) {
                    exception[0]=e;
                }
            }
        }.start();
        
        UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

            public boolean isTrue() {
                return ready[0];
            }
            
        }, false);
        if( exception[0]!=null )
            throw exception[0];

        lock.unlock();
        
        UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

            public boolean isTrue() {
                return obtained[0];
            }
            
        }, false);
        if( exception[0]!=null )
            throw exception[0];
        
        
    }

    @Test
    public void testReentrance() throws Exception {
        final Thread current = Thread.currentThread();
        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    PlatformGIS.wait(100, WAIT_LENGTH, new WaitCondition(){

                        public boolean isTrue() {
                            return !lock.isLocked();
                        }

                    }, null);
                } catch (InterruptedException e) {
                    current.interrupt();
                }
            }
        };

        t.start();

        lock.lockInterruptibly();
        lock.lockInterruptibly();

        assertTrue(lock.isHeldByCurrentThread());
        assertEquals(2, lock.getHoldCount());

        lock.lockInterruptibly();

        assertEquals(3, lock.getHoldCount());

        lock.unlock();
        assertTrue(lock.isHeldByCurrentThread());
        assertEquals(2, lock.getHoldCount());

        lock.unlock();
        assertTrue(lock.isHeldByCurrentThread());
        assertEquals(1, lock.getHoldCount());

        lock.unlock();
        assertFalse(lock.isHeldByCurrentThread());
        assertEquals(0, lock.getHoldCount());

    }

    @Test
    public void testWaitQueueLength() throws Exception {
        final Condition condition = lock.newCondition();
        assertEquals(0, lock.getWaitQueueLength(condition));
        assertFalse(lock.hasWaiters(condition));

        final Exception[] ex=new Exception[1];
        
        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    lock.lock();
                    condition.await();
                    lock.unlock();
                } catch (Exception e) {
                    ex[0]=e;
                }
            }
        };

        t.start();

        UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

            public boolean isTrue() {
                return lock.getWaitQueueLength(condition) == 1 || ex[0]!=null;
            }

        }, true);
        if( ex[0]!=null )
            throw ex[0];
        assertTrue(lock.hasWaiters(condition));

        lock.lock();
        condition.signal();
        lock.unlock();

        UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

            public boolean isTrue() {
                return lock.getWaitQueueLength(condition) == 0;
            }

        }, true);

        assertFalse(lock.hasWaiters(condition));
    }
    
    @Test
    public void testLockInterruptibly() throws Exception {
        final boolean[] interrupted = new boolean[1];
        final boolean[] isLocked = new boolean[1];
        lock.lock();

        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    isLocked[0] = true;
                    lock.lockInterruptibly();
                } catch (InterruptedException e) {
                    interrupted[0] = true;
                }
            }
        };

        t.start();

        UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

            public boolean isTrue() {
                return isLocked[0];
            }

        }, true);

        t.interrupt();

        UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

            public boolean isTrue() {
                return interrupted[0];
            }

        }, true);

        assertTrue(interrupted[0]);
        lock.unlock();
    }
    
    @Test
    public void testConditionWithDisplay() throws Exception {
        lock.lock();
        try {
            final Condition condition = lock.newCondition();
            Display.getCurrent().asyncExec(new Runnable(){

                public void run() {
                    lock.lock();
                    try {
                        condition.signal();
                    } finally {
                        lock.unlock();
                    }
                }

            });

            condition.await();
        } finally {
            lock.unlock();
        }
    }
    
    @Ignore("fails in tycho")
    @Test
    public void testSignalAll() throws Exception {
            final Condition condition = lock.newCondition();
            final Exception[] exception=new Exception[1];
            final boolean[] awake=new boolean[2];
            Thread t=new Thread(){

                public void run() {
                    lock.lock();
                    try {
                        condition.await();
                        awake[0]=true;
                    } catch (InterruptedException e) {
                        exception[0]=e;
                    } finally {
                        lock.unlock();
                    }
                }

            };
            t.start();

            Thread t2=new Thread(){

                public void run() {
                    lock.lock();
                    try {
                        condition.await();
                        awake[1]=true;
                    } catch (InterruptedException e) {
                        exception[0]=e;
                    } finally {
                        lock.unlock();
                    }
                }

            };
            t2.start();

            UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

                public boolean isTrue() {
                    return lock.getWaitQueueLength(condition)==2;
                }
                
            }, false);
            if( exception[0]!=null )
                throw exception[0];
            lock.lock();
            condition.signalAll();
            lock.unlock();
            UDIGTestUtil.inDisplayThreadWait(WAIT_LENGTH, new WaitCondition(){

                public boolean isTrue() {
                    return lock.getWaitQueueLength(condition)==0;
                }
                
            }, false);
            if( exception[0]!=null )
                throw exception[0];
            assertTrue(awake[0]);
            assertTrue(awake[1]);
            
    }
    
    /**
     * This is a special case that we found causes a bug.  See <a href="http://jira.codehaus.org/browse/UDIG-1007"/>
     *
     * @throws Exception
     */
    @Test
    public void testLockSignalLockUnlockUnlock() throws Exception {
        final Display display=Display.getCurrent();
        final Set<State> state=new CopyOnWriteArraySet<State>();
        final boolean[] done=new boolean[1];
        done[0]=false;
        
        Thread t=new Thread(){

            public void run() {
                lock.lock();
                state.add(UDIGDisplaySafeLockTest.State.THREAD_HAS_LOCK);
                final boolean[] locked=new boolean[1];
                locked[0]=false;

                display.asyncExec(new Runnable(){
                    public void run() {
                        display.asyncExec(new Runnable(){
                            public void run() {
                                state.add(UDIGDisplaySafeLockTest.State.DISPLAY_WAITING_FOR_LOCK);
                                lock.lock();
                                lock.unlock();

                                state.remove(UDIGDisplaySafeLockTest.State.DISPLAY_WAITING_FOR_LOCK);
                            }
                        });
                        lock.lock();
                        locked[0]=true;
                        lock.unlock();
                    }
                });
                
                long timeout=200000;
                long start=System.currentTimeMillis();
                
                while( !state.contains(UDIGDisplaySafeLockTest.State.DISPLAY_WAITING_FOR_LOCK) && timeout>(System.currentTimeMillis()-start) ){
                    synchronized (this) {
                        try {
                            wait(200);
                        } catch (InterruptedException e) {
                            throw (RuntimeException) new RuntimeException( ).initCause( e );
                        }
                    }
                }
                
                lock.unlock();
                state.remove(UDIGDisplaySafeLockTest.State.THREAD_HAS_LOCK);
                
                start=System.currentTimeMillis();
                while( (!locked[0]) && timeout>(System.currentTimeMillis()-start) ){
                    synchronized (this) {
                        try {
                            wait(200);
                        } catch (InterruptedException e) {
                            throw (RuntimeException) new RuntimeException( ).initCause( e );
                        }
                    }
                }

                if( !locked[0] ){
                    display.getThread().interrupt();
                    Exception exception=new Exception("Test Failed in a blocking manner so killing tests... Sorry no other way"); //$NON-NLS-1$
                    exception.printStackTrace();
                    System.exit(-1);
                }
                done[0]=true;
            }

        };
        t.start();

        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue() {
                return state.contains(State.THREAD_HAS_LOCK);
            }
            
        }, false);

        lock.lock();

        UDIGTestUtil.inDisplayThreadWait(5000, new WaitCondition(){

            public boolean isTrue() {
                return done[0];
            }
            
        }, true);
        lock.unlock();
    }
}
