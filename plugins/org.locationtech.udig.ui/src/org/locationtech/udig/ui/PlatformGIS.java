/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.geotools.brewer.color.ColorBrewer;
import org.locationtech.udig.aoi.IAOIService;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.internal.Messages;

/**
 * A facade into udig to simplify operations relating to performing platform operations.
 *
 * @author jeichar
 * @since 1.1
 * @version 1.2.3
 */
public class PlatformGIS {

    private static ColorBrewer colorBrewer;
    private static ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Runs the given runnable in a separate thread, providing it a progress monitor. Exceptions
     * thrown by the runnable are logged, and not rethrown.
     */
    public static void run( IRunnableWithProgress request ) {
        Runner runner = new Runner();
        runner.setRequest(request);
        runner.schedule();
    }
    /**
     * Runs the given runnable in a separate thread, providing it a progress monitor. Exceptions
     * thrown by the runnable are logged, and not rethrown.
     */
    public static void run( IRunnableWithProgress request, IProgressMonitor monitorToUse) {
        Runner runner = new Runner();
        RunnableAndProgress runnable = new RunnableAndProgress(request, monitorToUse);
        runner.setRequest(runnable);
        runner.schedule();

    }

    /**
     * This method runs the runnable in a separate thread. It is useful in cases where a thread must
     * wait for a long running and potentially blocking operation (for example an IO operation). If
     * the IO is done in the UI thread then the user interface will lock up. This allows synchronous
     * execution of a long running thread in the UI thread without locking the UI.
     * <p>
     * When called from the display thread, a {@link #executor} is used to schedule the background
     * runnable, and we will take over the {@link Display#readAndDispatch()} cycle while waiting
     * for the background runable to complete. When completed normal display thread execution will
     * resume.
     *
     * @param runnable The runnable(operation) to run
     * @param monitor the progress monitor to update.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public static void runBlockingOperation( final IRunnableWithProgress runnable,
            final IProgressMonitor monitor2 ) throws InvocationTargetException, InterruptedException {

        final IProgressMonitor monitor=monitor2==null?new NullProgressMonitor():monitor2;
        final InterruptedException[] interruptedException = new InterruptedException[1];
        final InvocationTargetException[] invocationTargetException = new InvocationTargetException[1];
        Display d = Display.getCurrent();
        if (d == null)
            d = Display.getDefault();
        final Display display = d;
        final AtomicBoolean done = new AtomicBoolean();
        final Object mutex=new Object();
        done.set(false);

        Future<Object> future = executor.submit(new Callable<Object>(){
            @SuppressWarnings("unused")
            Exception e = new Exception("For debugging"); //$NON-NLS-1$
            @Override
            public Object call() throws Exception {
                try {
                    runnable.run(new OffThreadProgressMonitor(monitor != null
                            ? monitor
                            : ProgressManager.instance().get(), display));
                } catch (InvocationTargetException ite) {
                    invocationTargetException[0] = ite;
                } catch (InterruptedException ie) {
                    interruptedException[0] = ie;
                } finally {
                    done.set(true);
                    synchronized (mutex) {
                        mutex.notify();
                    }
                }
                return null;
            }

        });
        while( !monitor.isCanceled() && !done.get() && !Thread.interrupted() ) {
            Thread.yield();
            if (Display.getCurrent() == null) {
                wait(mutex, 200);
            } else {
                try {
                    if (!d.readAndDispatch()) {
                        wait(mutex, 200);
                    }
                } catch (Exception e) {
                    LoggingSupport.log(UiPlugin.getDefault(),
                            "Error occurred while waiting for an operation to complete", e); //$NON-NLS-1$
                }
            }
        }
        if (monitor.isCanceled()) {
            future.cancel(true);
        }

        if (interruptedException[0] != null)
            throw interruptedException[0];
        else if (invocationTargetException[0] != null)
            throw invocationTargetException[0];
    }

    private static void wait( Object mutex, long waitTime ) {
        synchronized (mutex) {
            try {
                mutex.wait(waitTime);
            } catch (InterruptedException e) {
                return ;
            }
        }
    }

    /**
     * Runs the given runnable in a protected mode. Exceptions thrown in the runnable are logged and
     * passed to the runnable's exception handler. Such exceptions are not rethrown by this method.
     */
    public static void run( ISafeRunnable request ) {
        Runner runner = new Runner();
        runner.setRequest(request);
        runner.schedule();
    }

