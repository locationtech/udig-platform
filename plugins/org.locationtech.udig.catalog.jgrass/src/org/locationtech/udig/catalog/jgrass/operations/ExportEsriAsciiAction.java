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
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.gce.arcgrid.ArcGridFormat;
import org.geotools.gce.arcgrid.ArcGridWriteParams;
import org.geotools.gce.arcgrid.ArcGridWriter;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.gce.grassraster.format.GrassCoverageFormatFactory;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.parameter.Parameter;
import org.locationtech.udig.catalog.jgrass.JGrassPlugin;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;
import org.locationtech.udig.ui.ExceptionDetailsDialog;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Action export a map to esri ascii.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ExportEsriAsciiAction
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

                        final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

                        DirectoryDialog folderDialog = new DirectoryDialog(shell, SWT.OPEN);
                        String selpath = folderDialog.open();
                        if (selpath == null) {
                            return;
                        }
                        final File folderFile = new File(selpath);
                        if (!folderFile.exists()) {
                            return;
                        }

                        final List toList = selection.toList();
                        try {
                            pm.beginTask("Exporting maps...", toList.size());

                            for( Object object : toList ) {
                                if (object instanceof JGrassMapGeoResource) {
                                    JGrassMapGeoResource mr = (JGrassMapGeoResource) object;
                                    File mapFile = mr.getMapFile();
                                    JGrassMapEnvironment mapEnvironment = new JGrassMapEnvironment(mapFile);
                                    JGrassRegion jGrassRegion = mapEnvironment.getActiveRegion();

                                    GeneralParameterValue[] readParams = createGridGeometryGeneralParameter(
                                            jGrassRegion.getCols(), jGrassRegion.getRows(), jGrassRegion.getNorth(),
                                            jGrassRegion.getSouth(), jGrassRegion.getEast(), jGrassRegion.getWest(),
                                            mapEnvironment.getCoordinateReferenceSystem());

                                    AbstractGridFormat format = (AbstractGridFormat) new GrassCoverageFormatFactory()
                                            .createFormat();
                                    GridCoverageReader reader = format.getReader(mapEnvironment.getCELL());
                                    GridCoverage2D geodata = ((GridCoverage2D) reader.read(readParams));

                                    final ArcGridFormat format1 = new ArcGridFormat();
                                    final ArcGridWriteParams wp = new ArcGridWriteParams();
                                    final ParameterValueGroup paramWrite = format1.getWriteParameters();
                                    paramWrite.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString()).setValue(
                                            wp);
                                    ArcGridWriter gtw = (ArcGridWriter) format1.getWriter(new File(folderFile, mapFile.getName()
                                            + ".asc"));
                                    gtw.write(geodata,
                                            (GeneralParameterValue[]) paramWrite.values().toArray(new GeneralParameterValue[1]));
                                    gtw.dispose();
                                }
                                pm.worked(1);
                            }
                        } catch (Exception e) {
                            String message = "An error occurred while exporting the maps.";
                            ExceptionDetailsDialog.openError("ERROR", message, IStatus.ERROR, JGrassPlugin.PLUGIN_ID, e);
                        } finally {
                            pm.done();
                        }

                    }
                });
            }
        };

        PlatformGIS.runInProgressDialog("Export maps...", true, operation, true);

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
     * Utility method to create read parameters for {@link GridCoverageReader} 
     * 
     * @param width the needed number of columns.
     * @param height the needed number of columns.
     * @param north the northern boundary.
     * @param south the southern boundary.
     * @param east the eastern boundary.
     * @param west the western boundary.
     * @param crs the {@link CoordinateReferenceSystem}. Can be null, even if it should not.
     * @return the {@link GeneralParameterValue array of parameters}.
     */
    public static GeneralParameterValue[] createGridGeometryGeneralParameter( int width, int height, double north, double south,
            double east, double west, CoordinateReferenceSystem crs ) {
        GeneralParameterValue[] readParams = new GeneralParameterValue[1];
        Parameter<GridGeometry2D> readGG = new Parameter<GridGeometry2D>(AbstractGridFormat.READ_GRIDGEOMETRY2D);
        GridEnvelope2D gridEnvelope = new GridEnvelope2D(0, 0, width, height);
        Envelope env;
        if (crs != null) {
            env = new ReferencedEnvelope(west, east, south, north, crs);
        } else {
            DirectPosition2D minDp = new DirectPosition2D(west, south);
            DirectPosition2D maxDp = new DirectPosition2D(east, north);
            env = new Envelope2D(minDp, maxDp);
        }
        readGG.setValue(new GridGeometry2D(gridEnvelope, env));
        readParams[0] = readGG;

        return readParams;
    }

}
