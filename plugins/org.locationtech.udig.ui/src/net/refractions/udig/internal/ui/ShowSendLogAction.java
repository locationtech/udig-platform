/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.internal.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Shows the dialog for sending the error log to the development team.
 * @author chorner
 */
public class ShowSendLogAction extends ActionDelegate implements
		IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	@Override
	public void run(IAction action) {
		SendLogDialog dialog = new SendLogDialog(window.getShell());
		dialog.setBlockOnOpen(true);
		dialog.open();
	}
	
	public void init(IWorkbenchWindow window) {
		this.window=window;
	}

}
