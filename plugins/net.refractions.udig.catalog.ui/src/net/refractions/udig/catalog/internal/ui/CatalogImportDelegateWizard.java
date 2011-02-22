package net.refractions.udig.catalog.internal.ui;

import java.lang.reflect.InvocationTargetException;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

public class CatalogImportDelegateWizard extends WorkflowWizard implements IImportWizard {


//	used to figure out when user presses back
	PageListener pageListener;

	public CatalogImportDelegateWizard() {
		super(new MyCatalogImport().createWorkflow(), new MyCatalogImport().createPageMapping());

		setWindowTitle(Messages.CatalogImportDelegateWizard_windowTitle);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getWorkflow().start(null);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		//create the page listener
		if (pageListener == null) {
			pageListener = new PageListener();
			WizardDialog dialog = (WizardDialog)getContainer();
			dialog.addPageChangedListener(pageListener);
		}

		//move the workflow to the next state
		try {
			getContainer().run(true,true,new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) throws
					InvocationTargetException, InterruptedException {

					getWorkflow().next(monitor);
				}

			});
		}
		catch (InvocationTargetException e) {
			CatalogUIPlugin.log(e.getLocalizedMessage(),e);
		}
		catch (InterruptedException e) {
			return null;
		}

		return super.getNextPage(page);
	}

	@Override
	protected  boolean performFinish(IProgressMonitor monitor) {

        if( !getWorkflow().isFinished() )
            getWorkflow().next(monitor);

		CatalogImport.CatalogImportWizard catalogImportWizard = new CatalogImport.CatalogImportWizard(getWorkflow(),getStateMap());
        catalogImportWizard.setContainer(getContainer());
        boolean performFinish = catalogImportWizard.performFinish();

//      TODO Something will need to be done here...
//      if( !performFinish )
//          getWorkflow().previous(monitor);
        return performFinish;
	}

	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	static class MyCatalogImport extends CatalogImport {
		@Override
		void init() {
			//do nothing
		}
	}

	class PageListener implements IPageChangedListener {
		public void pageChanged(PageChangedEvent event) {
			if (event.getSelectedPage() == null)
				return;

			//going backward?
			Workflow.State state = getWorkflow().getCurrentState();
			if (state!=null && state.getPreviousState() != null) {
				if (event.getSelectedPage().equals(getPage(state.getPreviousState()))) {
					//yes, move workflow backward
					try {
						getContainer().run(true,true,new IRunnableWithProgress() {

							public void run(IProgressMonitor monitor) throws
								InvocationTargetException, InterruptedException {

								getWorkflow().previous(monitor);
							}

						});
					}
					catch (InvocationTargetException e) {
						CatalogUIPlugin.log(e.getLocalizedMessage(),e);
					}
					catch (InterruptedException e) {}

					//update buttons
					getContainer().updateButtons();
				}
			}
		}
	}
}
