/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.ui.workflow;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * This class provides a bridge between a normal wizard and a {@link WorkflowWizard}. A Workflow
 * wizard requires a {@link WorkflowWizardDialog} in order to run correctly. This class hides the
 * original wizard dialog and opens a {@link WorkflowWizardDialog} when the wizard is activated.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class WorkflowWizardAdapter extends Wizard implements IImportWizard, IExportWizard, Listener {

    private WorkflowWizardDialog dialog;
    private final WorkflowWizard wizard;
    private WizardPage placeHolderPage;

    public WorkflowWizardAdapter( WorkflowWizard wizard ) {
        this.wizard = wizard;
        this.placeHolderPage = new WizardPage("placeholder"){ //$NON-NLS-1$

            public void createControl( Composite parent ) {
                setControl(new Composite(parent, SWT.NONE));
            }

            @Override
            public IWizard getWizard() {
                return WorkflowWizardAdapter.this;
            }

        };
    }
    
    @Override
    public void setContainer( IWizardContainer wizardContainer ) {
        super.setContainer(wizardContainer);
        getStartingPage();
    }

    @Override
    public boolean performFinish() {
        return false;
    }

    @Override
    public IWizardPage getStartingPage() {
        if (getContainer() == null) {
            // we're not open yet;
            return placeHolderPage;
        }
        Shell containerShell = getContainer().getShell();
        if (dialog == null) {
            Shell shell = new Shell(containerShell.getDisplay());
            configureShell(containerShell, shell);
            dialog = new WorkflowWizardDialog(shell, wizard);
            dialog.setBlockOnOpen(false);

            dialog.open();
            dialog.setAdapter(this, placeHolderPage.getPreviousPage());
            // add a listener that will close the original dialog when the new dialog is closed
            dialog.getShell().addListener(SWT.Dispose, this);
        }
        
        
        Shell adapterShell = dialog.getShell();
        configureShell(containerShell, adapterShell);
        adapterShell .setVisible(true);
        containerShell.setVisible(false);

        return placeHolderPage;
    }

    /**
     * Called by the {@link WorkflowWizardDialog} when the back button is pressed on the first page of that wizard.
     */
    void backPressed() {
        Shell containerShell = getContainer().getShell();
        Shell adapterShell = dialog.getShell();

        configureShell(adapterShell, containerShell);
        containerShell.setVisible(true);
        adapterShell.setVisible(false);
    }

    private void configureShell( Shell from, Shell to ) {
        to.setLocation(from.getLocation());
        to.setSize(from.getSize());
        getContainer().showPage(placeHolderPage.getPreviousPage());
    }

    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        if (wizard instanceof IWorkbenchWizard) {
            IWorkbenchWizard workbenchWizard = (IWorkbenchWizard) wizard;
            workbenchWizard.init(workbench, selection);
        }
    }

    public void handleEvent( Event event ) {
        // this is called when the dialog is closed either by cancel or finish
        if (getContainer() != null && getContainer().getShell() != null) {
            getContainer().getShell().close();
        }
    }

}
