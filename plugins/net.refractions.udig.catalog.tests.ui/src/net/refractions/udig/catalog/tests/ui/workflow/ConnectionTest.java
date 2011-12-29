package net.refractions.udig.catalog.tests.ui.workflow;

import java.net.URL;
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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Ignore;

@Ignore
public class ConnectionTest extends TestCase {
	Shell shell;

	WorkflowWizard wizard;

	WorkflowWizardDialog dialog;

	EndConnectionState state;

	ConnectionPageDecorator page;

	private Workflow workflow;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
	}

    private void init( String urlString ) {
        ArrayList<String> l = new ArrayList<String>();
		l.add(urlString); 
		
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
	
	public void testButtonState() {
        init("net.refractions.udig.catalog.ui.WMS"); //$NON-NLS-1$

		Assertion a1 = new Assertion() {
			@Override
			public void run() {
				Button next = DialogDriver.findButton(dialog,IDialogConstants.NEXT_ID);
				fail = !next.isEnabled();
			}
		};
		Object[] actions = new Object[]{a1,IDialogConstants.CANCEL_ID};
		
		DialogDriver driver = new DialogDriver(dialog,actions);
		driver.schedule();
		
		dialog.open();
		
		assertFalse(a1.fail);
		driver.cancel();
	}
	
	public void testWorkbenchSelection() {
        init("net.refractions.udig.catalog.ui.WMS"); //$NON-NLS-1$

		// create a workbench selection
		try {
			URL url = new URL("http://www.refractions.net:8080/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities"); //$NON-NLS-1$
			workflow.setContext(url);
		} 
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		Assertion a1 = new Assertion() {
			@Override
			public void run() {
				Button next = DialogDriver.findButton(dialog,IDialogConstants.NEXT_ID);
				fail = !next.isEnabled();
			}
		};
		Object[] actions = new Object[]{a1,IDialogConstants.CANCEL_ID};
		
		DialogDriver driver = new DialogDriver(dialog,actions);
		driver.schedule();
		
		dialog.open();
		
		assertFalse(a1.fail);
		driver.cancel();
	}

	public void testConnection() {
        init("net.refractions.udig.catalog.ui.WMS"); //$NON-NLS-1$

		//create a workbench selection
		try {
//			URL url = CatalogTestsUIPlugin.getDefault().getBundle()
//				.getEntry("data/"); //$NON-NLS-1$
//			url = FileLocator.toFileURL(new URL(url, "lakes.shp")); //$NON-NLS-1$
//			
			URL url = new URL("http://www.refractions.net:8080/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities"); //$NON-NLS-1$
			workflow.setContext(url);
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		Object[] actions = new Object[]{IDialogConstants.NEXT_ID,IDialogConstants.CANCEL_ID};
		
		DialogDriver driver = new DialogDriver(dialog,actions);
		driver.schedule();
		
		dialog.open();
		driver.cancel();
		
		assertNotNull(state.getServices());
		assertFalse(state.getServices().isEmpty());
		
	}
}