/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.ui.internal.Messages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * Provides a consistent way to handle error notifications.
 * 
 * @author chorner
 * @since 1.1.0
 */
public class ErrorManager {
    
    private ErrorManager() {
    }
    
    private static final ErrorManager instance = new ErrorManager();
    
    /**
     * Returns the singleton instance
     *
     * @return the singleton instance
     */
    public static ErrorManager get() {
        return instance;
    }

    public void displayError( final String title, final String message ) {
        PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                Shell shell = Display.getDefault().getActiveShell();
                MessageDialog.openError(shell, title, message);
            }
        });
    }
    
    public void displayException( final Throwable exception, final String message, final String pluginID) {
        List<Throwable> exceptions = new ArrayList<Throwable>();
        exceptions.add(exception);
        displayExceptions(exceptions, message, pluginID);
    }
    
    public void displayExceptions( final List<Throwable> exceptions, String message,
            final String pluginID ) {
        final String m;
        if( message==null )
            m=""; //$NON-NLS-1$
        else
            m=message;
        
        final MultiStatus multi = new MultiStatus(pluginID, IStatus.OK, message, null);
        for( Throwable exception : exceptions ) {
            Status status = new Status(IStatus.ERROR, pluginID, IStatus.ERROR, exception
                    .getLocalizedMessage(), exception);
            multi.add(status);
        }

        PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                Dialog dialog = new ErrorDialog(Display.getDefault().getActiveShell(), Messages.ErrorManager_very_informative_error,
                        m, multi, IStatus.ERROR);
                dialog.open();
            }
        });
    }

}
