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
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardDialog;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BasicWorkflowDialogTest {
	
	WorkflowWizard wizard;
	WorkflowWizard bwizard;
	MyDataPipelineWizard mwizard;
	
	WorkflowWizardDialog dialog;
	WorkflowWizardDialog bdialog;
	WorkflowWizardDialog mdialog;
	
	SimpleState s1,s2,s3,s4;
	BlockingState b1,b2,b3,b4;
	
	Shell shell;
	
	@Before
	public void setUp() throws Exception {
		s1 = new SimpleState() {};
		s2 = new SimpleState() {};
		s3 = new SimpleState() {};
		s4 = new SimpleState() {};
		s1.next = s2;
		s2.next = s3;
		
		b1 = new BlockingState() {};
		b2 = new BlockingState() {};
		b3 = new BlockingState() {};
		b4 = new BlockingState() {};
		b1.next = b2;
		b2.next = b3;
		
		State[] states = new State[]{s1,s4};
		State[] bstates = new State[]{b1,b4};
		
		Map<Class<? extends State>,WorkflowWizardPageProvider> map =
			new HashMap<Class<? extends State>,WorkflowWizardPageProvider>();
		map.put(s1.getClass(), new SimplePage("one")); //$NON-NLS-1$
		map.put(s2.getClass(), new SimplePage("two")); //$NON-NLS-1$
		map.put(s3.getClass(), new SimplePage("three")); //$NON-NLS-1$
		map.put(s4.getClass(), new SimplePage("four")); //$NON-NLS-1$
		map.put(b1.getClass(), new SimplePage("one")); //$NON-NLS-1$
		map.put(b2.getClass(), new SimplePage("two")); //$NON-NLS-1$
		map.put(b3.getClass(), new SimplePage("three")); //$NON-NLS-1$
		map.put(b4.getClass(), new SimplePage("four")); //$NON-NLS-1$
		
		Workflow workflow = new Workflow();
		workflow.setStates(states);
		
		Workflow bworkflow = new Workflow();
		bworkflow.setStates(bstates);
		
		Workflow mworkflow = new Workflow();
		mworkflow.setStates(states);
		
		wizard = new WorkflowWizard(workflow, map);
		bwizard = new WorkflowWizard(bworkflow, map);
		mwizard = new MyDataPipelineWizard(mworkflow,map);
		
		shell = new Shell(Display.getDefault());
		
		dialog = new WorkflowWizardDialog(shell, wizard);
		dialog.setBlockOnOpen(true);
		
		bdialog = new WorkflowWizardDialog(shell, bwizard);
		bdialog.setBlockOnOpen(true);
		
		mdialog = new WorkflowWizardDialog(shell, mwizard);
		mdialog.setBlockOnOpen(true);
	}
	
	@After
	public void tearDown() throws Exception {
		if (!shell.isDisposed())
			shell.dispose();
	}
	
	@Ignore
	@Test
	public void testBlockingWizard() {
		Assertion a1 = new Assertion() {
			public void run() {
				Button next = DialogDriver.findButton(bdialog, IDialogConstants.NEXT_ID);
				Button prev = DialogDriver.findButton(bdialog, IDialogConstants.BACK_ID);
				Button fin = DialogDriver.findButton(bdialog, IDialogConstants.FINISH_ID);
				
				if (!next.isEnabled())
					fail = true;
				if (prev.isEnabled())
					fail = true;
				if (fin.isEnabled())
					fail = true;
			}
		};
		Assertion a2 = new Assertion() {
			public void run() {
				Button next = DialogDriver.findButton(bdialog, IDialogConstants.NEXT_ID);
				Button prev = DialogDriver.findButton(bdialog, IDialogConstants.BACK_ID);
				Button fin = DialogDriver.findButton(bdialog, IDialogConstants.FINISH_ID);
				
				if (next.isEnabled())
					fail = true;
				if (!prev.isEnabled())
					fail = true;
				if (!fin.isEnabled())
					fail = true;
			}
		};
		
		Object[] buttons = new Object[]{
			a1, IDialogConstants.NEXT_ID,IDialogConstants.NEXT_ID,
			IDialogConstants.NEXT_ID,a2,IDialogConstants.BACK_ID,
			IDialogConstants.BACK_ID,IDialogConstants.BACK_ID, a1,
			IDialogConstants.NEXT_ID,IDialogConstants.NEXT_ID,
			IDialogConstants.NEXT_ID,a2,IDialogConstants.FINISH_ID 
		};
		
		DialogDriver driver = new DialogDriver(bdialog, buttons);
		driver.schedule();
		
		bdialog.open();
		driver.cancel();
		
		assertFalse(a1.fail);
		assertFalse(a2.fail);
	}
	
	@Ignore
    @Test
	public void testNonBlockingWizard() {
		Assertion a1 = new Assertion() {
			public void run() {
				Button next = DialogDriver.findButton(dialog, IDialogConstants.NEXT_ID);
				Button prev = DialogDriver.findButton(dialog, IDialogConstants.BACK_ID);
				Button fin = DialogDriver.findButton(dialog, IDialogConstants.FINISH_ID);
				
				if (!next.isEnabled())
					fail = true;
				if (prev.isEnabled())
					fail = true;
				if (fin.isEnabled())
					fail = true;
			}
		};
		Assertion a2 = new Assertion() {
			public void run() {
				Button next = DialogDriver.findButton(dialog, IDialogConstants.NEXT_ID);
				Button prev = DialogDriver.findButton(dialog, IDialogConstants.BACK_ID);
				Button fin = DialogDriver.findButton(dialog, IDialogConstants.FINISH_ID);
				
				if (next.isEnabled())
					fail = true;
				if (!prev.isEnabled())
					fail = true;
				if (!fin.isEnabled())
					fail = true;
			}
		};
		
		Object[] buttons = new Object[]{
			a1, IDialogConstants.NEXT_ID,IDialogConstants.NEXT_ID,
			IDialogConstants.NEXT_ID,a2,IDialogConstants.BACK_ID,
			IDialogConstants.BACK_ID,IDialogConstants.BACK_ID, a1,
			IDialogConstants.NEXT_ID,IDialogConstants.NEXT_ID,
			IDialogConstants.NEXT_ID,a2,IDialogConstants.FINISH_ID 
		};
		
		DialogDriver driver = new DialogDriver(dialog, buttons);
		driver.schedule();
		
		dialog.open();
		driver.cancel();
		
		assertFalse(a1.fail);
		assertFalse(a2.fail);
	}
	
    @Test
	public void testRunHeadless() {
		s3.run = false;
		Assertion a1 = new Assertion() {
			@Override
			public void run() {
				IWizardPage page = dialog.getCurrentPage();
				fail = !"three".equals(page.getName()); //$NON-NLS-1$
			}
		};
		
		DialogDriver driver = new DialogDriver(dialog,new Object[]{a1, IDialogConstants.CANCEL_ID});
		driver.schedule();
		
		dialog.runHeadless(new DummyMonitor());
		
		driver.cancel();
		assertFalse(a1.fail);
	}
	
    @Test
	public void testRunHeadlessToFinish() {
		dialog.runHeadless(new DummyMonitor());
		assertTrue(dialog.getWorkflowWizard().getWorkflow().isFinished());
	}
	
    @Test
	public void testWizardPerformedFinish() {
		mdialog.runHeadless(new DummyMonitor());
		assertTrue(mwizard.finished);
	}
	
	static class MyDataPipelineWizard extends WorkflowWizard {

		public boolean finish = true;
		public boolean finished = false;
		
		public MyDataPipelineWizard(Workflow workflow, Map<Class<? extends State>, WorkflowWizardPageProvider> map) {
			super(workflow, map);
		}
		
		@Override
		protected boolean performFinish(IProgressMonitor monitor) {
			finished = true;
			return finish;
		}
		
	}
}
