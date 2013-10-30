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
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.ui.DataSourceSelectionPage;
import org.locationtech.udig.catalog.ui.UDIGConnectionFactoryDescriptor;
import org.locationtech.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import org.locationtech.udig.catalog.ui.workflow.DataSourceSelectionState;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardDialog;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataSourceSelectionTest {

	Shell shell;

	Workflow workflow;
	
	WorkflowWizard wizard;

	WorkflowWizardDialog dialog;

	DataSourceSelectionState state;

	DataSourceSelectionPage page;

	@Before
	public void setUp() throws Exception {
		state = new DataSourceSelectionState(true);
		page = new DataSourceSelectionPage();

		Map<Class< ? extends State>, WorkflowWizardPageProvider> map = new HashMap<Class< ? extends State>, WorkflowWizardPageProvider>();
		map.put(state.getClass(), new BasicWorkflowWizardPageFactory(page));

		workflow = new Workflow();
		workflow.setStates(new State[] { state });
		
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
	public void testWorkbenchSelection() {
		try {
			URL url = new URL("http://wms.jpl.nasa.gov/wms.cgi?Service=WMS&Version=1.1.1&Request=GetCapabilities"); //$NON-NLS-1$
			workflow.setContext(url);
		} 
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		Assertion a1 = new Assertion() {
			@Override
			public void run() {
				IStructuredSelection sselection = 
					(IStructuredSelection) page.getViewer().getSelection(); 
				UDIGConnectionFactoryDescriptor d = 
					(UDIGConnectionFactoryDescriptor) sselection.getFirstElement(); 
					
				fail = !d.getId().equals("org.locationtech.udig.catalog.ui.WMS"); //$NON-NLS-1$
				if (!fail) {
					Button button = DialogDriver.findButton(dialog,IDialogConstants.NEXT_ID);
					fail = !button.isEnabled();
				}
			}
		};
		Object[] actions = new Object[] {
			a1, IDialogConstants.CANCEL_ID
		};
		
		DialogDriver driver = new DialogDriver(dialog,actions);
		driver.schedule();
		
		dialog.open();
		driver.cancel();
		
		assertFalse(a1.fail);
		
	}

	@Test
	public void testSelection() {
		// turn on and off a viewer selection
		Runnable r1 = new Runnable() {
			public void run() {
				page.getViewer().setSelection(new StructuredSelection());

				SelectionChangedEvent event = new SelectionChangedEvent(page
						.getViewer(), new StructuredSelection());
				page.selectionChanged(event);
			}
		};

		Assertion a1 = new Assertion() {
			@Override
			public void run() {
				Button next = DialogDriver.findButton(dialog,
						IDialogConstants.NEXT_ID);
				fail = next.isEnabled();
			}
		};
		Runnable r2 = new Runnable() {

			public void run() {
				List<UDIGConnectionFactoryDescriptor> l = page.getDescriptors();
				page.getViewer()
						.setSelection(new StructuredSelection(l.get(0)));
			}
		};
		Assertion a2 = new Assertion() {
			@Override
			public void run() {
				Button next = DialogDriver.findButton(dialog,
						IDialogConstants.NEXT_ID);
				fail = !next.isEnabled();
			}
		};
		Object[] actions = new Object[] { r1, a1, r2, a2,
				IDialogConstants.CANCEL_ID };
		DialogDriver driver = new DialogDriver(dialog, actions);
		driver.schedule();

		dialog.open();driver.cancel();
		driver.cancel();

		assertFalse(a1.fail);
		assertFalse(a2.fail);
	}
}
