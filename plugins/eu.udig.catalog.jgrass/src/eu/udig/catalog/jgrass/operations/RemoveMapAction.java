/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.catalog.jgrass.operations;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.refractions.udig.ui.PlatformGIS;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.geotools.gce.grassraster.JGrassConstants;

import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * Action to remove a map from disk.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class RemoveMapAction implements IObjectActionDelegate, IWorkbenchWindowActionDelegate, IWorkbenchWindowPulldownDelegate {

    IStructuredSelection selection = null;

    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
    }

    public void run( IAction action ) {

        Display.getDefault().syncExec(new Runnable(){
            public void run() {

                final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                boolean answer = MessageDialog.openQuestion(shell, "WARNING",
                        "Are you sure you want to remove the selected maps from disk? This can't be undone!");
                if (answer) {
                    final List toList = selection.toList();

                    IRunnableWithProgress operation = new IRunnableWithProgress(){

                        public void run( IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {
                            try {
                                pm.beginTask("Removing maps...", toList.size());

                                for( Object object : toList ) {
                                    if (object instanceof JGrassMapGeoResource) {
                                        JGrassMapGeoResource mr = (JGrassMapGeoResource) object;
                                        String type = mr.getType();
                                        if (type.equals(JGrassConstants.GRASSBINARYRASTERMAP)) {
                                            String[] mapsetpathAndMapname = JGrassCatalogUtilities
                                                    .getMapsetpathAndMapnameFromJGrassMapGeoResource(mr);
                                            try {
                                                removeGrassRasterMap(mapsetpathAndMapname[0], mapsetpathAndMapname[1]);
                                                ((JGrassMapsetGeoResource) mr.parent(new NullProgressMonitor())).removeMap(
                                                        mapsetpathAndMapname[1], JGrassConstants.GRASSBINARYRASTERMAP);
                                            } catch (Exception e) {
                                                MessageDialog.openInformation(shell, "Information",
                                                        "Problems occurred while removing the map.");
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    pm.worked(1);
                                }
                            } finally {
                                pm.done();
                            }
                        }

                    };

                    PlatformGIS.runInProgressDialog("Remove maps...", true, operation, true);

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
        // do nothing
    }

    /*
    * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
    */
    public Menu getMenu( Control parent ) {
        return null;
    }

    /**
     * Given the mapsetpath and the mapname, the map is removed with all its accessor files
     * 
     * @param mapsetPath
     * @param mapName
     * @throws IOException 
     */
    public void removeGrassRasterMap( String mapsetPath, String mapName ) throws IOException {
        // list of files to remove
        String mappaths[] = filesOfRasterMap(mapsetPath, mapName);

        // first delete the list above, which are just files
        for( int j = 0; j < mappaths.length; j++ ) {
            File filetoremove = new File(mappaths[j]);
            if (filetoremove.exists()) {
                FileUtils.forceDelete(filetoremove);
            }
        }
    }

    /**
     * Returns the list of files involved in the raster map issues. If for example a map has to be
     * deleted, then all these files have to.
     * 
     * @param mapsetPath - the path of the mapset
     * @param mapname -the name of the map
     * @return the array of strings containing the full path to the involved files
     */
    public String[] filesOfRasterMap( String mapsetPath, String mapname ) {
        String filesOfRaster[] = new String[]{
                mapsetPath + File.separator + JGrassConstants.FCELL + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELL + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CATS + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.HIST + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELLHD + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.COLR + File.separator + mapname,
                // it is very important that the folder cell_misc/mapname comes
                // before the files in it
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_FORMAT,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_QUANT,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_RANGE,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_NULL};
        return filesOfRaster;
    }

}