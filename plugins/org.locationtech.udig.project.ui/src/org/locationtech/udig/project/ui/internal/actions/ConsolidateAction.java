/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.actions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceMover;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.impl.MapImpl;
import org.locationtech.udig.project.ui.UDIGGenericAction;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class ConsolidateAction extends UDIGGenericAction {
    private Project project;
    private File projectDataFolder;

    public ConsolidateAction() {
        super();
    }

    protected void operate( Project project ) {
        this.project = project;

        projectDataFolder = checkProjectEnvironment();

        if (projectDataFolder == null) {
            throw new RuntimeException("Problems consolidating...");
        }

        // get the maps
        List<IProjectElement> projectElements = project.getElements();

        for( IProjectElement projectElement : projectElements ) {
            // get the layers of the map
            List<Layer> layers = ((MapImpl) projectElement).getLayersInternal();

            for( ILayer layer : layers ) {

                IService service = null;
                try {
                    service = layer.getGeoResource().service(null);
                    if (service.canResolve(ServiceMover.class)) {
                        ServiceMover tmp = service.resolve(ServiceMover.class, null);
                        String msg = tmp.move(projectDataFolder);
                        if (msg != null) {

                            MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
                                    .getActiveShell(), "Comsolidation Error", msg);
                        }
                    } else {
                        System.out.println("Not resolved: " + layer.getName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private File checkProjectEnvironment() {
        URI projectUri = project.eResource().getURI();
        String devicePath = projectUri.devicePath();
        String projectFolderFile = new File(devicePath).getParent();
        File dataFolder = new File(projectFolderFile + File.separator + "data");
        if (dataFolder.exists() || dataFolder.mkdirs()) {
            return dataFolder;
        }
        return null;
    }

}
