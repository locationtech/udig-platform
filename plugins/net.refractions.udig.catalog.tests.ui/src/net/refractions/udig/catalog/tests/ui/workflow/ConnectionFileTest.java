package net.refractions.udig.catalog.tests.ui.workflow;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.refractions.udig.catalog.internal.ui.ConnectionPageDecorator;
import net.refractions.udig.catalog.ui.ConnectionFactoryManager;
import net.refractions.udig.catalog.ui.UDIGConnectionFactoryDescriptor;
import net.refractions.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import net.refractions.udig.catalog.ui.workflow.EndConnectionState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardDialog;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ConnectionFileTest extends TestCase {
	Shell shell;

	WorkflowWizard wizard;

	WorkflowWizardDialog dialog;

	EndConnectionState state;

	ConnectionPageDecorator page;

	private Workflow workflow;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ArrayList<String> l = new ArrayList<String>();
		l.add("net.refractions.udig.catalog.ui.openFilePage"); //$NON-NLS-1$
		
		UDIGConnectionFactoryDescriptor d = ConnectionFactoryManager.instance().getConnectionFactoryDescriptors(l).get(0);
		state = new EndConnectionState(d,true);
		page = new ConnectionPageDecorator();

		Map<Class<? extends State>, WorkflowWizardPageProvider> map = new HashMap<Class<? extends State>, WorkflowWizardPageProvider>();
		map.put(state.getClass(), new BasicWorkflowWizardPageFactory(page));
		map.put(SimpleState.class, new SimplePage());

		workflow = new Workflow();
		workflow.setStates(new State[] { state, new SimpleState() });
		wizard = new WorkflowWizard(workflow, map);

		shell = new Shell(Display.getDefault());
		dialog = new WorkflowWizardDialog(shell, wizard);
		dialog.setBlockOnOpen(true);
	}

	@Override
	protected void tearDown() throws Exception {
		if (!shell.isDisposed())
			shell.dispose();
	}
    
    public void testPlaceholder() throws Exception {
        //TODO fix tests in this class
    }
	
//	public void testButtonState() {
//		Assertion a1 = new Assertion() {
//			@Override
//			public void run() {
//				Button next = DialogDriver.findButton(dialog,IDialogConstants.NEXT_ID);
//				//fail = next.isEnabled();
//			}
//		};
//		Object[] actions = new Object[]{a1,IDialogConstants.CANCEL_ID};
//		
//		DialogDriver driver = new DialogDriver(dialog,actions);
//		driver.schedule();
//		
//		dialog.open();
//		
//		assertFalse(a1.fail);
//		driver.cancel();
//	}
	
//	public void testWorkbenchSelection() {
//		// create a workbench selection
//		try {
//			URL url = CatalogTestsUIPlugin.getDefault().getBundle()
//				.getEntry("data/");
//			url = FileLocator.toFileURL(new URL(url, "streams.shp"));
//			
//			workflow.setContext(url);
//		} 
//		catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
//		
//		Assertion a1 = new Assertion() {
//			@Override
//			public void run() {
//				Button next = DialogDriver.findButton(dialog,IDialogConstants.NEXT_ID);
//				fail = !next.isEnabled();
//			}
//		};
//		Object[] actions = new Object[]{a1,IDialogConstants.CANCEL_ID};
//		
//		DialogDriver driver = new DialogDriver(dialog,actions);
//		driver.schedule();
//		
//		dialog.open();
//		
//		assertFalse(a1.fail);
//		driver.cancel();
//	}

//	public void testConnection() {
//		//create a workbench selection
//		try {
//			URL url = CatalogTestsUIPlugin.getDefault().getBundle()
//				.getEntry("data/"); //$NON-NLS-1$
//			url = FileLocator.toFileURL(new URL(url, "streams.shp")); //$NON-NLS-1$
//			workflow.setContext(url);
//		} 
//		catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
//		
//
//		Object[] actions = new Object[]{
//			IDialogConstants.NEXT_ID,IDialogConstants.CANCEL_ID
//		};
//		
//		DialogDriver driver = new DialogDriver(dialog,actions);
//		driver.schedule();
//		
//		dialog.open();
//		driver.cancel();
//		
//		assertNotNull(state.getServices());
//		assertFalse(state.getServices().isEmpty());
//		
//	}
}