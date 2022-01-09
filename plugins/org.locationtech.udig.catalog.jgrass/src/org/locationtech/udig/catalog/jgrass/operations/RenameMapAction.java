/**
 * JGrass - Free Open Source Java GIS http://www.jgrass.org
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.operations;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.geotools.gce.grassraster.JGrassConstants;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import org.locationtech.udig.catalog.jgrass.utils.JGrassCatalogUtilities;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.commands.DeleteLayersCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.PlatformGIS;

/**
 * Action to remove a map from disk.
 *
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class RenameMapAction implements IObjectActionDelegate, IWorkbenchWindowActionDelegate,
        IWorkbenchWindowPulldownDelegate {

    IStructuredSelection selection = null;

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {

    }

    @Override
    public void run(IAction action) {

        IRunnableWithProgress operation = new IRunnableWithProgress() {

            @Override
            public void run(final IProgressMonitor pm)
                    throws InvocationTargetException, InterruptedException {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {

                        final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                        final Object map = selection.getFirstElement();

                        try {
                            pm.beginTask("Renaming map...", IProgressMonitor.UNKNOWN);
                            pm.worked(1);
                            if (map instanceof JGrassMapGeoResource) {
                                JGrassMapGeoResource mr = (JGrassMapGeoResource) map;
                                String type = mr.getType();
                                if (type.equals(JGrassConstants.GRASSBINARYRASTERMAP)) {
                                    String[] mapsetpathAndMapname = JGrassCatalogUtilities
                                            .getMapsetpathAndMapnameFromJGrassMapGeoResource(mr);
                                    String oldMapName = mapsetpathAndMapname[1];
                                    String mapsetName = mapsetpathAndMapname[0];
                                    try {
                                        InputDialog iDialog = new InputDialog(
                                                Display.getDefault().getActiveShell(),
                                                "New map name",
                                                "Please enter the new name for the map: "
                                                        + oldMapName,
                                                "new_" + oldMapName, null);
                                        iDialog.open();
                                        String newMapName = iDialog.getValue();
                                        if (newMapName.indexOf(' ') != -1) {
                                            MessageBox msgBox = new MessageBox(
                                                    Display.getDefault().getActiveShell(),
                                                    SWT.ICON_ERROR);
                                            msgBox.setMessage(
                                                    "Map names can't contain spaces. Please choose a name without spaces.");
                                            msgBox.open();
                                            newMapName = null;
                                            return;
                                        }

                                        if (newMapName != null && newMapName.length() > 0) {
                                            // remove map from layer view
                                            IMap activeMap = ApplicationGIS.getActiveMap();
                                            List<ILayer> mapLayers = activeMap.getMapLayers();
                                            List<ILayer> toRemove = new ArrayList<>();
                                            for (int i = 0; i < mapLayers.size(); i++) {
                                                String layerName = mapLayers.get(i).getName();
                                                if (layerName.equals(oldMapName)) {
                                                    // remove it from layer list
                                                    toRemove.add(mapLayers.get(i));
                                                }

                                            }
                                            if (!toRemove.isEmpty())
                                                activeMap.sendCommandSync(
                                                        new DeleteLayersCommand(toRemove.toArray(
                                                                new ILayer[toRemove.size()])));

                                            // rename the map
                                            renameGrassRasterMap(mapsetName, oldMapName,
                                                    newMapName);
                                            // remove old map
                                            ((JGrassMapsetGeoResource) mr
                                                    .parent(new NullProgressMonitor())).removeMap(
                                                            oldMapName,
                                                            JGrassConstants.GRASSBINARYRASTERMAP);
                                            // add new name map
                                            ((JGrassMapsetGeoResource) mr
                                                    .parent(new NullProgressMonitor())).addMap(
                                                            newMapName,
                                                            JGrassConstants.GRASSBINARYRASTERMAP);
                                        }

                                    } catch (Exception e) {
                                        MessageDialog.openInformation(shell, "Information",
                                                "Problems occurred while renaming the map.");
                                        e.printStackTrace();
                                    }
                                }

                            }
                        } finally {
                            pm.done();
                        }

                    }
                });

            }
        };

        PlatformGIS.runInProgressDialog("Remove maps...", true, operation, true);
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection)
            this.selection = (IStructuredSelection) selection;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void init(IWorkbenchWindow window) {
        // do nothing
    }

    @Override
    public Menu getMenu(Control parent) {
        return null;
    }

    /**
     * Given the mapsetpath and the old and new mapname, the map is renamed with all its accessor
     * files
     *
     * @param mapsetPath the mapset path.
     * @param oldMapName the old map name.
     * @param newMapName the new map name.
     * @throws IOException
     */
    private void renameGrassRasterMap(String mapsetPath, String oldMapName, String newMapName)
            throws IOException {
        // list of files to remove
        String mappaths[] = JGrassCatalogUtilities.filesOfRasterMap(mapsetPath, oldMapName);

        // first delete the list above, which are just files
        for (int j = 0; j < mappaths.length; j++) {
            File filetorename = new File(mappaths[j]);
            if (filetorename.exists()) {
                File parentFile = filetorename.getParentFile();
                File renamedFile = new File(parentFile, newMapName);

                if (filetorename.isDirectory()) {
                    FileUtils.moveDirectory(filetorename, renamedFile);
                } else if (filetorename.isFile()) {
                    FileUtils.moveFile(filetorename, renamedFile);
                } else {
                    throw new IOException("File type not defined");
                }

            }
        }
    }

}
