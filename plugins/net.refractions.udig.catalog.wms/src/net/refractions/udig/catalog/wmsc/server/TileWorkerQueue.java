/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2007-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.wmsc.server;

import java.util.LinkedList;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.internal.PreferenceConstants;

/**
 * This is a work queue for re-using a group of threads to do Tile work.  An example use
 * is the work of saving tiles to disk.  When preloading all tiles in a tileset, the number of
 * threads originally got out of hand for saving tiles.  This queue allows a group
 * of threads to be reused to do the work.  It can also be used to manage the threads
 * for sending tile requests out, but there should be a separate queue for each.
 * 
 * NOTE: This class is not intended to be subclassed or extended.
 * 
 * The base of this class was copied from IBM's online resource
 * "Java theory and practice: Thread pools and work queues"
 * @see http://www.ibm.com/developerworks/library/j-jtp0730.html
 * 
 * @author GDavis
 *
 */
public class TileWorkerQueue {
    private final int nThreads;
    private final PoolWorker[] threads;
    private final LinkedList<Runnable> queue;

    /**
     * Max size could be larger, but beware that larger numbers of threads
     * could mean a much slower system.
     */
    public static final int maxWorkingQueueSize = 64;
    public static final int minWorkingQueueSize = 1;
    public static final int defaultWorkingQueueSize = 16;
    
    private boolean isTerminated = false;
    
    public TileWorkerQueue() {
    	// check if a preference is set for the max number of threads
    	int nThreads = CatalogPlugin.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_WMSCTILE_MAX_CON_REQUESTS);
    	if (nThreads <= 0) nThreads = defaultWorkingQueueSize;
        this.nThreads = nThreads;
        queue = new LinkedList<Runnable>();
        threads = new PoolWorker[nThreads];
        initThreads();
    }    

    public TileWorkerQueue(int nThreads) {
    	if (nThreads > maxWorkingQueueSize) nThreads = maxWorkingQueueSize;
    	if (nThreads < minWorkingQueueSize) nThreads = minWorkingQueueSize;
        this.nThreads = nThreads;
        queue = new LinkedList<Runnable>();
        threads = new PoolWorker[nThreads];
        initThreads();
    }
    
    private void initThreads() {
        for (int i=0; i<this.nThreads; i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }
    
    public int getThreadPoolSize() {
    	return this.nThreads;
    }

    public void execute(Runnable r) {
        synchronized(queue) {
            queue.addLast(r);
            queue.notify();
        }
    }
    
    public boolean isQueueEmpty() {
        synchronized(queue) {
            return queue.isEmpty();
        }
    }

    private class PoolWorker extends Thread {
        public void run() {
            Runnable r;

            while (!isTerminated) {
                synchronized(queue) {
                    while (queue.isEmpty() && !isTerminated) {
                        try
                        {
                            queue.wait();
                        }
                        catch (InterruptedException ignored)
                        {
                        }
                    }

                    if (!queue.isEmpty()) {
                    	r = (Runnable) queue.removeFirst();
                    }
                    else {
                    	r = null;
                    }
                }

                // If we don't catch RuntimeException, 
                // the pool could leak threads
                try {
                    r.run();
                }
                catch (RuntimeException e) {
                    // You might want to log something here
                }
            }
        }
    }
    
    /*
     * Stop and delete all the threads
     */
    public void dispose() {
    	this.isTerminated = true;
        for (int i=0; i<this.nThreads; i++) {
            threads[i] = null;
        }
        synchronized(queue) {
            queue.clear();
            queue.notifyAll();
        }        
    }
}

