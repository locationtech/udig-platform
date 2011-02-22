package net.refractions.udig.catalog.ui.export;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.catalog.ui.workflow.Workflow.State;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class CatalogExportDelegateWizard extends WorkflowWizard implements IExportWizard {

    private static final MyCatalogExport CONFIGURATION = new MyCatalogExport();

//	used to figure out when user presses back
	PageListener pageListener;

	public CatalogExportDelegateWizard() {
		super(CONFIGURATION.createWorkflow(), CONFIGURATION.createPageMapping());
		setWindowTitle(Messages.CatalogExportWizard_WindowTitle);
	}

    public CatalogExportDelegateWizard(Workflow workflow) {
        super(workflow, new MyCatalogExport().createPageMapping());
        setWindowTitle(Messages.CatalogExportWizard_WindowTitle);
    }

    public CatalogExportDelegateWizard(Workflow workflow, Map<Class<? extends State>, WorkflowWizardPageProvider> map) {
        super(workflow, map);
        setWindowTitle(Messages.CatalogExportWizard_WindowTitle);
    }

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getWorkflow().start(null);
	}

	@Override
    public boolean canFinish() {
        if (super.canFinish()) {
            //if the current page isn't complete, we can't finish yet!
            return getContainer().getCurrentPage().isPageComplete();
        }
        return false;
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
    public IDialogSettings getDialogSettings() {
    	return CatalogUIPlugin.getDefault().getDialogSettings();
    }

	@Override
	public boolean performFinish(IProgressMonitor monitor) {
        if( !getWorkflow().isFinished() )
            getWorkflow().next(monitor);

		CatalogExportWizard catalogExportWizard = new CatalogExportWizard(getWorkflow(),getStateMap());
        catalogExportWizard.setContainer(getContainer());

        boolean performFinish = catalogExportWizard.performFinish(monitor);

//        TODO Something will need to be done here...
//        if( !performFinish )
//            getWorkflow().previous(monitor);

        return performFinish;
	}

	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	protected static class MyCatalogExport extends CatalogExport {
		MyCatalogExport(){
			// I'm just updating this code.  Apparently this class is here to disable initializing
			//  not sure why... Oh I think it is because they don't want the
			// dialog created.  They just want to be able to obtain the default workflow and
			// page mapping.
			super(false);
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
