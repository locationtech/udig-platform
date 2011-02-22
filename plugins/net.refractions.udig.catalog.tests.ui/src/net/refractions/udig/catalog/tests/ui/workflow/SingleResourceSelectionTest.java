package net.refractions.udig.catalog.tests.ui.workflow;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.refractions.udig.catalog.internal.ui.ConnectionPageDecorator;
import net.refractions.udig.catalog.internal.ui.ResourceSelectionPage;
import net.refractions.udig.catalog.tests.DummyService;
import net.refractions.udig.catalog.ui.ConnectionFactoryManager;
import net.refractions.udig.catalog.ui.UDIGConnectionFactoryDescriptor;
import net.refractions.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import net.refractions.udig.catalog.ui.workflow.EndConnectionState;
import net.refractions.udig.catalog.ui.workflow.ResourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardDialog;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SingleResourceSelectionTest extends TestCase {

	Shell shell;

	WorkflowWizard wizard;
	WorkflowWizardDialog dialog;

	EndConnectionState connState;
	ResourceSelectionState state;

	ResourceSelectionPage page;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ArrayList<String> l = new ArrayList<String>();
		l.add("net.refractions.udig.catalog.tests.ui.dummyPage"); //$NON-NLS-1$

		UDIGConnectionFactoryDescriptor d = ConnectionFactoryManager.instance().getConnectionFactoryDescriptors(l).get(0);

		connState = new EndConnectionState(d,true);
		state = new ResourceSelectionState();
		page = new ResourceSelectionPage("foo"); //$NON-NLS-1$

		Map<Class<? extends Workflow.State>, WorkflowWizardPageProvider> map =
			new HashMap<Class<? extends Workflow.State>, WorkflowWizardPageProvider>();

		map.put(connState.getClass(), new BasicWorkflowWizardPageFactory(new ConnectionPageDecorator()));
		map.put(state.getClass(), new BasicWorkflowWizardPageFactory(page));

		Workflow workflow = new Workflow();
		workflow.setStates(new Workflow.State[] { connState, state });
		workflow.setContext(new URL(DummyService.url.toExternalForm() + "#dummy")); //$NON-NLS-1$
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
