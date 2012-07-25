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
package net.refractions.udig.catalog.ui.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyDialogAction;

/**
 * A command hander to display an IGeoResource properties page.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class IGeoResourcePropertiesCommandHandler extends AbstractHandler implements IHandler {

    public Object execute( final ExecutionEvent arg0 ) throws ExecutionException {

        final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        
        IShellProvider shellProvider = new IShellProvider(){
            public Shell getShell() {
                return new Shell(activeWorkbenchWindow.getShell());
            }
        };

        IWorkbenchPart activePart = activeWorkbenchWindow.getActivePage().getActivePart();
        ISelectionProvider selectionProvider = activePart.getSite().getSelectionProvider();
        
        PropertyDialogAction action = new PropertyDialogAction( shellProvider, selectionProvider);
        PreferenceDialog dialog = action.createDialog();
        dialog.open();

        return null;
    }

}
