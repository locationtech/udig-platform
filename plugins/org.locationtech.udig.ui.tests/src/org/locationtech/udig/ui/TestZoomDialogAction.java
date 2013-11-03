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
package org.locationtech.udig.ui;

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
