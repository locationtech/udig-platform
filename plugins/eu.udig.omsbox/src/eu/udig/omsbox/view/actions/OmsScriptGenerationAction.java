/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
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
