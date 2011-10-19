package net.refractions.udig.tutorials.urladapter;

import java.net.URL;

import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DisplayURLOp implements IOp {

    public void op(final Display display, Object target, IProgressMonitor monitor)
            throws Exception {
        final URL url = (URL) target;
        display.asyncExec(new Runnable() {
            public void run() {
                Shell shell = display.getActiveShell();
                MessageDialog.openInformation(shell, "URL display", url
                        .toExternalForm());
            }
        });
    }

}
