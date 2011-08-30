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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.program.Program;
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

import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import eu.udig.catalog.jgrass.core.JGrassService;

/**
 * Action to open the file browser in the mapset.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class OpenFolderAction implements IObjectActionDelegate, IWorkbenchWindowActionDelegate, IWorkbenchWindowPulldownDelegate {

    IStructuredSelection selection = null;
    private IWorkbenchWindow window;

    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
    }

    public void run( IAction action ) {

        Display.getDefault().syncExec(new Runnable(){
            public void run() {
                List toList = selection.toList();
                List<String> paths = new ArrayList<String>();
                for( Object object : toList ) {
                    if (object instanceof JGrassMapsetGeoResource) {
                        JGrassMapsetGeoResource mapsetGeoresource = (JGrassMapsetGeoResource) object;
                        String mapsetPath = mapsetGeoresource.getFile().getAbsolutePath();
                        if (!Program.launch(mapsetPath)) {
                            paths.add(mapsetPath);
                        }
                        paths.add(mapsetPath);
                    } else if (object instanceof JGrassService) {
                        JGrassService locationService = (JGrassService) object;
                        String locationPath = locationService.getFile().getAbsolutePath();
                        if (!Program.launch(locationPath)) {
                            paths.add(locationPath);
                        }
                        paths.add(locationPath);
                    }
                }

                if (!paths.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("No program to open the following paths could be found:\n");
                    for( String path : paths ) {
                        sb.append(path).append("\n");
                    }

                    Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                    MessageDialog.openWarning(shell, "WARNING", sb.toString());
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