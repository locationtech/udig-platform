/**
 * 
 */
package org.locationtech.udig.internal.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
/**
 * Utility class used to push buttons when writing test cases.
 * <p>
 * Having to write a class like this is insane, in a swing application
 * you can use the accessability API to push buttons during testing; does
 * SWT have similar functionality we can use?
 * <p>
 * The valuable part of this class is the push and findButton methods
 * for use in test cases.
 */
public class DialogDriver{

	public static final long DELAY = 100;

	/**
	 * This should be null if no errors occurred otherwise there should be debug
	 * message
	 */
	public String error;


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
		if (((Integer) shell.getDefaultButton().getData()).intValue() == id) //$NON-NLS-1$
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
