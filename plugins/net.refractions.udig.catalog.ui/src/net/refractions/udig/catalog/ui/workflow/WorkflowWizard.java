package net.refractions.udig.catalog.ui.workflow;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.Workflow.State;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;

public class WorkflowWizard extends Wizard {

    /** the workflow * */
    private Workflow workflow;

    /** the state to page map * */
    private Map<Class< ? extends Workflow.State>, WorkflowWizardPageProvider> map;

    /**
     * Creates a new workflow wizard. The workflow passed in must have a valid set of states.
     *
     * @param workflow The workflow to be run.
     * @param map A map of workflow states to wizard pages.
     */
    public WorkflowWizard( Workflow workflow,
            Map<Class< ? extends Workflow.State>, WorkflowWizardPageProvider> map ) {
        this.workflow = workflow;
        this.map = map;

        // do the wizard initialization stuff
        setNeedsProgressMonitor(true);
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    protected Map<Class< ? extends Workflow.State>, WorkflowWizardPageProvider> getStateMap() {
        return map;
    }

    @Override
    public void addPages() {


        // add the primary pages, make sure to set the state before they
        // are actually created
        Workflow.State[] states = workflow.getStates();
        if (states == null || states.length == 0)
            throw new IllegalStateException(Messages.WorkflowWizard_noStates);

        for( int i = 0; i < states.length; i++ ) {
            Workflow.State state = states[i];
            WorkflowWizardPage page = map.get(state.getClass()).getWorkflowWizardPage(state);

            init(page);

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
        Workflow.State state = workflow.getCurrentState();
        if (state == null)
        	return null;

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
    @SuppressWarnings("unchecked")
    public WorkflowWizardPage getPage( State state ) {
        if (state == null)
            return null;

        WorkflowWizardPage page = map.get(state.getClass()).getWorkflowWizardPage(state);
        init(page);
        return page;
    }


    void init( IWizardPage page ) {
        if( page.getWizard()!=this ){
            page.setWizard(this);
        }
    }

    private boolean isPageComplete() {
        Workflow.State state = workflow.getCurrentState();
        if (state == null)
            return false;

        WorkflowWizardPage current = getPage(state);
        return current.isPageComplete();
    }

    @Override
    public boolean canFinish() {
        return !workflow.hasMoreStates() && isPageComplete();
    }

    @Override
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
    	}catch (NullPointerException e) {
            CatalogUIPlugin.log("", e); //$NON-NLS-1$
    		// HACK A really bad hack because this sometimes causes a null pointer when
    		// wizard dialog calls stopped.  it appears that sometimes the wait
    		// cursor is null odd.  but I dn't have time to track it down.
        } catch (Exception e) {
            CatalogUIPlugin.log("", e); //$NON-NLS-1$
            return false;
        }
        return finished[0];
    }

    private void run( final IRunnableWithProgress runnable ) throws InvocationTargetException, InterruptedException {
        IWizardContainer container2 = getContainer();
            if (container2!=null && Display.getCurrent() != null ) {
                container2.run(true, true, new IRunnableWithProgress(){

                    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                            InterruptedException {
                        PlatformGIS.runBlockingOperation(runnable, monitor);
                    }

                });
            }else{
                runnable.run(new NullProgressMonitor());
            }
    }


    /**
     * This method is ran in a <b>non-UI</b> thread.  It uses {@link PlatformGIS#runBlockingOperation(IRunnableWithProgress, IProgressMonitor)} to ensure
     * that blocking operations in this method will not block the UI.  It is recommended that all long operation are done in this thread and only
     * quick UI updates should be done in the UI thread.
     *
     * @param monitor the dialog progress monitor.
     * @return if the wizard finished correctly.
     *
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
