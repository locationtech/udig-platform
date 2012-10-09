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
package net.refractions.udig.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

@SuppressWarnings("nls")
public class TestZoomDialogAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {

    public void init( IWorkbenchWindow window ) {
    }
    
    @Override
    public void run( IAction action ) {
        MessageDialog d=new MessageDialog(Display.getDefault().getActiveShell(), 
                "Test", null, "Test zoom dialog", MessageDialog.INFORMATION,
                new String[]{"OK"}, 1);
        ZoomingDialog zd=new ZoomingDialog(Display.getDefault().getActiveShell(), d, new Rectangle(0,0,10,10));
        zd.open();
    }

}
