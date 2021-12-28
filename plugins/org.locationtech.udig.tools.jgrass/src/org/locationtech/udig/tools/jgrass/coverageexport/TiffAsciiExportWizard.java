/**
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.coverageexport;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.arcgrid.ArcGridFormat;
import org.geotools.gce.arcgrid.ArcGridWriteParams;
import org.geotools.gce.arcgrid.ArcGridWriter;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.referencing.CRS;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.tools.jgrass.JGrassToolsPlugin;
import org.locationtech.udig.ui.ExceptionDetailsDialog;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class TiffAsciiExportWizard extends Wizard implements IExportWizard {

    public static boolean canFinish = false;

    private TiffAsciiExportWizardPage mainPage;

    public TiffAsciiExportWizard() {
        super();
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("Coverage export");
        setDefaultPageImageDescriptor(AbstractUIPlugin
                .imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID, "icons/export_wiz.png")); //$NON-NLS-1$
        setNeedsProgressMonitor(true);

        mainPage = new TiffAsciiExportWizardPage();
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

    @Override
    public boolean performFinish() {
        /**
         * Run with backgroundable progress monitoring
         */
        IRunnableWithProgress operation = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor pm)
                    throws InvocationTargetException, InterruptedException {

                IGeoResource geoResource = mainPage.getGeoResource();
                String folderPath = mainPage.getFolderPath();
                String fileName = mainPage.getFileName();
                String newPath = folderPath + File.separator + fileName;
                CoordinateReferenceSystem fileCrs = mainPage.getFileCrs();
                CoordinateReferenceSystem newCrs = mainPage.getNewCrs();
                boolean isAscii = mainPage.isAscii();
                boolean isTiff = mainPage.isTiff();

                /**
                 * finally do some processing
                 */
                pm.beginTask("Exporting map...", IProgressMonitor.UNKNOWN);
                try {
                    // TODO reactivate this as soon as netcdfs are back
                    // if (geoResource.canResolve(NetcdfMapGeoResource.class)) {
                    // NetcdfMapGeoResource netcdfGeoResource =
                    // geoResource.resolve(NetcdfMapGeoResource.class, pm);
                    // List<DateTime> availableTimeSteps =
                    // netcdfGeoResource.getAvailableTimeSteps();
                    // double[] availableElevationLevels =
                    // netcdfGeoResource.getAvailableElevationLevels();
                    //
                    // if (availableTimeSteps.isEmpty()) {
                    // availableTimeSteps = new ArrayList<DateTime>();
                    // // add a dummy one to make sure it enters the loop
                    // availableTimeSteps.add(null);
                    // }
                    // if (availableElevationLevels == null || availableElevationLevels.length == 0)
                    // {
                    // availableElevationLevels = new double[1];
                    // // add a dummy one to make sure it enters the loop
                    // availableElevationLevels[0] = Double.NaN;
                    // }
                    //
                    // for( DateTime dateTime : availableTimeSteps ) {
                    // for( double currentElevationDouble : availableElevationLevels ) {
                    // CoverageReadRequest readRequest = new DefaultCoverageReadRequest();
                    //
                    // if (dateTime != null) {
                    // SortedSet<TemporalGeometricPrimitive> temporalSubset = new
                    // TreeSet<TemporalGeometricPrimitive>();
                    // temporalSubset.add(new DefaultInstant(new
                    // DefaultPosition(dateTime.toDate())));
                    // readRequest.setTemporalSubset(temporalSubset);
                    // }
                    // if (!Double.isNaN(currentElevationDouble)) {
                    // Set<NumberRange<Double>> verticalSubset = new TreeSet<NumberRange<Double>>();
                    // NumberRange<Double> vertical = new NumberRange<Double>(Double.class,
                    // currentElevationDouble,
                    // true, currentElevationDouble, true);
                    // verticalSubset.add(vertical);
                    // readRequest.setVerticalSubset(verticalSubset);
                    // }
                    //
                    // GridCoverage2D coverage2D = netcdfGeoResource.getGridCoverage(readRequest);
                    //
                    // StringBuilder sB = new StringBuilder(newPath);
                    // if (dateTime != null) {
                    // sB.append("_");
                    // sB.append(dateTime.toString(dateTimeFormatter));
                    // }
                    // if (!Double.isNaN(currentElevationDouble)) {
                    // sB.append("_");
                    // sB.append(currentElevationDouble);
                    // }
                    // String mapPath = sB.toString();
                    // pm.subTask("Exporting map: " + mapPath);
                    // dumpMap(coverage2D, fileCrs, newCrs, mapPath, isAscii, isTiff);
                    // }
                    // }
                    //
                    // } else
                    if (geoResource.canResolve(GridCoverage.class)) {
                        GridCoverage2D coverage2D = (GridCoverage2D) geoResource
                                .resolve(GridCoverage.class, pm);
                        dumpMap(coverage2D, fileCrs, newCrs, newPath, isAscii, isTiff);
                    } else {
                        throw new IOException(
                                "The selected resource doesn't seem to be a coverage layer: " //$NON-NLS-1$
                                        + geoResource.getTitle());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    String message = "An error occurred while exporting the resource: " //$NON-NLS-1$
                            + geoResource.getTitle();
                    ExceptionDetailsDialog.openError(null, message, IStatus.ERROR,
                            JGrassToolsPlugin.PLUGIN_ID, e);
                }
                pm.done();

            }
        };

        PlatformGIS.runInProgressDialog("Downloading selected maps", true, operation, true);
        return true;
    }

    @Override
    public boolean canFinish() {
        return super.canFinish() && canFinish;
    }

    private void dumpMap(GridCoverage2D coverage2D, CoordinateReferenceSystem fileCrs,
            CoordinateReferenceSystem newCrs, String newFilePath, boolean isAscii, boolean isTiff)
            throws FactoryException, IOException {
        if (newCrs != null) {
            MathTransform mathTransform = CRS.findMathTransform(fileCrs, newCrs);
            if (!mathTransform.isIdentity()) {
                coverage2D = (GridCoverage2D) Operations.DEFAULT.resample(coverage2D, newCrs);
            }
        }

        if (isTiff) {
            final GeoTiffFormat format = new GeoTiffFormat();
            final GeoTiffWriteParams wp = new GeoTiffWriteParams();
            wp.setCompressionMode(GeoTiffWriteParams.MODE_DEFAULT);
            wp.setTilingMode(GeoToolsWriteParams.MODE_DEFAULT);
            final ParameterValueGroup paramWrite = format.getWriteParameters();
            paramWrite.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString())
                    .setValue(wp);
            File dumpFile = new File(newFilePath + ".tif"); //$NON-NLS-1$
            GeoTiffWriter gtw = (GeoTiffWriter) format.getWriter(dumpFile);
            gtw.write(coverage2D, paramWrite.values().toArray(new GeneralParameterValue[1]));
        }
        if (isAscii) {
            final ArcGridFormat format = new ArcGridFormat();
            final ArcGridWriteParams wp = new ArcGridWriteParams();
            final ParameterValueGroup paramWrite = format.getWriteParameters();
            paramWrite.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString())
                    .setValue(wp);
            File dumpFile = new File(newFilePath + ".asc"); //$NON-NLS-1$
            ArcGridWriter gtw = (ArcGridWriter) format.getWriter(dumpFile);
            gtw.write(coverage2D, paramWrite.values().toArray(new GeneralParameterValue[1]));
        }
    }
}