    private static class Runner extends Job {

        public Runner( ) {
            super("Platform GIS runner"); //$NON-NLS-1$
        }

        Object runnable;

        /**
         * Add a runnable object to be run.
         *
         * @param runnable
         */
        public void setRequest( Object runnable ) {
            this.runnable = runnable;
        }

        private void run( ISafeRunnable runnable ) {
            try {
                runnable.run();
            } catch (Throwable e) {
                if (e.getMessage() != null) {
                    LoggingSupport.log(UiPlugin.getDefault(), e);
                } else {
                    // TODO REVIEW : What would happen in Error-View,  if message is empty?
                    LoggingSupport.log(UiPlugin.getDefault(), "", e); //$NON-NLS-1$
                }
                runnable.handleException(e);
            }
        }

        private void run( IRunnableWithProgress runnable, IProgressMonitor monitor ) {
            try {
                runnable.run(monitor);
            } catch (Throwable t) {
                LoggingSupport.log(UiPlugin.getDefault(), t);
            }
        }

        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            if (!PlatformUI.getWorkbench().isClosing()) {
                if (runnable != null) {

                    if (runnable instanceof ISafeRunnable) {
                        run((ISafeRunnable) runnable);
                    } else if (runnable instanceof IRunnableWithProgress) {
                        run((IRunnableWithProgress) runnable, monitor);
                    }else if (runnable instanceof RunnableAndProgress) {
                        RunnableAndProgress request = (RunnableAndProgress)runnable;
                        run(request.runnable, request.monitor);
                    }
                }
            }
            return Status.OK_STATUS;
        }
    }

    private static class RunnableAndProgress{
        public RunnableAndProgress( IRunnableWithProgress request, IProgressMonitor monitorToUse ) {
            this.runnable=request;
            Display display = Display.getCurrent();
            if( display == null ){
                Display.getDefault();
            }
            this.monitor = new OffThreadProgressMonitor(monitorToUse, display);
        }
        IRunnableWithProgress runnable;
        IProgressMonitor monitor;
    }

    public static ColorBrewer getColorBrewer() {
        synchronized (ColorBrewer.class) {
            if (colorBrewer == null) {
                colorBrewer = ColorBrewer.instance();
            }
        }
        return colorBrewer;
    }

    /**
     * Acts as a safer alternative to Display.syncExec(). If readAndDispatch is being called from
     * the display thread syncExec calls will not be executed only Display.asyncExec calls are
     * executed. So this method uses Display.asyncExec and patiently waits for the result to be
     * returned. Can be called from display thread or non-display thread. Runnable should not be
     * blocking or it will block the display thread.
     *
     * @param runnable runnable to execute
     */
    public static void syncInDisplayThread( final Runnable runnable ) {
        Display display = Display.getCurrent();
        if( display==null ){
            display = Display.getDefault();
        }
        syncInDisplayThread(display, runnable);
    }
    /**
     * Run in the display thread at the next reasonable opportunity, current thread will
     * wait for the runnable to complete.
     *
     * @param display
     * @param runnable
     */
    public static void syncInDisplayThread( Display display, final Runnable runnable ) {
        if (Display.getCurrent() != display) {
            final AtomicBoolean done = new AtomicBoolean(false);
            final Object mutex=new Object();
            display.asyncExec(new Runnable(){
                @Override
                public void run() {
                    try {
                        runnable.run();
                    } finally {
                        done.set(true);
                        synchronized (mutex) {
                            mutex.notify();
                        }
                    }
                }
            });
            while( !done.get() && !Thread.interrupted() ) {
                synchronized (mutex) {
                    wait(mutex, 200);
                }
            }
        } else {
            runnable.run();
        }
    }

    /**
     * Waits for the condition to become true. Will call Display#readAndDispatch() if currently in
     * the display thread.
     *
     * @param interval the time to wait between testing of condition, in milliseconds. Must be a
     *        positive number and is recommended to be larger than 50
     * @param timeout maximum time to wait. Will throw an {@link InterruptedException} if reached.
     *        If -1 then it will not timeout.
     * @param condition condition to wait on.
     * @param mutex if not null mutex will be waited on so that a notify will interrupt the wait.
     * @throws InterruptedException
     */
    public static void wait( long interval, long timeout, WaitCondition condition, Object mutex )
            throws InterruptedException {
        long start = System.currentTimeMillis();
        Object mutex2 = mutex == null ? new Object() : mutex;

        Display current = Display.getCurrent();
        if (current == null) {
            while( !condition.isTrue() ) {
                if (timeout > 0 && System.currentTimeMillis() - start > timeout)
                    throw new InterruptedException("Timed out waiting for condition " + condition); //$NON-NLS-1$
                synchronized (mutex2) {
                    mutex2.wait(interval);
                }
            }
        } else {
            while( !condition.isTrue() ) {
                Thread.yield();
                if (timeout > 0 && System.currentTimeMillis() - start > timeout)
                    throw new InterruptedException("Timed out waiting for condition " + condition); //$NON-NLS-1$
                if (!current.readAndDispatch())
                    synchronized (mutex2) {
                        mutex2.wait(interval);
                    }
            }

        }
    }

    /**
     * Runs a blocking task in a ProgressDialog. It is ran in such a way that even if the task
     * blocks it can be cancelled. This is unlike the normal ProgressDialog.run(...) method which
     * requires that the {@link IProgressMonitor} be checked and the task to "nicely" cancel.
     *
     * @param dialogTitle The title of the Progress dialog
     * @param showRunInBackground if true a button added to the dialog that will make the job be ran
     *        in the background.
     * @param runnable the task to execute.
     * @param runASync if true the runnable will be ran asynchronously
     */
    public static void runInProgressDialog( final String dialogTitle,
            final boolean showRunInBackground, final IRunnableWithProgress runnable,
            boolean runASync ) {

        Runnable object = new Runnable(){
            @Override
            public void run() {
                Shell shell = Display.getDefault().getActiveShell();
                ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell){
                    @Override
                    protected void configureShell( Shell shell ) {
                        super.configureShell(shell);
                        shell.setText(dialogTitle);
                    }

                    @Override
                    protected void createButtonsForButtonBar( Composite parent ) {
                        if (showRunInBackground)
                            createBackgroundButton(parent);
                        super.createButtonsForButtonBar(parent);
                    }

                    private void createBackgroundButton( Composite parent ) {
                        createButton(parent, IDialogConstants.BACK_ID,
                                Messages.PlatformGIS_background, true);
                    }

                    @Override
                    protected void buttonPressed( int buttonId ) {
                        if (buttonId == IDialogConstants.BACK_ID) {
                            getShell().setVisible(false);
                        } else
                            super.buttonPressed(buttonId);
                    }
                };
                try {

                    dialog.run(true, true, new IRunnableWithProgress(){
                        @Override
                        public void run( IProgressMonitor monitor ) {
                            try {
                                runBlockingOperation(new IRunnableWithProgress(){

                                    @Override
                                    public void run( IProgressMonitor monitor )
                                            throws InvocationTargetException, InterruptedException {
                                        runnable.run(monitor);
                                    }
                                }, monitor);
                            } catch (Exception e) {
                                LoggingSupport.log(UiPlugin.getDefault(), e);
                            }

                        }
                    });
                } catch (Exception e) {
                    LoggingSupport.log(UiPlugin.getDefault(), e);
                }
            }
        };

        if (runASync)
            Display.getDefault().asyncExec(object);
        else
            syncInDisplayThread(object);
    }

    /**
     * Runs the runnable in the display thread but asynchronously.
     *
     * @param runnable the runnable to execute
     * @param executeIfInDisplay if true and the current thread is the display thread then the
     *        runnable will just be executed.
     */
    public static void asyncInDisplayThread( Runnable runnable, boolean executeIfInDisplay ) {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        asyncInDisplayThread(display, runnable, executeIfInDisplay);
    }

    /**
     * Runs the runnable in the display thread but asynchronously.
     *
     * @param display the display in which to run the runnable
     * @param runnable the runnable to execute
     * @param executeIfInDisplay if true and the current thread is the display thread then the
     *        runnable will just be executed.
     */
    public static void asyncInDisplayThread( Display display, Runnable runnable,
            boolean executeIfInDisplay ) {
        if (executeIfInDisplay && display == Display.getCurrent()) {
            runnable.run();
        } else {
            display.asyncExec(runnable);
        }
    }

    /**
     * Gets the Area of Interest workbench service
     * @return
     */
    public static IAOIService getAOIService() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        return workbench.getService(IAOIService.class);
    }

}
