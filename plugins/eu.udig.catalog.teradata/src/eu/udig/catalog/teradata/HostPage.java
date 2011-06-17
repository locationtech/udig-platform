package eu.udig.catalog.teradata;


import net.refractions.udig.catalog.service.database.UserHostPage;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;

import org.eclipse.swt.widgets.Composite;

import eu.udig.catalog.teradata.internal.Messages;

public class HostPage extends UserHostPage {

	public HostPage() {
		super(new TeradataDialect());
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		WorkflowWizard wizard = getWizard();
		wizard.setWindowTitle(Messages.TeradataGeoResource_hostPageTitle);

		if(!Activator.checkTeradataDrivers()) {
			wizard.performCancel();
			parent.getShell().close();
		} 
	}
}
