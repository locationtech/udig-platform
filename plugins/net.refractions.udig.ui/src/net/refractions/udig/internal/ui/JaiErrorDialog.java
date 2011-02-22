package net.refractions.udig.internal.ui;

import net.refractions.udig.ui.internal.Messages;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class JaiErrorDialog extends IconAndMessageDialog {

	public JaiErrorDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		message = Messages.UDIGApplication_error_jai_warning_text;
		System.out.println("MESSAGES : " + message); //$NON-NLS-1$

		Composite composite = (Composite) super.createDialogArea(parent);
        ((GridLayout)composite.getLayout()).numColumns = 2;
        ((GridLayout)composite.getLayout()).makeColumnsEqualWidth = false;

        createMessageArea(composite);

        return composite;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	@Override
	protected Image getImage() {
		return getWarningImage();
	}


	protected void configureShell( Shell newShell ) {
        newShell.setText(Messages.UDIGApplication_error_jai_warning_title);
        newShell.setImage(UiPlugin.getDefault().create("icon32.gif").createImage()); //$NON-NLS-1$
    }

	public static void display() {
		JaiErrorDialog dialog = new JaiErrorDialog(Display.getCurrent().getActiveShell());
		dialog.open();
	}

}
