/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.view.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import org.locationtech.udig.omsbox.core.OmsModulesManager;
import org.locationtech.udig.omsbox.ui.SettingsDialog;
import org.locationtech.udig.omsbox.view.OmsBoxView;

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
                    pm.beginTask(OmsBoxView.LOADING_MODULES_FROM_LIBRARIES, IProgressMonitor.UNKNOWN);
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
            PlatformGIS.runInProgressDialog(OmsBoxView.SPATIAL_TOOLBOX, true, operation, true);

        }
    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

}
