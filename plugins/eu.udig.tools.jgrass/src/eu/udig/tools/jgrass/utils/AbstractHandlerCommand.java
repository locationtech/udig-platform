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
package eu.udig.tools.jgrass.utils;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * Utility handler for launching IOps from commands.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public abstract class AbstractHandlerCommand extends AbstractHandler {

    public abstract Object execute( ExecutionEvent event ) throws ExecutionException;

    protected void runOp( final IOp op, Class< ? > checkClass ) throws Exception {
        final ILayer selectedLayer = ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer();
        if (selectedLayer == null) {
            // Display.getDefault().syncExec(new Runnable(){
            // public void run() {
            Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
            MessageDialog.openWarning(shell, "WARNING", "No layer selected");
            // }
            // });
            return;
        }

        if (checkClass != null) {
            Object resource = selectedLayer.getResource(checkClass, new NullProgressMonitor());
            if (resource == null) {
                // Display.getDefault().syncExec(new Runnable(){
                // public void run() {
                Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                MessageDialog.openWarning(shell, "WARNING", "The launched operation is not applicable on the selected layer.");
                // }
                // });
                return;
            }
        }

        IWorkbench wb = PlatformUI.getWorkbench();
        final IProgressService ps = wb.getProgressService();
        ps.busyCursorWhile(new IRunnableWithProgress(){
            public void run( IProgressMonitor pm ) {
                try {
                    op.op(Display.getDefault(), selectedLayer, pm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
