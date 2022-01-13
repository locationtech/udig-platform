/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.internal.Messages;

/**
 * This class allows a plugin to add an {@link IShutdownTask} object that will be run when uDig
 * shuts down. It allows a single place for shutdown tasks such as saving the catalog or projects or
 * anything else.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class ShutdownTaskList implements IWorkbenchListener {

    private static final ShutdownTaskList INSTANCE = new ShutdownTaskList();

    private Collection<PostTask> postShutdownTasks = new LinkedList<>();

    private Collection<PreTask> preShutdownTasks = new LinkedList<>();

    @Override
    public void postShutdown(final IWorkbench workbench) {

        try {
            final ProgressMonitorDialog dialog = getDialog(workbench);
            dialog.run(true, false, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor2)
                        throws InvocationTargetException, InterruptedException {
                    OffThreadProgressMonitor monitor = new OffThreadProgressMonitor(monitor2,
                            dialog.getShell().getDisplay());

                    int totalsteps = 0;
                    for (PostTask task : postShutdownTasks) {
                        try {
                            task.steps = task.task.getProgressMonitorSteps();
                            totalsteps += task.steps;
                        } catch (Throwable e) {
                            LoggingSupport.log(UiPlugin.getDefault(),
                                    "error calling getProgressMonitorSteps() on " + task.task, //$NON-NLS-1$
                                    e);
                        }
                    }

                    monitor.beginTask(Messages.ShutdownTaskList_shutDown, totalsteps);

                    for (PostTask task : postShutdownTasks) {
                        SubMonitor subMonitor = SubMonitor.convert(monitor, task.steps);
                        try {
                            task.task.postShutdown(subMonitor, workbench);
                        } catch (Throwable t) {
                            task.task.handlePostShutdownException(t);
                        } finally {
                            subMonitor.done();
                        }
                    }
                }

            });
        } catch (InvocationTargetException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        } catch (InterruptedException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        }

    }

    @Override
    public boolean preShutdown(final IWorkbench workbench, final boolean forced) {
        final ProgressMonitorDialog dialog = getDialog(workbench);

        final boolean[] allowShutdown = new boolean[1];
        allowShutdown[0] = true;

        workbench.getActiveWorkbenchWindow().getShell().setVisible(false);

        final Display display = Display.getCurrent();
        try {
            dialog.run(true, forced, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor2)
                        throws InvocationTargetException, InterruptedException {

                    IProgressMonitor monitor = new OffThreadProgressMonitor(monitor2, display);

                    int totalsteps = 0;
                    for (PreTask task : preShutdownTasks) {
                        try {
                            task.steps = task.task.getProgressMonitorSteps();
                            totalsteps += task.steps;
                        } catch (Throwable e) {
                            LoggingSupport.log(UiPlugin.getDefault(),
                                    "error calling getProgressMonitorSteps() on " + task.task, //$NON-NLS-1$
                                    e);
                        }
                    }
                    monitor.beginTask(Messages.ShutdownTaskList_shutDown, totalsteps);

                    for (PreTask task : preShutdownTasks) {
                        SubMonitor subMonitor = SubMonitor.convert(monitor, task.steps);
                        boolean result;
                        try {
                            result = task.task.preShutdown(subMonitor, workbench, forced);
                        } catch (Throwable t) {
                            result = task.task.handlePreShutdownException(t, forced);
                        } finally {
                            subMonitor.done();
                        }
                        if (!forced) {
                            if (monitor.isCanceled() || !result)
                                allowShutdown[0] = false;
                            if (monitor.isCanceled())
                                return;
                        }
                    }
                }

            });
        } catch (InvocationTargetException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        } catch (InterruptedException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        }

        if (!allowShutdown[0])
            workbench.getActiveWorkbenchWindow().getShell().setVisible(true);

        return allowShutdown[0];
    }

    private ProgressMonitorDialog getDialog(IWorkbench workbench) {
        Shell shell = new Shell(Display.getCurrent());

        ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
        dialog.open();
        return dialog;
    }

    /**
     * Returns the shared instance for the application. In most cases this method should be used to
     * get an instance of ShutdownTaskList instead of creating a new instance.
     *
     * @return The shared instance of ShutdownTaskList
     */
    public static ShutdownTaskList instance() {
        return INSTANCE;
    }

    /**
     * Adds a task to the list of tasks to be run after shutdown.
     *
     * @see #postShutdown(IWorkbench)
     * @param task the task to be ran. The ordering or the tasks ran is random so make sure there
     *        are no order dependencies between tasks
     */
    public synchronized void addPostShutdownTask(PostShutdownTask task) {
        postShutdownTasks.add(new PostTask(task));
    }

    /**
     * Adds a task to the list of tasks to be run before shutdown.
     *
     * @see #postShutdown(IWorkbench)
     * @param task the task to be ran. The ordering or the tasks ran is random so make sure there
     *        are no order dependencies between tasks
     */
    public synchronized void addPreShutdownTask(PreShutdownTask task) {
        preShutdownTasks.add(new PreTask(task));
    }

    public synchronized void removePreShutdownTask(PreShutdownTask shutdownTask) {
        preShutdownTasks.remove(new PreTask(shutdownTask));
    }

    public synchronized void removePostShutdownTask(PostShutdownTask shutdownTask) {
        postShutdownTasks.remove(new PostTask(shutdownTask));
    }

    public static class PostTask {

        int steps;

        final PostShutdownTask task;

        public PostTask(PostShutdownTask task) {
            this.task = task;
        }

        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((task == null) ? 0 : task.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final PostTask other = (PostTask) obj;
            if (task == null) {
                if (other.task != null)
                    return false;
            } else if (!task.equals(other.task))
                return false;
            return true;
        }

    }

    public static class PreTask {

        int steps;

        final PreShutdownTask task;

        public PreTask(PreShutdownTask task) {
            this.task = task;
        }

        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((task == null) ? 0 : task.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final PreTask other = (PreTask) obj;
            if (task == null) {
                if (other.task != null)
                    return false;
            } else if (!task.equals(other.task))
                return false;
            return true;
        }

    }
}
