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
package net.refractions.udig.validation.ui;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

/**
 * The dialog box used to select which validations to perform.
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.0.1
 */
public class ValidationDialogOp implements IOp {

    private Dialog dialog;
    private ILayer[] layer;

    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
        // define the ILayer array (save it as a private var)
        if (target.getClass().isArray()) {
            layer = (ILayer[]) target;
        } else {
            layer = new ILayer[1];
            layer[0] = (ILayer) target;
        }
        // create or find the dialog
        display.asyncExec(new Runnable(){

            public void run() {
                if (dialog == null) {
                    dialog = new ValidationDialog(display.getActiveShell(), layer);
                }
                dialog.open();
            }
        });

        // done
        monitor.done();
    }
}
