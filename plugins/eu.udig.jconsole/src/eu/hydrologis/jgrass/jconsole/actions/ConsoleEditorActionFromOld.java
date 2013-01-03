/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
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
package eu.hydrologis.jgrass.jconsole.actions;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import eu.hydrologis.jgrass.jconsole.JConsolePlugin;
import eu.hydrologis.jgrass.jconsole.JavaEditor;
import eu.hydrologis.jgrass.jconsole.JavaFileEditorInput;
import eu.hydrologis.jgrass.jconsole.jgrasstools.JGrassTools;

/**
 * Action to open an editor
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class ConsoleEditorActionFromOld implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    public void dispose() {
    }

    public void init( IWorkbenchWindow window ) {
        this.window = window;
    }

    public void run( IAction action ) {
        JGrassTools.firstModulesGathering();

        try {

            File lastOpenFolder = JConsolePlugin.getDefault().getLastOpenFolder();
            FileDialog fileDialog = new FileDialog(window.getShell(), SWT.OPEN | SWT.MULTI);
            fileDialog.setFilterExtensions(new String[]{"*.jgrass"});
            fileDialog.setFilterPath(lastOpenFolder.getAbsolutePath());
            String path = fileDialog.open();
            String[] fileNames = fileDialog.getFileNames();
            if (path == null || path.length() < 1) {
                return;
            }
            File f = new File(path);
            if (!f.exists()) {
                return;
            }

            File parentFolder = f.getParentFile();
            JConsolePlugin.getDefault().setLastOpenFolder(parentFolder.getAbsolutePath());

            for( String fileName : fileNames ) {
                File tmpFile = new File(parentFolder, fileName);
                JavaFileEditorInput jFile = new JavaFileEditorInput(tmpFile);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(jFile, JavaEditor.ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

}
