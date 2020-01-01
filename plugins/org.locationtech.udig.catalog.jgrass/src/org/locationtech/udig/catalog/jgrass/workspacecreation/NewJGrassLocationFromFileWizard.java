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
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.grassraster.GrassCoverageWriter;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.gce.grassraster.format.GrassCoverageFormat;
import org.geotools.gce.grassraster.format.GrassCoverageFormatFactory;
import org.locationtech.udig.catalog.jgrass.JGrassPlugin;
import org.locationtech.udig.catalog.jgrass.utils.JGrassCatalogUtilities;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class NewJGrassLocationFromFileWizard extends Wizard implements INewWizard {

    public boolean canFinish = false;
    private WorkspaceProperties props;

    public NewJGrassLocationFromFileWizard() {
        super();
        // setNeedsProgressMonitor(true);
        setWindowTitle("Creation of a new JGrass location");
        setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/simulwizard.png"));
    }

    @Override
    public void addPages() {
        props = new WorkspaceProperties();
        props.locationName = "newLocation";
        props.mapsetName = "newMapset";
        W11CreateLocationFromFileWizardPage page1 = new W11CreateLocationFromFileWizardPage(props);
        addPage(page1);
    }

    @Override
    public boolean performFinish() {
        try {
            String importFilePath = props.importFilePath;
            File mapFile = new File(importFilePath);
            CoordinateReferenceSystem fileCrs = null;
            GridCoverage2D geodata = null;
            if (mapFile.getName().endsWith(".asc")) {
                ArcGridReader arcGridReader = new ArcGridReader(mapFile);
                geodata = arcGridReader.read(null);
                geodata = JGrassCatalogUtilities.removeNovalues(geodata);
                fileCrs = arcGridReader.getCoordinateReferenceSystem();
            } else if (mapFile.getName().endsWith(".tif") || mapFile.getName().endsWith(".tiff")) {
                GeoTiffReader geotiffGridReader = new GeoTiffReader(mapFile);
                geodata = geotiffGridReader.read(null);
                geodata = JGrassCatalogUtilities.removeNovalues(geodata);
                fileCrs = geotiffGridReader.getCoordinateReferenceSystem();
            }

            HashMap<String, Double> regionParams = JGrassCatalogUtilities.getRegionParamsFromGridCoverage(geodata);
            double n = regionParams.get(JGrassCatalogUtilities.NORTH);
            double s = regionParams.get(JGrassCatalogUtilities.SOUTH);
            double e = regionParams.get(JGrassCatalogUtilities.EAST);
            double w = regionParams.get(JGrassCatalogUtilities.WEST);
            double xres = regionParams.get(JGrassCatalogUtilities.XRES);
            double yres = regionParams.get(JGrassCatalogUtilities.YRES);

            // create the location and mapset
            JGrassRegion window = new JGrassRegion(w, e, s, n, xres, yres);
            File baseFolderFile = new File(props.basePath);
            File locationFile = new File(baseFolderFile, props.locationName);
            File mapsetFile = new File(locationFile, props.mapsetName);
            String locationPath = locationFile.getAbsolutePath();
            JGrassCatalogUtilities.createLocation(locationPath, fileCrs, window);
            JGrassCatalogUtilities.createMapset(locationPath, mapsetFile.getName(), null, null);
            JGrassRegion.writeWINDToMapset(mapsetFile.getAbsolutePath(), window);

            // write the map into it
            String mapName = mapFile.getName();
            mapName = FilenameUtils.getBaseName(mapName);
            JGrassMapEnvironment mapEnvironment = new JGrassMapEnvironment(mapsetFile, mapName);
            GrassCoverageFormat format = new GrassCoverageFormatFactory().createFormat();
            GrassCoverageWriter writer = format.getWriter(mapEnvironment.getCELL(), null);
            GeneralParameterValue[] readParams = null;
            writer.write(geodata, readParams);

            JGrassCatalogUtilities.addServiceToCatalog(locationPath + File.separator
                    + JGrassCatalogUtilities.JGRASS_WORKSPACE_FILENAME, new NullProgressMonitor());
        } catch (IOException e) {
            JGrassPlugin
                    .log("JGrassPlugin problem: eu.hydrologis.udig.catalog.workspacecreation.wizard#NewJGrassLocationWizard#performFinish", e); //$NON-NLS-1$
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
