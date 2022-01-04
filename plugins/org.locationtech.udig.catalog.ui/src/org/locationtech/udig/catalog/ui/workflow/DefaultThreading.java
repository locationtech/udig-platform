/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.workflow;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;

/**
 * Threading strategy that uses eclipse Jobs to run through the workflow
 *
 * @author jesse
 * @since 1.1.0
 */
public class DefaultThreading implements ThreadingStrategy {
    /** A thread that drives the workflow */
    private WorkflowThread thread;

    @Override
    public synchronized void init() {
        if (thread == null) {
            thread = new WorkflowThread();
            thread.setDaemon(true);
            thread.setName("Workflow Thread"); //$NON-NLS-1$
            thread.start();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }

    @Override
    public void shutdown() {
        if (thread != null && thread.running) {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    thread.running = false;
                    thread = null;
                }

            };
            thread.requests.add(runnable);
        }
    }

    @Override
    public void run(final Runnable runnable) {
        if (Thread.currentThread() == thread) {
            runnable.run();
        } else {
            final boolean[] done = new boolean[1];
            final Throwable[] exception = new Throwable[1];
            thread.requests.add(new Runnable() {

                @Override
                public void run() {
                    try {
                        runnable.run();
                    } catch (Throwable e) {
                        exception[0] = e;
                        CatalogUIPlugin.log(e.getMessage(), e); // $NON-NLS-1$
                    } finally {
                        done[0] = true;
                    }
                }

            });

            Display display = Display.getCurrent();
            while (!done[0]) {
                if (display == null || !display.readAndDispatch()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }

            if (exception[0] instanceof RuntimeException) {
                throw (RuntimeException) exception[0];
            } else if (exception[0] != null) {
                throw new RuntimeException(exception[0]);
            }
        }
    }

    private static final class WorkflowThread extends Thread {
        private final BlockingQueue<Runnable> requests = new LinkedBlockingQueue<>();

        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                Runnable runnable;
                try {
                    runnable = requests.take();
                } catch (InterruptedException e) {
                    continue;
                }
                runnable.run();
            }
        }
    }

}
