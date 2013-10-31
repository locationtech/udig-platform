/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2007-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.internal.PreferenceConstants;

/**
 * This is a work queue for re-using a group of threads to do Tile work. An example use is the work
 * of saving tiles to disk. When preloading all tiles in a tileset, the number of threads originally
 * got out of hand for saving tiles. This queue allows a group of threads to be reused to do the
 * work. It can also be used to manage the threads for sending tile requests out, but there should
 * be a separate queue for each.
 * 
 * NOTE: This class is not intended to be subclassed or extended.
 * 
 * This class us based on the code example "Java theory and practice: Thread pools and work queues"
 * 
 * @see http://www.ibm.com/developerworks/library/j-jtp0730.html
 * 
 *      This class can be replaced with {@link Executor} now that Doug Lee's work is included in
 *      Java.
 * 
 * @author GDavis
 * 
 */
public class TileWorkerQueue {
    /** Collection of threads and quee of tasks waiting for execution */
    ExecutorService executor;
    final int limit;
    /**
     * Max size could be larger, but beware that larger numbers of threads could mean a much slower
     * system.
     */
    public static final int maxWorkingQueueSize = 64;

    public static final int minWorkingQueueSize = 1;

    public static final int defaultWorkingQueueSize = 16;

    private boolean isTerminated = false;

    public TileWorkerQueue() {
        // check if a preference is set for the max number of threads
        int nThreads = CatalogPlugin.getDefault().getPreferenceStore()
                .getInt(PreferenceConstants.P_WMSCTILE_MAX_CON_REQUESTS);
        if (nThreads <= 0) {
            nThreads = 16;
        }
        executor = Executors.newFixedThreadPool(nThreads);
        limit = nThreads;
    }

    public TileWorkerQueue(int nThreads) {
        if (nThreads > maxWorkingQueueSize)
            nThreads = maxWorkingQueueSize;
        if (nThreads < minWorkingQueueSize)
            nThreads = minWorkingQueueSize;
        executor = Executors.newFixedThreadPool(nThreads);
        limit = nThreads;
    }

    public void execute(Runnable r) {
        executor.execute(r); // submit for execution
    }

    /*
     * Stop and delete all the threads
     */
    public synchronized void dispose() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
            executor = null;
        }
    }
    /** Maximum number of threads support by Executor */
    public int getThreadPoolSize() {
        return limit;
    }
}
