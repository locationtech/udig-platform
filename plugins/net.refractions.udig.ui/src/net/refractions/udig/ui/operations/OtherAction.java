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
package net.refractions.udig.ui.operations;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Opens a dialog with all the Operations
 * 
 * @author Jody Garnett
 * @since 1.1.0
 */
public class OtherAction implements IWorkbenchWindowActionDelegate {

    private ISelection selection;
    private IWorkbenchWindow window;
    public void init( IWorkbenchWindow window ) {
        this.window = window;
    }

    public void run( IAction action ) {        
        OperationDialog opDialog = new OperationDialog( window.getShell(), selection);
        
        // We can make the operation dialog display a single OperationCategory
        // if you change the input
        opDialog.open();
    }

    public void dispose() {
        window = null;
        selection = null;
    }
    
    public void selectionChanged( IAction action, ISelection selection ) {
        this.selection = selection;        
    }

}
