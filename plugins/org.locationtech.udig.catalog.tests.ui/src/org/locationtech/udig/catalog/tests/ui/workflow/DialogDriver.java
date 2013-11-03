/**
 * 
 */
package org.locationtech.udig.catalog.tests.ui.workflow;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
/**
 * This class is insane the good part is the push and findbuttons as helpers for testing.
 * 
 */
public class DialogDriver extends Job {

	public static long DELAY = 100;

	Dialog dialog;

	Object[] actions;

	/**
	 * This should be null if no errors occurred otherwise there should be debug
	 * message
	 */
	public String error;

	public DialogDriver(Dialog dialog, Object[] actions) {
		super("driver"); //$NON-NLS-1$

		this.dialog = dialog;
		this.actions = actions;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			final ArrayList<Boolean> l = new ArrayList<Boolean>();
			l.add(true);

			while (l.get(0)) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						l.set(0, dialog.getShell() == null
								|| dialog.getShell().isDisposed()
								|| !dialog.getShell().isVisible());
					}
				});
				Thread.sleep(DELAY);
			}

			for (int i = 0; i < actions.length; i++) {
				final Object action = actions[i];
				dialog.getShell().getDisplay().syncExec(new Runnable() {
					public void run() {
						if (action instanceof Assertion)
							((Assertion) action).run();
						else if (action instanceof Runnable)
							((Runnable) action).run();
						else if (action instanceof Integer)
							pushButton(dialog, ((Integer) action).intValue());

					}
				});

				Thread.sleep(DELAY);
			}

		}

		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}

	public static void pushButton(Dialog dialog, int id) {
		Shell shell = dialog.getShell();
		Button button = findButton(shell.getChildren(), id, shell);
		if( !button.isEnabled() )
			throw new RuntimeException("Error button to press is not enabled"); //$NON-NLS-1$
		button.notifyListeners(SWT.Selection, new Event());
	}

	public static Button findButton(Dialog dialog, int id) {
		Shell shell = dialog.getShell();
		Button found = findButton(shell.getChildren(), id, shell);
		if (found != null)
			return found;

		Display display = Display.getCurrent();
		shell = display.getActiveShell();
		found = findButton(shell.getChildren(), id, shell);
		if (found != null)
			return found;

		Shell[] shells = display.getShells();
		for (Shell shell2 : shells) {
			found = findButton(shell2.getChildren(), id, shell2);
			if (found != null)
				return found;
		}
		return null;
	}

	public static Button findButton(Control[] children, int id, Shell shell) {
		if (((Integer) shell.getDefaultButton().getData()).intValue() == id)
			return shell.getDefaultButton();

		for (Control child : children) {
			if (child instanceof Button) {
				Button button = (Button) child;
				Object data = button.getData();
				if (data != null) {
					if (((Integer) data).intValue() == id)
						return button;
				}
			}
			if (child instanceof Composite) {
				Composite composite = (Composite) child;
				Button button = findButton(composite.getChildren(), id, shell);
				if (button != null)
					return button;
			}
		}
		return null;
	}

}
