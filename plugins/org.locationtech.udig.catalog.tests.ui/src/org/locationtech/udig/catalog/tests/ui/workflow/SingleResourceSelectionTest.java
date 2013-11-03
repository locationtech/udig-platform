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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.internal.ui.ConnectionPageDecorator;
import org.locationtech.udig.catalog.internal.ui.ResourceSelectionPage;
import org.locationtech.udig.catalog.tests.DummyService;
import org.locationtech.udig.catalog.ui.ConnectionFactoryManager;
import org.locationtech.udig.catalog.ui.UDIGConnectionFactoryDescriptor;
import org.locationtech.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import org.locationtech.udig.catalog.ui.workflow.EndConnectionState;
import org.locationtech.udig.catalog.ui.workflow.ResourceSelectionState;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardDialog;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SingleResourceSelectionTest {
	
	Shell shell;
	
	WorkflowWizard wizard;
	WorkflowWizardDialog dialog;
	
	EndConnectionState connState;
	ResourceSelectionState state;
	
	ResourceSelectionPage page;
	
	@Before
	public void setUp() throws Exception {
		ArrayList<String> l = new ArrayList<String>();
		l.add("org.locationtech.udig.catalog.tests.ui.dummyPage"); //$NON-NLS-1$
		
		UDIGConnectionFactoryDescriptor d = ConnectionFactoryManager.instance().getConnectionFactoryDescriptors(l).get(0);

		connState = new EndConnectionState(d,true);
		state = new ResourceSelectionState();
		page = new ResourceSelectionPage("foo"); //$NON-NLS-1$
		
		Map<Class<? extends State>, WorkflowWizardPageProvider> map = 
			new HashMap<Class<? extends State>, WorkflowWizardPageProvider>();
		
		map.put(connState.getClass(), new BasicWorkflowWizardPageFactory(new ConnectionPageDecorator()));
		map.put(state.getClass(), new BasicWorkflowWizardPageFactory(page));
		
		Workflow workflow = new Workflow();
		workflow.setStates(new State[] { connState, state });
		workflow.setContext(new URL(DummyService.url.toExternalForm() + "#dummy")); //$NON-NLS-1$
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
	public void test() {
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
		Object[] actions = new Object[]{
			IDialogConstants.NEXT_ID, a1, IDialogConstants.CANCEL_ID
		};
		
		DialogDriver driver = new DialogDriver(dialog,actions);
		driver.schedule();
		
		dialog.open();
		
		assertFalse(a1.fail);
		driver.cancel();
	}
}
