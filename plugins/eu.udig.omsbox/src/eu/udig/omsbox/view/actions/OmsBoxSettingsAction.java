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
package eu.udig.omsbox.view.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import eu.udig.omsbox.core.OmsModulesManager;
import eu.udig.omsbox.ui.SettingsDialog;
import eu.udig.omsbox.view.OmsBoxView;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class OmsBoxSettingsAction implements IViewActionDelegate {

    private IViewPart view;

    public void init( IViewPart view ) {
        this.view = view;
    }

    public void run( IAction action ) {
        if (view instanceof OmsBoxView) {

            final OmsBoxView dbView = (OmsBoxView) view;

            Shell shell = dbView.getSite().getShell();

            SettingsDialog dialog = new SettingsDialog();
            dialog.open(shell, SWT.MULTI);

            if (dialog.isCancelPressed()) {
                return;
            }

            final List<String> resources = dialog.getSelectedResources();
            IRunnableWithProgress operation = new IRunnableWithProgress(){
                public void run( IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {
                    pm.beginTask("Loading modules from libraries...", IProgressMonitor.UNKNOWN);
                    try {

                        OmsModulesManager manager = OmsModulesManager.getInstance();
                        manager.clearJars();
                        for( String resource : resources ) {
                            manager.addJar(resource);
                        }
                        manager.browseModules(true);

                        dbView.relayout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        pm.done();
                    }
                }
            };
            PlatformGIS.runInProgressDialog("Load modules", true, operation, true);

        }
    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

}
