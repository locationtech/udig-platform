package net.refractions.udig.catalog.tests.ui.workflow;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.refractions.udig.catalog.internal.ui.ConnectionPageDecorator;
import net.refractions.udig.catalog.internal.ui.ResourceSelectionPage;
import net.refractions.udig.catalog.ui.ConnectionFactoryManager;
import net.refractions.udig.catalog.ui.UDIGConnectionFactoryDescriptor;
import net.refractions.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import net.refractions.udig.catalog.ui.workflow.EndConnectionState;
import net.refractions.udig.catalog.ui.workflow.ResourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardDialog;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ResourceSelectionTest extends TestCase {
	Shell shell;

	WorkflowWizard wizard;

	WorkflowWizardDialog dialog;

	EndConnectionState conn;
	ResourceSelectionState state;

	ConnectionPageDecorator connPage;
	ResourceSelectionPage page;

	private Workflow workflow;
	
	@Override
	protected void setUp() throws Exception {
	
		ArrayList<String> l = new ArrayList<String>();
		l.add("net.refractions.udig.catalog.ui.WMS"); //$NON-NLS-1$
		
		UDIGConnectionFactoryDescriptor d = ConnectionFactoryManager.instance().getConnectionFactoryDescriptors(l).get(0);
		
		conn = new EndConnectionState(d,true);
		state = new ResourceSelectionState();
	
		connPage = new ConnectionPageDecorator();
		page = new ResourceSelectionPage("foo"); //$NON-NLS-1$

		Map<Class<? extends State>, WorkflowWizardPageProvider> map = 
			new HashMap<Class<? extends State>, WorkflowWizardPageProvider>();
		
		map.put(state.getClass(), new BasicWorkflowWizardPageFactory(page));
		map.put(conn.getClass(), new BasicWorkflowWizardPageFactory(connPage));

		workflow = new Workflow();
		workflow.setStates(new State[] { conn, state });
		
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
	
	public void testNormal() throws Exception {
		//create a context
			URL url = new URL("http://www.refractions.net:8080/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities"); //$NON-NLS-1$
			workflow.setContext(url);
			
		Assertion a1 = new Assertion() {
			@Override
			public void run() {
				fail = page.getViewer().getTree().getItemCount() != 1;
				if (!fail) {
					fail = page.getViewer().getTree().getItem(0).getItemCount() < 1;
				}
				if (!fail) 
					fail = page.getViewer().getCheckedElements().length != 0;
			}
		};
		Object[] actions = new Object[]{IDialogConstants.NEXT_ID, a1, IDialogConstants.CANCEL_ID};
		
		DialogDriver driver = new DialogDriver(dialog,actions);
		driver.schedule();
		
		dialog.open();
		
		assertFalse(a1.fail);
		driver.cancel();
	}

	public void testNormalSelectedGeoResource() throws Exception {
		//create a workbench selection
		URL url = new URL("http://www.refractions.net:8080/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities#gd:swamps"); //$NON-NLS-1$
		workflow.setContext(url);
		
		Assertion a1 = new Assertion() {
			@Override
			public void run() {
				fail = page.getViewer().getTree().getItemCount() != 1;
				if (!fail) {
					fail = page.getViewer().getTree().getItem(0).getItemCount() < 1;
				}
				if (!fail) 
					fail = page.getViewer().getCheckedElements().length != 2;
			}
		};
		Object[] actions = new Object[]{IDialogConstants.NEXT_ID, a1, IDialogConstants.CANCEL_ID};
		
		DialogDriver driver = new DialogDriver(dialog,actions);
		driver.schedule();
		
		dialog.setBlockOnOpen(true);
		dialog.open();
		
		assertFalse(a1.fail);
		driver.cancel();
	}


}