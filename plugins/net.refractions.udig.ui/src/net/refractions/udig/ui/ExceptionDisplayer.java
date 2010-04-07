/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
