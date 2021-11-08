/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.workflow;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.ProgressManager;

public class WorkflowWizardDialog extends WizardDialog implements Listener {

    /** flag to indicate whether headless execution is occurring * */
    boolean headless = false;

    private WorkflowWizardAdapter workflowWizardAdapter;

    public WorkflowWizardDialog(Shell parentShell, WorkflowWizard wizard) {
        super(parentShell, wizard);

        wizard.getWorkflow().addListener(this);
    }

    @Override
    protected WorkflowWizard getWizard() {
        return (WorkflowWizard) super.getWizard();
    }

    public WorkflowWizard getWorkflowWizard() {
        return getWizard();
    }

    @Override
    public WorkflowWizardPage getCurrentPage() {
        return (WorkflowWizardPage) super.getCurrentPage();
    }

    @Override
    protected void nextPressed() {
        // if there are no more states, do nothing
        if (getWorkflowWizard().getWorkflow().isFinished()) {
            return;
        }
        if (!getCurrentPage().leavingPage()) {
            return;
        }

        // move workflow to next state, start up a new thread to do it.
        final Workflow pipe = getWizard().getWorkflow();

        try {
            // don't fork, can cancel during transition
            run(false, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    pipe.next(monitor); // progress through the workflow
                }

            });
        } catch (Exception e) {
            CatalogUIPlugin.log("Exception while moving workflow forward", e); //$NON-NLS-1$
        }

    }

    @Override
    public void run(boolean fork, boolean cancelable, final IRunnableWithProgress request)
            throws InvocationTargetException, InterruptedException {
        if (headless && Display.getCurrent() == null) {
            PlatformGIS.run(request);
            return;
        }

        if (getProgressMonitor() instanceof ProgressMonitorPart) {
            ProgressMonitorPart part = (ProgressMonitorPart) getProgressMonitor();
            if (Display.getCurrent() != null)
                part.setVisible(true);

            try {
                setEnablement(buttonBar, false);
                if (fork) {
                    PlatformGIS.run(request, part);
                } else {
                    PlatformGIS.runBlockingOperation(request, part);
                }
            } finally {
                setEnablement(buttonBar, true);
                if (Display.getCurrent() != null && !part.isDisposed())
                    part.setVisible(false);
            }
        } else {
            if (fork) {
                PlatformGIS.run(request);
            } else {
                PlatformGIS.runBlockingOperation(request, ProgressManager.instance().get());
            }
        }
    }

    private void setEnablement(Control controlA, boolean enabled) {
        if (controlA == null)
            return;
        if (controlA instanceof Composite) {
            Composite composite = (Composite) controlA;
            Control[] children = composite.getChildren();
            for (Control control : children) {
                setEnablement(control, enabled);
            }
        } else {
            if (controlA != getButton(IDialogConstants.CANCEL_ID))
                controlA.setEnabled(enabled);
        }

    }

    protected void nextPressedSuper() {
        try {
            super.nextPressed();
        } catch (Exception e) {
            setErrorMessage(Messages.WorkflowWizard_PageTransitionError);
            CatalogUIPlugin.log("WorkflowWizardDialog#nextPressedSuper(): Error moving to " //$NON-NLS-1$
                    + getWorkflowWizard().getWorkflow().getCurrentState().getName(), e);
        }
    }

    @Override
    protected void backPressed() {
        // move workflow to previous state, start up a new thread to do it.
        final Workflow pipe = getWizard().getWorkflow();

        if (pipe.getCurrentState().getPreviousState() == null) {
            // this means that this wizard is part of a larger wizard and we need to return control
            // to it. See setAdapter for details
            workflowWizardAdapter.backPressed();
        }

        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {

                pipe.previous(monitor);
            }
        };
        try {
            run(false, true, runnable);
        } catch (InvocationTargetException e) {
            CatalogUIPlugin.log(e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            CatalogUIPlugin.log(e.getLocalizedMessage(), e);
        }
    }

    protected void backPressedSuper() {
        super.backPressed();
    }

    @Override
    protected void finishPressed() {
        // if there are no more states, do nothing
        if (getWorkflowWizard().getWorkflow().isFinished())
            return;

        /**
         * what happens if the page is driven by the state? I guess it means the state knows that it
         * can pass so go for it.
         */
        if (!getCurrentPage().leavingPage()) {
            return;
        }

        // move workflow to next state, start up a new thread to do it.
        final Workflow pipe = getWizard().getWorkflow();

        try {
            run(false, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    pipe.run(monitor);
                }

            });
        } catch (Exception e) {
            CatalogUIPlugin.log("Exception while moving workflow forward", e); //$NON-NLS-1$
        }

    }

    protected void finishPressedSuper() {
        super.finishPressed();
    }

    @Override
    public int open() {
        initWorkflow();
        return super.open();
    }

    @Override
    public boolean close() {
        getWorkflowWizard().getWorkflow().shutdown();
        return super.close();
    }

    @Override
    public void showPage(IWizardPage page) {
        super.showPage(page);

        // let the page know that is is being shown in the wizard, lets page
        // do some lazy initialization of UI widgets, etc.
        ((WorkflowWizardPage) page).shown();
    }

    /**
     * Runs the dialog in headless mode. The dialog will run headless while the workflow can run.
     *
     * @param monitor
     * @param true if the workflow ran and completed correctly. False if it failed or the user
     *        cancelled (because user interaction was required)
     */
    public boolean runHeadless(IProgressMonitor monitor) {
        try {
            this.headless = true;
            int ticks = getWorkflowWizard().getWorkflow().getStates().length * 10;
            monitor.beginTask(Messages.WorkflowWizardDialog_importTask, ticks);
            // we must ensure that the contents of the dialog (shell) have been
            // creates, needed for wizard pages
            if (getShell() == null) {
                // do in UI thread
                PlatformGIS.syncInDisplayThread(new Runnable() {
                    @Override
                    public void run() {
                        create();
                    }
                });
            }

            Workflow pipe = getWizard().getWorkflow();
            pipe.run(SubMonitor.convert(monitor, ticks));
            final boolean[] result = new boolean[] { true };
            if (!pipe.isFinished()) {
                // show the page corresponding to the current state
                final IWizardPage page = getWizard().getPage(pipe.getCurrentState());
                if (page != null) {
                    // ensure the page has a state if it is a DataPipelinePage
                    if (page instanceof WorkflowWizardPage) {
                        WorkflowWizardPage dpPage = (WorkflowWizardPage) page;
                        if (dpPage.getState() == null)
                            dpPage.setState(pipe.getCurrentState());
                    }

                    PlatformGIS.syncInDisplayThread(new Runnable() {
                        @Override
                        public void run() {
                            headless = false;
                            showPage(page);
                            if (open() == Window.CANCEL) {
                                result[0] = false;
                            }
                        };
                    });
                }

            }

            this.headless = false;
            return result[0];
        } finally {
            monitor.done();
        }
    }

    /**
     * Performs the initialization of the workflow.
     */
    protected void initWorkflow() {
        // start the workflow
        // TODO: This can potentially freeze up the UI if the fist state
        // does a lot of work in the #init(IProgressMonitor) method. Perhaps
        // it should be made part of the contract of the dialog that the pipe
        // already be started before open is called.
        final Workflow pipe = getWizard().getWorkflow();
        final IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {
                if (!pipe.started)
                    pipe.start(monitor);
            }
        };

        if (Display.getCurrent() != null) {
            try {
                runnable.run(ProgressManager.instance().get());
            } catch (Exception e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        } else {
            PlatformGIS.syncInDisplayThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(false, false,
                                runnable);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    @Override
    public void started(State first) {
        // do nothing
    }

    @Override
    public void forward(State current, State prev) {
        if (headless)
            return;

        // move the wizard to the next page
        PlatformGIS.syncInDisplayThread(new Runnable() {

            @Override
            public void run() {
                nextPressedSuper();
            }
        });
    }

    @Override
    public void backward(State current, State next) {
        if (headless)
            return;

        // move the wizard to the previous page
        PlatformGIS.syncInDisplayThread(new Runnable() {

            @Override
            public void run() {
                backPressedSuper();
            }
        });
    }

    @Override
    public void statePassed(State state) {
        // do nothing

    }

    @Override
    public void stateFailed(State state) {
        // do nothing

    }

    @Override
    public void finished(State last) {
        if (headless) {
            // signal to the wizard that we are finished
            getWizard().performFinish();
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    finishPressedSuper();
                }
            };
            PlatformGIS.asyncInDisplayThread(runnable, true);
        }
    }

    /**
     * This method is a hook for the {@link WorkflowWizardAdapter} so that it can get notification
     * when the previous button is pressed on the first page. If there is a
     * {@link WorkflowWizardAdapter} it means that this wizard is really part of a large wizard that
     * is created by the eclipse framework. This dialog is opened to handle the workflow wizard work
     * but it needs the previous button on always and needs to notify the adapter when that button
     * is pressed so that this dialog can be hidden until needed again.
     *
     * @param workflowWizardAdapter the adapter that interfaces with the other wizard.
     * @param previousPage the page that is previous to this page
     */
    void setAdapter(final WorkflowWizardAdapter workflowWizardAdapter, IWizardPage previousPage) {
        this.workflowWizardAdapter = workflowWizardAdapter;
        IWizardPage startingPage = getWizard().getStartingPage();
        startingPage.setPreviousPage(previousPage);
        updateButtons();
    }
}
