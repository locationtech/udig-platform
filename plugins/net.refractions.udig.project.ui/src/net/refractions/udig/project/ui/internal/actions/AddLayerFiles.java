/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.project.ui.internal.actions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ui.internal.MapFactory;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.emf.common.ui.action.WorkbenchWindowActionDelegate;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

/**
 * Performs the open action from the file menu of uDig. It is responseible for creating new maps
 * from selected resources.
 * 
 * @author rgould
 * @since 0.6.0
 */
public class AddLayerFiles extends WorkbenchWindowActionDelegate {

    public static final String ID = "net.refractions.udig.project.ui.openFilesAction"; //$NON-NLS-1$

    /*
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action ) {
        addLayers(false);
    }

    protected void addLayers( boolean forceNewMap ) {
        String lastOpenedDirectory = ProjectUIPlugin.getDefault().getPluginPreferences().getString(
                ProjectUIPlugin.PREF_OPEN_DIALOG_DIRECTORY);
        FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.MULTI);
        fileDialog.setFilterExtensions(new String[]{"*.shp", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
        if (lastOpenedDirectory != null) {
            fileDialog.setFilterPath(lastOpenedDirectory);
        }
        String result = fileDialog.open();
        if (result == null) {
            return;
        }
        String path = fileDialog.getFilterPath();
        ProjectUIPlugin.getDefault().getPluginPreferences().setValue(
                ProjectUIPlugin.PREF_OPEN_DIALOG_DIRECTORY, path);
        ProjectUIPlugin.getDefault().savePluginPreferences();
        String[] filenames = fileDialog.getFileNames();
        List<URL> urls = new ArrayList<URL>();
        for( int i = 0; i < filenames.length; i++ ) {
            try {
                URL url = new File(path + System.getProperty("file.separator") + filenames[i]).toURL(); //$NON-NLS-1$
                urls.add(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        MapFactory.instance().processURLs(urls, null, forceNewMap);
    }
}
