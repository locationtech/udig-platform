/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.workspacecreation;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.gce.grassraster.JGrassRegion;

import org.locationtech.udig.catalog.jgrass.JGrassPlugin;
import org.locationtech.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class NewJGrassLocationWizard extends Wizard implements INewWizard {

    public boolean canFinish = false;
    private WorkspaceProperties props;

    public NewJGrassLocationWizard() {
        super();
        // setNeedsProgressMonitor(true);
        setWindowTitle("Creation of a new JGrass location");
        setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                JGrassPlugin.PLUGIN_ID, "icons/simulwizard.png"));
    }

    @Override
    public void addPages() {
        props = new WorkspaceProperties();
        W01CreateLocationWizardPage page1 = new W01CreateLocationWizardPage(props);
        addPage(page1);
        W02CreateLocationWizardPage page2 = new W02CreateLocationWizardPage(props);
        addPage(page2);
        W03CreateLocationWizardPage page3 = new W03CreateLocationWizardPage(props);
        addPage(page3);
    }

    @Override
    public boolean performFinish() {
        // TODO create a nice workspace with the properties
        for( String m : props.mapsets ) {
            System.out.println(m);
        }
        System.out.println(props.locationPath);
        System.out.println(props.north);
        System.out.println(props.xres);
        System.out.println(props.crs.getName().toString());

        try {
            JGrassRegion window = new JGrassRegion(props.west, props.east, props.south,
                    props.north, props.xres, props.yres);
            JGrassCatalogUtilities.createLocation(props.locationPath, props.crs, window);
            for( String mapset : props.mapsets ) {
                JGrassCatalogUtilities.createMapset(props.locationPath, mapset, null, null);
                // set the WIND file
                String mapsetPath = props.locationPath + File.separator + mapset;
                JGrassRegion.writeWINDToMapset(mapsetPath, window);
            }

            JGrassCatalogUtilities.addServiceToCatalog(props.locationPath + File.separator
                    + JGrassCatalogUtilities.JGRASS_WORKSPACE_FILENAME, new NullProgressMonitor());
        } catch (IOException e) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.workspacecreation.wizard#NewJGrassLocationWizard#performFinish", e); //$NON-NLS-1$
            e.printStackTrace();
        }

        return true;
    }
    @Override
    public boolean canFinish() {
        return canFinish;
    }

    public void init( IWorkbench workbench, IStructuredSelection selection ) {
    }

}
