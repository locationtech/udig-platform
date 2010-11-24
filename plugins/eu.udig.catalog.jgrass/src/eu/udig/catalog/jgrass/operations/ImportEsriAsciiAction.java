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
package eu.udig.catalog.jgrass.operations;

import java.awt.geom.AffineTransform;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.media.jai.RasterFactory;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;
import javax.media.jai.iterator.WritableRandomIter;

import net.refractions.udig.ui.ExceptionDetailsDialog;
import net.refractions.udig.ui.PlatformGIS;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.ViewType;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.gce.grassraster.GrassCoverageWriter;
import org.geotools.gce.grassraster.JGrassConstants;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.format.GrassCoverageFormat;
import org.geotools.gce.grassraster.format.GrassCoverageFormatFactory;
import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.matrix.XAffineTransform;
import org.opengis.coverage.grid.GridCoverageWriter;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.udig.catalog.jgrass.JGrassPlugin;
import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * Action to import an esrii ascii grid into the mapset.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ImportEsriAsciiAction
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

                        Object firstElement = selection.getFirstElement();
                        if (firstElement instanceof JGrassMapsetGeoResource) {
                            try {
                                JGrassMapsetGeoResource mapsetResource = (JGrassMapsetGeoResource) firstElement;

                                File mapsetFile = mapsetResource.getFile();
                                // create a thread and inside do a syncExec
                                FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN
                                        | SWT.MULTI);
                                fileDialog.setFilterExtensions(new String[]{"*.asc"});
                                String selpath = fileDialog.open();
                                if (selpath == null) {
                                    return;
                                }
                                File mapFile = new File(selpath);
                                if (!mapFile.exists()) {
                                    return;
                                }
                                File folder = mapFile.getParentFile();

                                String[] fileNames = fileDialog.getFileNames();
                                pm.beginTask("Importing maps...", fileNames.length);
                                for( int i = 0; i < fileNames.length; i++ ) {
                                    mapFile = new File(folder, fileNames[i]);
                                    /*
                                     * import the file
                                     */
                                    ArcGridReader arcGridReader = new ArcGridReader(mapFile);
                                    GridCoverage2D geodata = arcGridReader.read(null);
                                    geodata = geodata.view(ViewType.GEOPHYSICS);

                                    geodata = removeNovalues(geodata);

                                    CoordinateReferenceSystem jGrassCrs = mapsetResource.getJGrassCrs();
                                    CoordinateReferenceSystem fileCrs = arcGridReader.getCrs();

                                    // if required, reproject
                                    if (!CRS.equalsIgnoreMetadata(jGrassCrs, fileCrs)) {
                                        geodata = (GridCoverage2D) Operations.DEFAULT.resample(geodata, jGrassCrs);
                                    }

                                    String mapName = mapFile.getName();
                                    mapName = FilenameUtils.getBaseName(mapName);
                                    JGrassMapEnvironment mapEnvironment = new JGrassMapEnvironment(mapsetFile, mapName);
                                    GrassCoverageFormat format = new GrassCoverageFormatFactory().createFormat();
                                    GridCoverageWriter writer = format.getWriter(mapEnvironment.getCELL(), null);

                                    GeneralParameterValue[] readParams = null;
                                    writer.write(geodata, readParams);

                                    JGrassCatalogUtilities.addMapToCatalog(mapsetFile.getParent(), mapsetFile.getName(), mapName,
                                            JGrassConstants.GRASSBINARYRASTERMAP);
                                    pm.worked(1);
                                }

                                MessageDialog.openInformation(shell, "IMPORT", "Maps successfully imported");
                            } catch (IOException e) {
                                e.printStackTrace();
                                String message = "An error occurred while importing the map.";
                                ExceptionDetailsDialog.openError("ERROR", message, IStatus.ERROR, JGrassPlugin.PLUGIN_ID, e);
                            } finally {
                                pm.done();
                            }
                        }
                    }

                });
            }

        };

        PlatformGIS.runInProgressDialog("Import maps...", true, operation, true);

    }

    public static final String NORTH = "NORTH"; //$NON-NLS-1$
    public static final String SOUTH = "SOUTH"; //$NON-NLS-1$
    public static final String WEST = "WEST"; //$NON-NLS-1$
    public static final String EAST = "EAST"; //$NON-NLS-1$
    public static final String XRES = "XRES"; //$NON-NLS-1$
    public static final String YRES = "YRES"; //$NON-NLS-1$
    public static final String ROWS = "ROWS"; //$NON-NLS-1$
    public static final String COLS = "COLS"; //$NON-NLS-1$

    private GridCoverage2D removeNovalues( GridCoverage2D geodata ) {
        // need to adapt it, for now do it dirty
        HashMap<String, Double> params = getRegionParamsFromGridCoverage(geodata);
        int height = params.get(ROWS).intValue();
        int width = params.get(COLS).intValue();
        WritableRaster tmpWR = createDoubleWritableRaster(width, height, null, null, null);
        WritableRandomIter tmpIter = RandomIterFactory.createWritable(tmpWR, null);
        RenderedImage readRI = geodata.getRenderedImage();
        RandomIter readIter = RandomIterFactory.create(readRI, null);
        for( int r = 0; r < height; r++ ) {
            for( int c = 0; c < width; c++ ) {
                double value = readIter.getSampleDouble(c, r, 0);

                if (Double.isNaN(value) || Float.isNaN((float) value) || Math.abs(value - -9999.0) < .0000001) {
                    tmpIter.setSample(c, r, 0, Double.NaN);
                } else {
                    tmpIter.setSample(c, r, 0, value);
                }
            }
        }
        geodata = buildCoverage("newcoverage", tmpWR, params, geodata.getCoordinateReferenceSystem());
        return geodata;
    }

    /**
     * Creates a {@link GridCoverage2D coverage} from the {@link WritableRaster writable raster} and the necessary geographic Information.
     * 
     * @param name the name of the coverage.
     * @param writableRaster the raster containing the data.
     * @param envelopeParams the map of boundary parameters.
     * @param crs the {@link CoordinateReferenceSystem}.
     * @return the {@link GridCoverage2D coverage}.
     */
    public static GridCoverage2D buildCoverage( String name, WritableRaster writableRaster,
            HashMap<String, Double> envelopeParams, CoordinateReferenceSystem crs ) {

        double west = envelopeParams.get(WEST);
        double south = envelopeParams.get(SOUTH);
        double east = envelopeParams.get(EAST);
        double north = envelopeParams.get(NORTH);
        Envelope2D writeEnvelope = new Envelope2D(crs, west, south, east - west, north - south);
        GridCoverageFactory factory = CoverageFactoryFinder.getGridCoverageFactory(null);

        GridCoverage2D coverage2D = factory.create(name, writableRaster, writeEnvelope);
        return coverage2D;
    }

    /**
     * Creates a {@link WritableRaster writable raster}.
     * 
     * @param width width of the raster to create.
     * @param height height of the raster to create.
     * @param dataClass data type for the raster. If <code>null</code>, defaults to double.
     * @param sampleModel the samplemodel to use. If <code>null</code>, defaults to 
     *                  <code>new ComponentSampleModel(dataType, width, height, 1, width, new int[]{0});</code>.
     * @param value value to which to set the raster to. If null, the default of the raster creation is 
     *                  used, which is 0.
     * @return a {@link WritableRaster writable raster}.
     */
    public static WritableRaster createDoubleWritableRaster( int width, int height, Class< ? > dataClass,
            SampleModel sampleModel, Double value ) {
        int dataType = DataBuffer.TYPE_DOUBLE;
        if (dataClass != null) {
            if (dataClass.isAssignableFrom(Integer.class)) {
                dataType = DataBuffer.TYPE_INT;
            } else if (dataClass.isAssignableFrom(Float.class)) {
                dataType = DataBuffer.TYPE_FLOAT;
            } else if (dataClass.isAssignableFrom(Byte.class)) {
                dataType = DataBuffer.TYPE_BYTE;
            }
        }
        if (sampleModel == null) {
            sampleModel = new ComponentSampleModel(dataType, width, height, 1, width, new int[]{0});
        }

        WritableRaster raster = RasterFactory.createWritableRaster(sampleModel, null);
        if (value != null) {
            // autobox only once
            double v = value;

            for( int y = 0; y < height; y++ ) {
                for( int x = 0; x < width; x++ ) {
                    raster.setSample(x, y, 0, v);
                }
            }
        }
        return raster;
    }

    public static HashMap<String, Double> getRegionParamsFromGridCoverage( GridCoverage2D gridCoverage ) {
        HashMap<String, Double> envelopeParams = new HashMap<String, Double>();

        Envelope envelope = gridCoverage.getEnvelope();

        DirectPosition lowerCorner = envelope.getLowerCorner();
        double[] westSouth = lowerCorner.getCoordinate();
        DirectPosition upperCorner = envelope.getUpperCorner();
        double[] eastNorth = upperCorner.getCoordinate();

        GridGeometry2D gridGeometry = gridCoverage.getGridGeometry();
        GridEnvelope2D gridRange = gridGeometry.getGridRange2D();
        int height = gridRange.height;
        int width = gridRange.width;

        AffineTransform gridToCRS = (AffineTransform) gridGeometry.getGridToCRS();
        double xRes = XAffineTransform.getScaleX0(gridToCRS);
        double yRes = XAffineTransform.getScaleY0(gridToCRS);

        envelopeParams.put(NORTH, eastNorth[1]);
        envelopeParams.put(SOUTH, westSouth[1]);
        envelopeParams.put(WEST, westSouth[0]);
        envelopeParams.put(EAST, eastNorth[0]);
        envelopeParams.put(XRES, xRes);
        envelopeParams.put(YRES, yRes);
        envelopeParams.put(ROWS, (double) height);
        envelopeParams.put(COLS, (double) width);

        return envelopeParams;
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

}