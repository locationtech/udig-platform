package net.refractions.udig.catalog.ui.workflow;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;

/**
 * Wizard page that delegates to an internal State.
 * <p>
 * We have the ability to just process the State objects during a DnD; allowing
 * us to work through the wizard pages without annoying users - unless some
 * input is needed - at that point we can pop open the correct wizard page.
 */
public abstract class WorkflowWizardPage extends WizardPage {
    /**
     * State used to perform the function of this wizard
     * page during DragNDrop; in the event of failure
     * this wizard page will be shown.
     */
	State state;
	
	protected WorkflowWizardPage(
		String pageName, String title, ImageDescriptor titleImage
	) {
		super(pageName, title, titleImage);
	}

    protected WorkflowWizardPage(String pageName) {
        super(pageName);
    }
    
    /**
     * State used to perform the function of this wizard page
     * during DragNDrop; in the event of failure
     * this wizard page will be shown to allow the user
     * to complete the tasks manually.
     *
     * @return State for this wizard page
     */
	public State getState() {
		return state;
	}
	
	/**
	 * Pass in the state for this wizard page.
	 *
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	/**
	 * Specifically access the WorkflowWizard containing this page.
	 */
    @Override
    public WorkflowWizard getWizard() {
        return (WorkflowWizard) super.getWizard();
    }
    
	/**
	 * Called immediately after a page has been shown in the wizard. At the 
	 * time this method is called, the page can be sure that its ui has been
	 * created (via a call to #createContents()) and that it's state has been
	 * set (via #setState()). Default implementation does nothing, subclass
	 * should override.
	 */
	public void shown() {
		//do nothing
	}
	

    /**
     * Called by framework as the page is about to be left.  Following Changes should not depend on this
     * method.  It is primarily here for the wizard page to save settings and provide better feedback to the
     * user than the associated state does.
     * 
     * <p>
     * There are two main use cases for this method. The first is to save settings for the next time
     * the wizard is visited. The other is to perform some checks or do some loading that is too
     * expensive to do every time isPageComplete() is called. For example a database wizard page
     * might try to connect to the database in this method rather than isPageComplete() because it
     * is such an expensive method to call.
     * </p>
     * <p>
     * Remember that this method is <em>only</em> called when moving forward.
     * <p>
     * If an expensive method is called make sure to run it in the container:
     * 
     * <pre>
     * getContainer().run(false, cancelable, runnable);
     * </pre>
     * Remember to pass in false as the fork parameter so that it blocks until the method has completed executing.
     * </p>
     * 
     * @return true if it is acceptable to leave the page false if the page must not be left
     */
    public boolean leavingPage() {
        // default does nothing
        return true;
    }

	/**
	 * This method returns true if there are more states in the workflow, and
	 * the current page is complete. Subclasses should extend this method in the 
	 * following way.
	 *
	 * <pre>
	 * <code>
	 * public boolean canFlipToNextPage() {
	 *   boolean flip = super.canFlipToNextPage();
	 *   if (flip) {
	 *   	//validate user input (usually checking state of ui)
	 *   	if (...) {
	 *   		return true;
	 *   	}
	 *   }
	 * 
	 *   return false;
	 * }
	 * </code>
	 * </pre>
	 */
	@Override
	public boolean canFlipToNextPage() {
		boolean complete = isPageComplete();
		boolean more = false;
		if (state != null)
			more = state.getWorkflow().hasMoreStates();
		
		return complete && more;
	}
    public String toString() {
    	StringBuffer text = new StringBuffer();
    	text.append( getName() );
    	text.append( "(" );
    	text.append( state != null ? state.getName() : "null" );
    	text.append( "_" );
        return text.toString();
    }
}