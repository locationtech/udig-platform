/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
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
package net.refractions.udig.catalog.ui.workflow;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.swt.widgets.Display;

/**
 * Threading strategy that uses eclipse Jobs to run through the workflow
 * 
 * @author jesse
 * @since 1.1.0
 */
public class DefaultThreading implements ThreadingStrategy {
    /** A thread that drives the workflow */
    private WorkflowThread thread;

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

    public void shutdown() {
        if (thread!=null && thread.running) {
            Runnable runnable = new Runnable(){

                public void run() {
                    thread.running = false;
                    thread = null;
                }

            };
            thread.requests.add(runnable);
        }
    }

    public void run( final Runnable runnable ) {
        if (Thread.currentThread() == thread) {
            runnable.run();
        } else {
            final boolean[] done = new boolean[1];
            final Throwable[] exception = new Throwable[1];
            thread.requests.add(new Runnable(){

                public void run() {
                    try {
                        runnable.run();
                    } catch (Throwable e) {
                        exception[0] = e;
                        CatalogUIPlugin.log( e.getMessage() , e); //$NON-NLS-1$
                    } finally {
                        done[0] = true;
                    }
                }

            });

            Display display = Display.getCurrent();
            while( !done[0] ) {
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


    private final static class WorkflowThread extends Thread {
        private final BlockingQueue<Runnable> requests = new LinkedBlockingQueue<Runnable>();
        private volatile boolean running = true;
        @Override
        public void run() {
            while( running ) {
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
