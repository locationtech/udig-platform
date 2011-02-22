package net.refractions.udig.catalog.internal.ui;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.CatalogTreeViewer;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ConnectionErrorPage;
import net.refractions.udig.catalog.ui.DataSourceSelectionPage;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.ConnectionErrorState;
import net.refractions.udig.catalog.ui.workflow.ConnectionFailurePage;
import net.refractions.udig.catalog.ui.workflow.ConnectionFailureState;
import net.refractions.udig.catalog.ui.workflow.DataSourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.EndConnectionState;
import net.refractions.udig.catalog.ui.workflow.IntermediateState;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardDialog;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPage;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.catalog.ui.workflow.Workflow.State;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class CatalogImport {

	Shell shell;
	private WorkflowWizardDialog dialog;
    protected WorkflowWizard wizard;

	public CatalogImport() {
		init();
	}

	void init() {
		Workflow workflow = createWorkflow();
		Map<Class<? extends State>, WorkflowWizardPageProvider> map = createPageMapping();
		wizard = createWorkflowWizard(workflow,map);

        if( Display.getCurrent()==null ){
		PlatformGIS.syncInDisplayThread(
			new Runnable() {
				public void run() {
                    shell=createShell();
				}
			}
		);
        }else{
            shell=createShell();
        }

		dialog = new WorkflowWizardDialog(
			shell, wizard
		);
		dialog.setBlockOnOpen(true);
	}

    /**
     * Must be called in the Display thread.
     */
	Shell createShell() {
        Display d=PlatformUI.getWorkbench().getDisplay();
        if( d==null )
            d=Display.getCurrent();
        Shell parent = d.getActiveShell();
		return new Shell(parent);
    }

    public WorkflowWizardDialog getDialog() {
		return dialog;
	}

	public void open() {
		Display.getDefault().asyncExec(
			new Runnable() {
				public void run() {
					dialog.open();
				};
			}
		);
	}

	/**
	 * Runs the workflow.
	 *
	 * @param monitor the monitor for
	 * @param context
	 * @return
	 */
	public boolean run(IProgressMonitor monitor, Object context) {
		dialog.getWorkflowWizard().getWorkflow().setContext(context);
        String bind = MessageFormat.format(Messages.CatalogImport_monitor_task, new Object[] {format(context)});
        monitor.beginTask(bind, IProgressMonitor.UNKNOWN);
        monitor.setTaskName(bind);
		try{
		   return dialog.runHeadless(new SubProgressMonitor(monitor,100));
		}finally{
			monitor.done();
		}
	}


    private String format( Object data ) {
        if( data instanceof URL ){
            return formatURL((URL)data);
        }if( data instanceof IGeoResource ){
            return ((IGeoResource)data).getIdentifier().getRef();
        }if (data instanceof IResolve ){
            return formatURL(((IResolve)data).getIdentifier());
        }
        return data.toString();
    }

    private String formatURL( URL url ) {
        return url.getProtocol()+"://"+url.getPath(); //$NON-NLS-1$
    }

	protected Workflow createWorkflow() {
		DataSourceSelectionState state = new DataSourceSelectionState(false);
		Workflow workflow = new Workflow(new Workflow.State[]{state});

		return workflow;
	}

	protected Map<Class<? extends State>, WorkflowWizardPageProvider> createPageMapping() {
		HashMap<Class<? extends State>, WorkflowWizardPageProvider> map = new HashMap<Class<? extends State>, WorkflowWizardPageProvider>();

		addToMap(map, DataSourceSelectionState.class, DataSourceSelectionPage.class);

        WorkflowWizardPageProvider provider = new ReflectionWorkflowWizardPageProvider(ConnectionPageDecorator.class);
        map.put(IntermediateState.class, provider);
        map.put(EndConnectionState.class, provider);

        addToMap(map, ConnectionErrorState.class, ConnectionErrorPage.class);

        addToMap(map, ConnectionFailureState.class, ConnectionFailurePage.class);
		return map;
	}

    private void addToMap( Map<Class< ? extends State>, WorkflowWizardPageProvider> map, Class<? extends State> key,
            Class<? extends WorkflowWizardPage> page ) {
        WorkflowWizardPageProvider pageFactory = new ReflectionWorkflowWizardPageProvider(page);
        map.put(key, pageFactory);
    }


    protected WorkflowWizard createWorkflowWizard(Workflow workflow, Map<Class<? extends State>, WorkflowWizardPageProvider> map) {
		return new CatalogImportWizard(workflow,map);
	}

	public static class CatalogImportWizard extends WorkflowWizard {


		public CatalogImportWizard( Workflow workflow,
                Map<Class< ? extends State>, WorkflowWizardPageProvider> map ) {
            super(workflow, map);
            setWindowTitle("Import");
        }

		@Override
		protected boolean performFinish(IProgressMonitor monitor) {
			//get the connection state from the pipe
			EndConnectionState connState =
				getWorkflow().getState(EndConnectionState.class);

			if (connState == null)
				return false;

			//add the services to the catalog
			final Collection<IService> services = connState.getServices();
			if (services == null || services.isEmpty())
				return false;

			//add the services to the catalog
			ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
			for (IService service : services) catalog.add(service);

			//select the first service
			//TODO: this has threading issues
            PlatformGIS.syncInDisplayThread(
				new Runnable() {
					public void run() {
						try {
						    CatalogView view = getCatalogView();
                            if( view!=null ){
                                CatalogTreeViewer treeviewer = view.getTreeviewer();
                                treeviewer.setSelection(
    								new StructuredSelection(services.iterator().next())
    							);
                            }
						}
						catch (Exception e) {
							CatalogUIPlugin.log(e.getLocalizedMessage(), e);
						}
					}
				}
			);

			return true;
		}

        protected boolean isShowCatalogView() {
            return true;
        }

        protected CatalogView getCatalogView() throws PartInitException {
            CatalogView view;
            if( isShowCatalogView() ){
                view = (CatalogView) PlatformUI
                .getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().showView(CatalogView.VIEW_ID);
            }else{
                view = (CatalogView) PlatformUI
                .getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().findView(CatalogView.VIEW_ID);
            }
            return view;
        }
	}
}

