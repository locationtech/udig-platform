/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.ui;

import java.util.List;

import net.refractions.udig.ui.internal.Messages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

public class ExceptionDisplayer {
    public static void displayExceptions( final List<Throwable> exceptions, final String message, final String pluginID) {
        final MultiStatus multi = new MultiStatus(pluginID, IStatus.OK, message, null);
        for (Throwable exception : exceptions) {
            Status status = new Status(IStatus.ERROR, pluginID, IStatus.ERROR, exception.getLocalizedMessage(), exception);
            multi.add(status);
        }
        
       PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                Dialog dialog = new ErrorDialog(Display.getDefault().getActiveShell(), Messages.ExceptionDisplayer_very_informative_error, message, multi, IStatus.ERROR);
                dialog.open();
            }
        });
    }
}
