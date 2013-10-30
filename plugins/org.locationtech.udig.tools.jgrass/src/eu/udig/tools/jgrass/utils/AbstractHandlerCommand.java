/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.utils;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.operations.IOp;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * Utility handler for launching IOps from commands.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public abstract class AbstractHandlerCommand extends AbstractHandler {

    public abstract Object execute( ExecutionEvent event ) throws ExecutionException;

    protected void runOp( final IOp op, Class< ? > checkClass ) throws Exception {
        final ILayer selectedLayer = ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer();
        if (selectedLayer == null) {
            // Display.getDefault().syncExec(new Runnable(){
            // public void run() {
            Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
            MessageDialog.openWarning(shell, "WARNING", "No layer selected");
            // }
            // });
            return;
        }

        if (checkClass != null) {
            Object resource = selectedLayer.getResource(checkClass, new NullProgressMonitor());
            if (resource == null) {
                // Display.getDefault().syncExec(new Runnable(){
                // public void run() {
                Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                MessageDialog.openWarning(shell, "WARNING", "The launched operation is not applicable on the selected layer.");
                // }
                // });
                return;
            }
        }

        IWorkbench wb = PlatformUI.getWorkbench();
        final IProgressService ps = wb.getProgressService();
        ps.busyCursorWhile(new IRunnableWithProgress(){
            public void run( IProgressMonitor pm ) {
                try {
                    op.op(Display.getDefault(), selectedLayer, pm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
