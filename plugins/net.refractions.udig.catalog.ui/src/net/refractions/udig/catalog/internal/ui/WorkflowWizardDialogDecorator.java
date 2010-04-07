/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.ui;

import java.lang.reflect.InvocationTargetException;

import net.refractions.udig.catalog.ui.workflow.WorkflowWizardAdapter;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Poses as the Container for the {@link WorkflowWizardAdapter}. Wraps a WizardDialog and
 * delegates most calls to it.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class WorkflowWizardDialogDecorator implements IWizardContainer {

    private WizardDialog container;

    public WorkflowWizardDialogDecorator( WizardDialog container ) {
        this.container = container;
    }

    public IWizardPage getCurrentPage() {
        return container.getCurrentPage();
    }

    public Shell getShell() {
        return container.getShell();
    }

    public void showPage( IWizardPage page ) {
        container.showPage(page);
    }

    public void updateButtons() {
        container.updateButtons();
    }

    public void updateMessage() {
        container.updateMessage();
    }

    public void updateTitleBar() {
        container.updateTitleBar();
    }

    public void updateWindowTitle() {
        container.updateWindowTitle();
    }

    public void run( boolean fork, boolean cancelable, final IRunnableWithProgress request )
            throws InvocationTargetException, InterruptedException {

        ProgressMonitorPart progressMonitor = getProgressMonitor(getShell().getChildren());
        if (progressMonitor instanceof ProgressMonitorPart) {
            ProgressMonitorPart part = (ProgressMonitorPart) progressMonitor;
            if (Display.getCurrent() != null)
                part.setVisible(true);

            try {
                setEnablement(container.buttonBar, false);
                if (fork) {
                    PlatformGIS.run(request, part);
                } else {
                    PlatformGIS.runBlockingOperation(request, part);
                }
            } finally {
                setEnablement(container.buttonBar, true);
                if (Display.getCurrent() != null && !part.isDisposed())
                    part.setVisible(false);
            }
        } else {
            if (fork) {
                PlatformGIS.run(request);
            } else {
                PlatformGIS.runBlockingOperation(request, ProgressManager.instance().get());
            }
        }
    }

    protected ProgressMonitorPart getProgressMonitor( Control[] controls ) {
        for( Control control : controls ) {
            if (control instanceof Composite) {
                Control[] children = ((Composite) control).getChildren();
                ProgressMonitorPart found = getProgressMonitor(children);
                if (found != null) {
                    return found;
                }
            } else if (control instanceof ProgressMonitorPart) {
                return (ProgressMonitorPart) control;
            }
        }
        return null;
    }

    private void setEnablement( Control controlA, boolean enabled ) {
        Composite buttonBar = (Composite) container.buttonBar;
        if (controlA == null)
            return;
        if (controlA instanceof Composite) {
            Composite composite = (Composite) controlA;
            Control[] children = composite.getChildren();
            for( Control control : children ) {
                setEnablement(control, enabled);
            }
        } else {
            if (controlA != getButton(buttonBar, IDialogConstants.CANCEL_ID))
                controlA.setEnabled(enabled);
        }

    }

    private Button getButton( Composite buttonBar, int id ) {
        Control[] children = buttonBar.getChildren();
        for( Control control : children ) {
            if (control instanceof Composite) {
                Button button = getButton((Composite) control, id);
                if (button != null) {
                    return button;
                }
            } else if (control instanceof Button) {
                if (control.getData().equals(id)) {
                    return (Button) control;
                }
            }
        }
        return null;
    }
}