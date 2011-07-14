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

import java.io.File;

import net.refractions.udig.ui.ExceptionDetailsDialog;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.joda.time.DateTime;

import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.utils.OmsBoxConstants;
import eu.udig.omsbox.view.OmsBoxView;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class OmsScriptGenerationAction implements IViewActionDelegate {

    private IViewPart view;

    public void init( IViewPart view ) {
        this.view = view;
    }

    public void run( IAction action ) {
        if (view instanceof OmsBoxView) {
            OmsBoxView omsView = (OmsBoxView) view;
            try {
                String script = omsView.generateScriptForSelectedModule();
                if (script == null) {
                    return;
                }
                Program program = Program.findProgram(".txt");
                if (program != null) {
                    File tempFile = File.createTempFile("omsbox_", ".oms");
                    if (tempFile == null || !tempFile.exists() || tempFile.getAbsolutePath() == null) {
                        // try with user's home folder
                        String ts = new DateTime().toString(OmsBoxConstants.dateTimeFormatterYYYYMMDDHHMMSS);
                        String userHomePath = System.getProperty("user.home"); //$NON-NLS-1$

                        File userHomeFile = new File(userHomePath);
                        if (!userHomeFile.exists()) {
                            String message = "Unable to create the oms script both in the temp folder and user home. Check your permissions.";
                            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, OmsBoxPlugin.PLUGIN_ID,
                                    new RuntimeException());
                            return;
                        }
                        tempFile = new File(userHomeFile, "omsbox_" + ts + ".oms");
                    }
                    FileUtils.writeStringToFile(tempFile, script);

                    program.execute(tempFile.getAbsolutePath());

                    // cleanup when leaving uDig
                    // tempFile.deleteOnExit();
                } else {
                    // make it the good old way prompting
                    FileDialog fileDialog = new FileDialog(view.getSite().getShell(), SWT.SAVE);
                    String path = fileDialog.open();
                    if (path == null || path.length() < 1) {
                        return;
                    }
                    FileUtils.writeStringToFile(new File(path), script);
                }

            } catch (Exception e) {
                e.printStackTrace();
                String message = "An error ocurred while generating the script.";
                ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, OmsBoxPlugin.PLUGIN_ID, e);
            }

        }
    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

}
