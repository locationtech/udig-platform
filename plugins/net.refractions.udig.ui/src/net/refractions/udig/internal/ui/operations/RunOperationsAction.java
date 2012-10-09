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
package net.refractions.udig.internal.ui.operations;

import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.operations.OpAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class RunOperationsAction extends Action {

    @Override
    public void run() {
        RunOperationDialog dialog = new RunOperationDialog(Display.getDefault().getActiveShell(), 
                UiPlugin.getDefault().getOperationMenuFactory());
               
        dialog.open();
        
        if (dialog.getReturnCode() == Window.CANCEL)
            return;
        
        final OpAction[] actions = dialog.getSelection();
        for (OpAction action : actions) {
            action.run();
        }
    }
}
