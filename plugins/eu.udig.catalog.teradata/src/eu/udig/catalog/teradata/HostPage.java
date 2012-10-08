/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
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
