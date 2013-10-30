/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
