/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.operations;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.ExceptionDetailsDialog;
import org.locationtech.udig.ui.PlatformGIS;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.geotools.gce.grassraster.JGrassConstants;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

import org.locationtech.udig.catalog.jgrass.JGrassPlugin;
import org.locationtech.udig.catalog.jgrass.activeregion.ActiveRegionStyle;
import org.locationtech.udig.catalog.jgrass.activeregion.ActiveregionStyleContent;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;

/**
 * Action to set the active region to selected maps.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class SetActiveRegionToMapsAction
        implements
            IObjectActionDelegate,
            IWorkbenchWindowActionDelegate,
            IWorkbenchWindowPulldownDelegate {

    IStructuredSelection selection = null;

    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
    }

    public void run( IAction action ) {

        IRunnableWithProgress operation = new IRunnableWithProgress(){

            public void run( final IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {
                Display.getDefault().syncExec(new Runnable(){
                    public void run() {

                        final List< ? > toList = selection.toList();
                        Envelope bounds = null;
                        try {
                            pm.beginTask("Set active region to maps bounds...", toList.size());

                            try {
                                JGrassRegion currentRegion = null;
                                JGrassMapEnvironment grassMapEnvironment = null;

                                for( Object object : toList ) {
                                    if (object instanceof JGrassMapGeoResource) {
                                        JGrassMapGeoResource mr = (JGrassMapGeoResource) object;
                                        JGrassRegion fileWindow = mr.getFileWindow();
                                        if (currentRegion == null) {
                                            currentRegion = mr.getActiveWindow();
                                            grassMapEnvironment = mr.getjGrassMapEnvironment();
                                        }

                                        Envelope envelope = fileWindow.getEnvelope();
                                        if (bounds == null) {
                                            bounds = envelope;
                                        } else {
                                            bounds.expandToInclude(envelope);
                                        }

                                    }
                                    pm.worked(1);
                                }

                                String code = null;
                                try {
                                    CoordinateReferenceSystem jGrassCrs = grassMapEnvironment.getCoordinateReferenceSystem();
                                    try {
                                        Integer epsg = CRS.lookupEpsgCode(jGrassCrs, true);
                                        code = "EPSG:" + epsg;
                                    } catch (Exception e) {
                                        // try non epsg
                                        code = CRS.lookupIdentifier(jGrassCrs, true);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                JGrassRegion newActiveRegion = JGrassRegion.adaptActiveRegionToEnvelope(bounds, currentRegion);
                                File windFile = grassMapEnvironment.getWIND();
                                JGrassRegion.writeWINDToMapset(windFile.getParent(), newActiveRegion);

                                IMap activeMap = ApplicationGIS.getActiveMap();
                                IBlackboard blackboard = activeMap.getBlackboard();
                                ActiveRegionStyle style = (ActiveRegionStyle) blackboard.get(ActiveregionStyleContent.ID);
                                if (style == null) {
                                    style = ActiveregionStyleContent.createDefault();
                                }
                                style.north = (float) newActiveRegion.getNorth();
                                style.south = (float) newActiveRegion.getSouth();
                                style.east = (float) newActiveRegion.getEast();
                                style.west = (float) newActiveRegion.getWest();
                                style.rows = newActiveRegion.getRows();
                                style.cols = newActiveRegion.getCols();
                                style.windPath = windFile.getAbsolutePath();
                                style.crsString = code;

                                blackboard.put(ActiveregionStyleContent.ID, style);

                                ILayer activeRegionMapGraphic = JGrassPlugin.getDefault().getActiveRegionMapGraphic();
                                activeRegionMapGraphic.refresh(null);

                            } catch (IOException e) {
                                e.printStackTrace();
                                String message = "Problems occurred while setting the new active region.";
                                ExceptionDetailsDialog
                                        .openError("Information", message, IStatus.ERROR, JGrassPlugin.PLUGIN_ID, e);
                            }
                        } finally {
                            pm.done();
                        }

                    }
                });

            }
        };

        PlatformGIS.runInProgressDialog("Set active region...", true, operation, true);
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
        // do nothing
    }

    /*
    * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
    */
    public Menu getMenu( Control parent ) {
        return null;
    }

    /**
     * Given the mapsetpath and the mapname, the map is removed with all its accessor files
     * 
     * @param mapsetPath
     * @param mapName
     * @throws IOException 
     */
    public void removeGrassRasterMap( String mapsetPath, String mapName ) throws IOException {
        // list of files to remove
        String mappaths[] = filesOfRasterMap(mapsetPath, mapName);

        // first delete the list above, which are just files
        for( int j = 0; j < mappaths.length; j++ ) {
            File filetoremove = new File(mappaths[j]);
            if (filetoremove.exists()) {
                FileUtils.forceDelete(filetoremove);
            }
        }
    }

    /**
     * Returns the list of files involved in the raster map issues. If for example a map has to be
     * deleted, then all these files have to.
     * 
     * @param mapsetPath - the path of the mapset
     * @param mapname -the name of the map
     * @return the array of strings containing the full path to the involved files
     */
    public String[] filesOfRasterMap( String mapsetPath, String mapname ) {
        String filesOfRaster[] = new String[]{
                mapsetPath + File.separator + JGrassConstants.FCELL + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELL + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CATS + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.HIST + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELLHD + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.COLR + File.separator + mapname,
                // it is very important that the folder cell_misc/mapname comes
                // before the files in it
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_FORMAT,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_QUANT,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_RANGE,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_NULL};
        return filesOfRaster;
    }

}
