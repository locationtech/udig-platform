/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.ui.workflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.internal.ui.ConnectionPageDecorator;
import org.locationtech.udig.catalog.tests.ui.CatalogTestsUIPlugin;
import org.locationtech.udig.catalog.ui.ConnectionFactoryManager;
import org.locationtech.udig.catalog.ui.UDIGConnectionFactoryDescriptor;
import org.locationtech.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import org.locationtech.udig.catalog.ui.workflow.EndConnectionState;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardDialog;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class ConnectionFileTest {
	Shell shell;

	WorkflowWizard wizard;

	WorkflowWizardDialog dialog;

	EndConnectionState state;

	ConnectionPageDecorator page;

	private Workflow workflow;

	@Before
	public void setUp() throws Exception {
		List<String> l = new ArrayList<String>();
		l.add("org.locationtech.udig.catalog.ui.openFileConnection");
		
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

	@After
	public void tearDown() throws Exception {
		if (!shell.isDisposed())
			shell.dispose();
	}
	
    @Test
	public void testButtonState() {
		Assertion a1 = new Assertion() {
			@Override
			public void run() {
				Button next = DialogDriver.findButton(dialog,IDialogConstants.NEXT_ID);
				//fail = next.isEnabled();
			}
		};
		Object[] actions = new Object[]{a1,IDialogConstants.CANCEL_ID};
		
		DialogDriver driver = new DialogDriver(dialog,actions);
		driver.schedule();
		
		dialog.open();
		
		assertFalse(a1.fail);
		driver.cancel();
	}
	
    @Test
	public void testWorkbenchSelection() {
		// create a workbench selection
		try {
			URL url = CatalogTestsUIPlugin.getDefault().getBundle().getEntry("data/");
			url = FileLocator.toFileURL(new URL(url, "streams.shp"));
			
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

	@Test
	public void testConnection() {
		//create a workbench selection
		try {
			URL url = CatalogTestsUIPlugin.getDefault().getBundle().getEntry("data/");
			url = FileLocator.toFileURL(new URL(url, "streams.shp"));
			workflow.setContext(url);
		} 
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		

		Object[] actions = new Object[]{
			IDialogConstants.NEXT_ID,IDialogConstants.CANCEL_ID
		};
		
		DialogDriver driver = new DialogDriver(dialog,actions);
		driver.schedule();
		
		dialog.open();
		driver.cancel();
		
		assertNotNull(state.getServices());
		assertFalse(state.getServices().isEmpty());
		
	}
}
