/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.omsbox.view.actions;

import java.util.HashMap;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.ui.RunningProcessListDialog;
import eu.udig.omsbox.view.OmsBoxView;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class OmsStopExecutionAction implements IViewActionDelegate {

    private IViewPart view;

    public void init( IViewPart view ) {
        this.view = view;
    }

    public void run( IAction action ) {
        if (view instanceof OmsBoxView) {
            Shell shell = view.getSite().getShell();

            HashMap<String, Process> runningProcessesMap = OmsBoxPlugin.getDefault().getRunningProcessesMap();

            if (runningProcessesMap.size() == 0) {
                MessageDialog.openInformation(shell, "Process List", "No running processes available at the current time");
            } else {
                RunningProcessListDialog dialog = new RunningProcessListDialog();
                dialog.open(shell, SWT.MULTI);
            }

        }
    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

}
