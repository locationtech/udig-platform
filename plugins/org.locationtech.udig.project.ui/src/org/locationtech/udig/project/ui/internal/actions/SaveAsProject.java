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
package org.locationtech.udig.project.ui.internal.actions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.impl.MapImpl;
import org.locationtech.udig.project.ui.wizard.export.project.ExportProjectUtils;
import org.locationtech.udig.ui.ProgressManager;

/**
 * Action to save a project to disk in any location. Links to export.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class SaveAsProject implements IWorkbenchWindowActionDelegate {
    private IStructuredSelection selection;
    private IWorkbenchWindow window;
    private Project project;

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action ) {
        Object firstElement = selection.getFirstElement();

        if (firstElement instanceof MapImpl) {
            MapImpl map = (MapImpl) firstElement;
            project = (Project) map.getProject();
        }

        if (firstElement instanceof Project) {
            project = (Project) firstElement;
        }
        Display.getDefault().syncExec(new Runnable(){
            public void run() {
                Shell shell = null;
                if (window == null || window.getShell() == null) {
                    shell = Display.getDefault().getActiveShell();
                }else{
                    shell = window.getShell();
                }
                DirectoryDialog fileDialog = new DirectoryDialog(shell, SWT.OPEN);
                fileDialog.setMessage(Messages.SaveProject_Destination);
                String path = fileDialog.open();

                URI origURI = project.eResource().getURI();
                File file = new File(origURI.toFileString());

                String destinationUdigFolder = path + File.separator + project.getName() + ".udig";
                String destinationProject = destinationUdigFolder + File.separator + file.getName();

                File dest = new File(destinationUdigFolder);
                if (dest.exists()) {
                    boolean isOk = MessageDialog.openConfirm(shell, Messages.SaveProject_Export, //$NON-NLS-1$
                            Messages.SaveProject_Overwrite);
                    if (isOk) {
                        try {
                            FileUtils.deleteDirectory(dest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }
                }

                if (path != null && path.length() > 0) {
                    ExportProjectUtils.exportProject(project, path, ProgressManager.instance().get(null));
                    File destPrj = new File(destinationProject);
                    if (destPrj.exists()) {
                        MessageDialog.openInformation(shell, Messages.SaveProject_Export, //$NON-NLS-1$
                                Messages.SaveProject_Success);
                    } else {
                        MessageDialog.openError(shell, Messages.SaveProject_Export, //$NON-NLS-1$
                                Messages.SaveProject_Fail);
                    }

                }
            }
        });

    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection ) {
        if (selection instanceof IStructuredSelection)
            this.selection = (IStructuredSelection) selection;
        else
            this.selection = new StructuredSelection();
    }

    public void dispose() {
    }

    public void init( IWorkbenchWindow window ) {
        this.window = window;
    }

}
