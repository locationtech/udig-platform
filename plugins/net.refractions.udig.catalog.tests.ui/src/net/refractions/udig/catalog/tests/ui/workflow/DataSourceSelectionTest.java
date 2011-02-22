package net.refractions.udig.catalog.tests.ui.workflow;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.refractions.udig.catalog.ui.DataSourceSelectionPage;
import net.refractions.udig.catalog.ui.UDIGConnectionFactoryDescriptor;
import net.refractions.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import net.refractions.udig.catalog.ui.workflow.DataSourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardDialog;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.catalog.ui.workflow.Workflow.State;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DataSourceSelectionTest extends TestCase {

	Shell shell;

	Workflow workflow;

	WorkflowWizard wizard;

	WorkflowWizardDialog dialog;

	DataSourceSelectionState state;

	DataSourceSelectionPage page;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		state = new DataSourceSelectionState(true);
		page = new DataSourceSelectionPage();

		Map<Class< ? extends State>, WorkflowWizardPageProvider> map = new HashMap<Class< ? extends State>, WorkflowWizardPageProvider>();
		map.put(state.getClass(), new BasicWorkflowWizardPageFactory(page));

		workflow = new Workflow();
		workflow.setStates(new Workflow.State[] { state });

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

				fail = !d.getId().equals("net.refractions.udig.catalog.ui.WMS"); //$NON-NLS-1$
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
				List l = page.getDescriptors();
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
