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
package net.refractions.udig.catalog.ui.workflow;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;

/**
 * Wizard capable of tracking states in a workflow; and passing control to the user in the event the
 * workflow cannot be automatically followed to completion.
 * <p>
 * A workflow is set up as a series of States; each one of which is "run" (similar to what happens
 * when you hit "Finish" in a wizard. In the event one of the states cannot complete (because
 * information from the user is required) the workflow wizard can display the correct page; allowing
 * the user to mannually complete the rest of the workflow.
 * <p>
 * To facilitate this the contents of a WorkflowWizard are WorkflowWizardPages (which have an
 * additional method to pass in their State). Such pages are expected to completly delegate to their
 * State in order to prevent duplication of logic.
 * 
 * @since 1.2.0
 */
public class WorkflowWizard extends Wizard {

    /** the workflow * */
    private Workflow workflow;

    /** the state to page map * */
    private Map<Class< ? extends State>, WorkflowWizardPageProvider> map;

    /**
     * Creates a new workflow wizard. The workflow passed in must have a valid set of states.
     * 
     * @param workflow The workflow to be run.
     * @param map A map of workflow states to wizard pages.
     */
    public WorkflowWizard( Workflow workflow,
            Map<Class< ? extends State>, WorkflowWizardPageProvider> map ) {
        this.workflow = workflow;
        this.map = map;

        // do the wizard initialization stuff
        setNeedsProgressMonitor(true);
    }
    /**
     * Access to the current workflow.
     * 
     * @return access to the workflow
     */
    public Workflow getWorkflow() {
        return workflow;
    }

    /**
     * Maps state to corresponding wizard page.
     * <p>
     * Note a wizard page provider is supplied to facilitate lazy evaulation.
     * 
     * @return Look up wizard page by state.
     */
    protected Map<Class< ? extends State>, WorkflowWizardPageProvider> getStateMap() {
        return map;
    }

    /**
     * Grab the initial page from the workflow; and set it up for use.
     */
    @Override
    public void addPages() {

        // add the primary pages, make sure to set the state before they
        // are actually created
        State[] states = workflow.getStates();
        if (states == null || states.length == 0) {
            throw new IllegalStateException(Messages.WorkflowWizard_noStates);
        }
        for( int i = 0; i < states.length; i++ ) {
            State state = states[i];

            // look up page for this state
            WorkflowWizardPage page = getPage(state);

            if (page == null) {
                String msg = Messages.WorkflowWizard_noPage;
                throw new IllegalStateException(msg);
            }
            page.setState(state);

            addPage(page);
        }
    }

    /**
     * Returns the next primary page in the page sequence. This method is called by the
     * 
     * @see org.eclipse.jface.wizard.IWizardContainer when a page does not contribute a secondary
     *      page.
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage( IWizardPage page ) {
        // get the current state from the workflow
        State state = workflow.getCurrentState();
        if (state == null) {
            return null;
        }

        WorkflowWizardPage next = getPage(state);

        if (next == null) {
            String msg = Messages.WorkflowWizard_noPage;
            throw new IllegalStateException(msg);
        }

        next.setState(workflow.getCurrentState());
        return next;
    }

    /**
     * Returns the wizard page which corresponds to a particular workflow state.
     * 
     * @param state The state in question.
     * @return The wizard page registered for the state or null if no such page exists.
     */
    public WorkflowWizardPage getPage( State state ) {

        if (state == null) {
            return null;
        }
        Class< ? extends State> stateClass = state.getClass();
        WorkflowWizardPageProvider workflowWizardPageProvider = map.get(stateClass);
        WorkflowWizardPage page = workflowWizardPageProvider.getWorkflowWizardPage(state);

        init(page);

        return page;
    }

    void init( IWizardPage page ) {
        if (page.getWizard() != this) {
            page.setWizard(this);
        }
    }

    /**
     * Check the workflow to see if the current state/page is both non null and complete.
     * 
     * @return true if the page for the current state exits and is complete
     */
    private boolean isPageComplete() {
        State state = workflow.getCurrentState();
        if (state == null)
            return false;

        WorkflowWizardPage current = getPage(state);
        return current.isPageComplete();
    }

    /**
     * Checks that the current page is complete, and with this information a workflow.dryRun can be
     * completed (without further information from the user).
     */
    public boolean canFinish() {
        return isPageComplete() && (workflow.dryRun() || !workflow.hasMoreStates());
    }

    /**
     * Run the performFinish( monitor ) returning true if it was in fact able to complete.
     */
    public final boolean performFinish() {
        final boolean[] finished = new boolean[1];

        IRunnableWithProgress runnable = new IRunnableWithProgress(){

            public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                    InterruptedException {
                finished[0] = performFinish(monitor);
            }

        };
        try {
            run(runnable);
        } catch (NullPointerException e) {
            if (CatalogUIPlugin.getDefault().isDebugging()) {
                String name = getWindowTitle();
                State state = workflow.getCurrentState();
                if (state != null && state.getName() != null) {
                    name = state.getName();
                }
                CatalogUIPlugin.log(name + " could not finish.", e); //$NON-NLS-1$
                // FIXME this sometimes causes a null pointer when
                // wizard dialog calls stopped. it appears that sometimes the wait
                // cursor is null odd. but I dn't have time to track it down.
            }
        } catch (Exception e) {
            if (CatalogUIPlugin.getDefault().isDebugging()) {
                String name = getWindowTitle();
                State state = workflow.getCurrentState();
                if (state != null && state.getName() != null) {
                    name = state.getName();
                }
                CatalogUIPlugin.log(name + " could not finish:" + e, e); //$NON-NLS-1$
            }
            return false;
        }
        return finished[0];
    }

    /**
     * Run using the wizard progress bar.
     * 
     * @param runnable
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    private void run( final IRunnableWithProgress runnable ) throws InvocationTargetException,
            InterruptedException {
        IWizardContainer wizardContainer = getContainer();
        if (wizardContainer != null && Display.getCurrent() != null) {
            wizardContainer.run(false, true, new IRunnableWithProgress(){

                public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                        InterruptedException {
                    runnable.run(monitor);
                }

            });
        } else {
            runnable.run(new NullProgressMonitor());
        }
    }

    /**
     * This method is ran in a <b>non-UI</b> thread. It uses
     * {@link PlatformGIS#runBlockingOperation(IRunnableWithProgress, IProgressMonitor)} to ensure
     * that blocking operations in this method will not block the UI. It is recommended that all
     * long operation are done in this thread and only quick UI updates should be done in the UI
     * thread.
     * 
     * @param monitor the dialog progress monitor.
     * @return if the wizard finished correctly.
     * @see Display#syncExec(Runnable)
     * @see Display#asyncExec(Runnable)
     */
    protected boolean performFinish( IProgressMonitor monitor ) {
        return true;
    }

    @Override
    public boolean needsPreviousAndNextButtons() {
        return true;
    }

}
