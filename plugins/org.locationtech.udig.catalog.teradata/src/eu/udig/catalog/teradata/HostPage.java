/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
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
