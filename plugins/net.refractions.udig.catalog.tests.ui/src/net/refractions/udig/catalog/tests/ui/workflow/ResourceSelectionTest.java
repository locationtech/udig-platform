/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.tests.ui.workflow;

import static org.junit.Assert.assertFalse;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResourceSelectionTest {
	Shell shell;

	WorkflowWizard wizard;

	WorkflowWizardDialog dialog;

	EndConnectionState conn;
	ResourceSelectionState state;

	ConnectionPageDecorator connPage;
	ResourceSelectionPage page;

	private Workflow workflow;
	
	@Before
	public void setUp() throws Exception {
	
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
	
	@After
	public void tearDown() throws Exception {
		if (!shell.isDisposed())
			shell.dispose();
	}
    
	@Test
	public void testNormal() throws Exception {
		//create a context
		URL url = new URL("http://demo.opengeo.org/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities"); //$NON-NLS-1$
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

    @Test
	public void testNormalSelectedGeoResource() throws Exception {
		//create a workbench selection
		URL url = new URL("http://demo.opengeo.org/geoserver/wms?Service=WMS&Version=1.1.1&Request=GetCapabilities#topp:tasmania_cities"); //$NON-NLS-1$
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