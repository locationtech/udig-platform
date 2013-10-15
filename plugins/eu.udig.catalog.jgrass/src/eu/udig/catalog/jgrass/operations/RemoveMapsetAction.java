/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.catalog.jgrass.operations;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.refractions.udig.ui.ExceptionDetailsDialog;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;

import eu.udig.catalog.jgrass.JGrassPlugin;
import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * Action to remove a mapset from disk.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class RemoveMapsetAction
        implements
            IObjectActionDelegate,
            IWorkbenchWindowActionDelegate,
            IWorkbenchWindowPulldownDelegate {

    IStructuredSelection selection = null;
    private IWorkbenchWindow window;

    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
    }

    public void run( IAction action ) {

        Display.getDefault().syncExec(new Runnable(){
            public void run() {

                Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                boolean answer = MessageDialog.openQuestion(shell, "WARNING",
                        "Are you sure you want to remove the selected mapsets from disk? This can't be undone!");
                if (answer) {
                    List toList = selection.toList();
                    for( Object object : toList ) {
                        if (object instanceof JGrassMapsetGeoResource) {

                            JGrassMapsetGeoResource mapsetr = (JGrassMapsetGeoResource) object;
                            String mapsetPath = mapsetr.getFile().getAbsolutePath();

                            try {
                                FileUtils.deleteDirectory(new File(mapsetPath));
                                JGrassCatalogUtilities.removeMapsetFromCatalog(mapsetr.getFile().getParent(), mapsetr.getFile()
                                        .getName());
                            } catch (IOException e) {
                                e.printStackTrace();
                                String message = "An error occurred while removing the mapset.";
                                ExceptionDetailsDialog.openError("ERROR", message, IStatus.ERROR, JGrassPlugin.PLUGIN_ID, e);
                            }

                        }
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
    }

    /*
    * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
    */
    public void dispose() {
    }

    /*
    * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
    */
    public void init( IWorkbenchWindow window ) {
        this.window = window;
        // do nothing
    }

    /*
    * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
    */
    public Menu getMenu( Control parent ) {
        return null;
    }

}