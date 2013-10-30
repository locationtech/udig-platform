/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.jconsole.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import eu.udig.jconsole.JConsolePlugin;
import eu.udig.jconsole.JavaEditor;
import eu.udig.jconsole.JavaFileEditorInput;

/**
 * Action to open an editor
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class ConsoleEditorActionNew implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    public void dispose() {
    }

    public void init( IWorkbenchWindow window ) {
        this.window = window;
    }

    public void run( IAction action ) {

        // JConsolePlugin.getDefault().gatherModules();

        try {
            File lastOpenFolder = JConsolePlugin.getDefault().getLastOpenFolder();
            FileDialog fileDialog = new FileDialog(window.getShell(), SWT.SAVE);
            fileDialog.setText("Save the new script...");
            fileDialog.setFilterExtensions(new String[]{"*.groovy"});
            fileDialog.setFilterPath(lastOpenFolder.getAbsolutePath());
            String path = fileDialog.open();
            if (path == null || path.length() < 1) {
                return;
            }
            if (!path.endsWith(".groovy")) {
                path = path + ".groovy";
            }

            File f = new File(path);
            if (!f.getParentFile().exists()) {
                return;
            }
            if (!f.exists()) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write("");
                bw.flush();
                bw.close();
            }

            File parentFolder = f.getParentFile();
            JConsolePlugin.getDefault().setLastOpenFolder(parentFolder.getAbsolutePath());

            JavaFileEditorInput jFile = new JavaFileEditorInput(f);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(jFile, JavaEditor.ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

}
