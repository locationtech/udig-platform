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
package net.refractions.udig.internal.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Shows a dialog containing the error log and information on how submit an issue for the uDig
 * development team.
 * 
 * @author pjessup
 * @since 1.2.0
 */
public class ShowSubmitIssueAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    @Override
    public void run( IAction action ) {
        SubmitIssueDialog dialog = new SubmitIssueDialog(window.getShell());
        dialog.setBlockOnOpen(true);
        dialog.open();
    }

    public void init( IWorkbenchWindow window ) {
        this.window = window;
    }
}