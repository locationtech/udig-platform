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
package org.locationtech.udig.project.ui.internal.actions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

/**
 * Loads a Map file into a project.
 *
 * @author utaddei
 *
 */
public class OpenMapAction implements IViewActionDelegate,
        IWorkbenchWindowActionDelegate {

    public void run(IAction action) {
        Shell activeShell = Display.getDefault().getActiveShell();
        String path = null;

        while (path == null) {
            FileDialog dialog = new FileDialog(activeShell);
            // dialog.setFilterPath(Messages.OpenProject_newProject_filename);
            // dialog.setMessage(Messages.OpenProject_selectProject);
            dialog.setText(Messages.OpenProject_openProject);
            path = dialog.open();
            if (path == null) {
                return;
            }

            loadMapFromString(path, ApplicationGIS.getActiveProject(), true);
        }
    }

    /**
     * Loads a map into a target project or into the active project, if target
     * is null.
     *
     * @param url
     *            the URL pointing to the map file. Cannot be null.
     * @param target
     *            the project into which to load the map. If null, the target is
     *            the currently active project.
     *
     * @param openMap
     *            whether to open the map UI
     */
    public void loadMapFromURL(URL url, IProject target, boolean openMap) {

        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null.");
        }

        IProjectElement elem = null;
        if (target == null) {
            target = ApplicationGIS.getActiveProject();
        }
        Shell activeShell = null;// invalid thread access when drag and dropping
        // if shell =
        // Display.getDefault().getActiveShell();
        try {
            elem = ApplicationGIS.loadProjectElement(url, target);
        } catch (Exception e) {
            ProjectUIPlugin.log("Could not load map from file: " + url, e); //$NON-NLS-1$
            MessageDialog
                    .openError(
                            activeShell,
                            "Error reading map file",
                            "An unexpected error occurred while reading the map file.\nPlease send error log.");
            return;
        }

        if (!(elem instanceof IMap)) {
            MessageDialog.openError(activeShell, "Error reading map file",
                    "The selected file does.");
            return;
        }

        if (openMap) {
            Map map = (Map) elem;
            ApplicationGIS.openMap(map);
        }
    }

    /**
     * Loads a map into a target project or into the active project, if target
     * is null.
     *
     * @param file
     *            a string pointing to the map file. Cannot be null.
     * @param target
     *            the project into which to load the map. If null, the target is
     *            the currently active project.
     * @param openMap
     *            whether to open the map UI
     */
    public void loadMapFromString(String filename, IProject target,
            boolean openMap) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null.");
        }

        File f = new File(filename);
        if (!f.exists()) {
            throw new RuntimeException("File does not exist: "
                    + f.getAbsolutePath());
        }

        URL url;
        try {
            url = f.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        loadMapFromURL(url, target, openMap);
    }

    public void init(IViewPart view) {
        // nothing to do for now
    }

    public void selectionChanged(IAction action, ISelection selection) {
        // nothing to do for now
    }

    public void dispose() {
        // nothing to do for now
    }

    public void init(IWorkbenchWindow window) {
        // nothing to do for now
    }

}
