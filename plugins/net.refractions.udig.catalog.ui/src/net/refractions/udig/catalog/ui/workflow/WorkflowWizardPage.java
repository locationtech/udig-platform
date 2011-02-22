package net.refractions.udig.catalog.ui.workflow;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;

public abstract class WorkflowWizardPage extends WizardPage {

	Workflow.State state;

	protected WorkflowWizardPage(
		String pageName, String title, ImageDescriptor titleImage
	) {
		super(pageName, title, titleImage);
	}

    protected WorkflowWizardPage(String pageName) {
        super(pageName);
    }

	public Workflow.State getState() {
		return state;
	}

	public void setState(Workflow.State state) {
		this.state = state;
	}

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

}
