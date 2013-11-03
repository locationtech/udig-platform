/**
 * 
 */
package org.locationtech.udig.catalog.tests.ui.workflow;

import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPage;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

class SimplePage extends WorkflowWizardPage implements WorkflowWizardPageProvider {

	SimplePage() {
		this("simple"); //$NON-NLS-1$
	}
	
	SimplePage(String name) {
		super(name);
		setTitle(name);
		setDescription(name);
	}

	public void createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new FillLayout(SWT.VERTICAL));
		
		Label l = new Label(c, SWT.LEFT);
		l.setText(this.getName());
		
		setControl(c);
	}

    public WorkflowWizardPage getWorkflowWizardPage( State state ) {
        return this;
    }
	
}
