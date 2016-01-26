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

import java.awt.geom.AffineTransform;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Control;
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
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.gce.grassraster.format.GrassCoverageFormatFactory;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.parameter.Parameter;
import org.geotools.referencing.operation.matrix.XAffineTransform;
import org.locationtech.udig.catalog.jgrass.JGrassPlugin;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;
import org.locationtech.udig.catalog.rasterings.AbstractRasterGeoResource;
import org.locationtech.udig.ui.ExceptionDetailsDialog;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Action map summary.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class MapSummaryAction implements IObjectActionDelegate, IWorkbenchWindowActionDelegate, IWorkbenchWindowPulldownDelegate {

    IStructuredSelection selection = null;

    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
    }

    public void run( IAction action ) {

        IRunnableWithProgress operation = new IRunnableWithProgress(){
            public void run( final IProgressMonitor pm ) throws InvocationTargetException, InterruptedException {
                Display.getDefault().syncExec(new Runnable(){
                    public void run() {
                        final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

                        final Object object = selection.getFirstElement();
                        try {
                            pm.beginTask("Collecting stats...", IProgressMonitor.UNKNOWN);
                            StringBuilder regionString = new StringBuilder();
                            GridCoverage2D geodata = null;
                            if (object instanceof AbstractRasterGeoResource) {
                                AbstractRasterGeoResource rGeo = (AbstractRasterGeoResource) object;
                                AbstractGridCoverage2DReader gridCoverage2DReader = rGeo.resolve(
                                        AbstractGridCoverage2DReader.class, pm);
                                geodata = ((GridCoverage2D) gridCoverage2DReader.read(null));

                                Envelope envelope = geodata.getEnvelope();
                                DirectPosition lowerCorner = envelope.getLowerCorner();
                                double[] westSouth = lowerCorner.getCoordinate();
                                DirectPosition upperCorner = envelope.getUpperCorner();
                                double[] eastNorth = upperCorner.getCoordinate();
                                GridGeometry2D gridGeometry = geodata.getGridGeometry();
                                GridEnvelope2D gridRange = gridGeometry.getGridRange2D();
                                int rows = gridRange.height;
                                int cols = gridRange.width;

                                AffineTransform gridToCRS = (AffineTransform) gridGeometry.getGridToCRS();
                                double we_res = XAffineTransform.getScaleX0(gridToCRS);
                                double ns_res = XAffineTransform.getScaleY0(gridToCRS);
                                double north = eastNorth[1];
                                double south = westSouth[1];
                                double east = eastNorth[0];
                                double west = westSouth[0];
                                regionString.append("region:\nwest=");
                                regionString.append(west);
                                regionString.append("\neast=");
                                regionString.append(east);
                                regionString.append("\nsouth=");
                                regionString.append(south);
                                regionString.append("\nnorth=");
                                regionString.append(north);
                                regionString.append("\nwe_res=");
                                regionString.append(we_res);
                                regionString.append("\nns_res=");
                                regionString.append(ns_res);
                                regionString.append("\nrows=");
                                regionString.append(rows);
                                regionString.append("\ncols=");
                                regionString.append(cols);

                            } else if (object instanceof JGrassMapGeoResource) {
                                JGrassMapGeoResource mr = (JGrassMapGeoResource) object;
                                File mapFile = mr.getMapFile();
                                JGrassMapEnvironment mapEnvironment = new JGrassMapEnvironment(mapFile);
                                JGrassRegion jGrassRegion = mapEnvironment.getActiveRegion();

                                int cols = jGrassRegion.getCols();
                                int rows = jGrassRegion.getRows();
                                double north = jGrassRegion.getNorth();
                                double south = jGrassRegion.getSouth();
                                double east = jGrassRegion.getEast();
                                double west = jGrassRegion.getWest();
                                double we_res = jGrassRegion.getWEResolution();
                                double ns_res = jGrassRegion.getNSResolution();
                                regionString.append("region:\nwest=");
                                regionString.append(west);
                                regionString.append("\neast=");
                                regionString.append(east);
                                regionString.append("\nsouth=");
                                regionString.append(south);
                                regionString.append("\nnorth=");
                                regionString.append(north);
                                regionString.append("\nwe_res=");
                                regionString.append(we_res);
                                regionString.append("\nns_res=");
                                regionString.append(ns_res);
                                regionString.append("\nrows=");
                                regionString.append(rows);
                                regionString.append("\ncols=");
                                regionString.append(cols);

                                GeneralParameterValue[] readParams = createGridGeometryGeneralParameter(cols, rows, north, south,
                                        east, west, mapEnvironment.getCoordinateReferenceSystem());
                                AbstractGridFormat format = (AbstractGridFormat) new GrassCoverageFormatFactory().createFormat();
                                AbstractGridCoverage2DReader reader = format.getReader(mapEnvironment.getCELL());
                                geodata = ((GridCoverage2D) reader.read(readParams));
                            } else {
                                MessageDialog.openInformation(shell, "WARNING", "Unable to read format");
                                return;
                            }

                            GridGeometry2D gridGeometry = geodata.getGridGeometry();
                            GridEnvelope2D gridRange = gridGeometry.getGridRange2D();
                            int rows = gridRange.height;
                            int cols = gridRange.width;
                            AffineTransform gridToCRS = (AffineTransform) gridGeometry.getGridToCRS();
                            double xRes = XAffineTransform.getScaleX0(gridToCRS);
                            double yRes = XAffineTransform.getScaleY0(gridToCRS);

                            RandomIter inputIter = RandomIterFactory.create(geodata.getRenderedImage(), null);
                            /*
                             * calculate mean, max and min
                             */
                            double mean = 0.0;

                            double[] minMaxMeans = new double[]{Double.MAX_VALUE, Double.MIN_VALUE, 0, 0, 0};
                            int validCells = 0;
                            for( int y = 0; y < rows; y++ ) {
                                for( int x = 0; x < cols; x++ ) {
                                    double value = inputIter.getSampleDouble(x, y, 0);
                                    if (!Double.isNaN(value) && (int) value != -9999) {
                                        if (value < minMaxMeans[0])
                                            minMaxMeans[0] = value;
                                        if (value > minMaxMeans[1])
                                            minMaxMeans[1] = value;
                                        mean = mean + value;
                                        validCells++;
                                    }
                                }
                            }

                            mean = mean / (double) validCells;

                            minMaxMeans[2] = mean;
                            minMaxMeans[3] = validCells;
                            minMaxMeans[4] = validCells * xRes * yRes;

                            /*
                             * print out some system out
                             */
                            StringBuilder sb = new StringBuilder();
                            sb.append("Summary for the map:\n");
                            sb.append("\n");
                            sb.append("range: " + minMaxMeans[0] + " - " + minMaxMeans[1] + "\n");
                            sb.append("mean: " + minMaxMeans[2] + "\n");
                            sb.append("active cells: " + minMaxMeans[3] + "\n");
                            sb.append("active area (assuming metric resolution): " + minMaxMeans[4] + "\n");
                            sb.append(regionString.toString() + "\n");
                            sb.append("data crs: " + geodata.getCoordinateReferenceSystem().getName().toString());

                            MessageDialog.openInformation(shell, "Summary", sb.toString());

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

        PlatformGIS.runInProgressDialog("Calculating map summery...", true, operation, true);

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
