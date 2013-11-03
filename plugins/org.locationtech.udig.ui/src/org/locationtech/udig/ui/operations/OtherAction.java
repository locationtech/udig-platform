/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui.operations;


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
