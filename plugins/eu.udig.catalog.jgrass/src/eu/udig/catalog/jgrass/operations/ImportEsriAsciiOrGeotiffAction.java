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

import it.geosolutions.imageio.plugins.arcgrid.AsciiGridsImageMetadataFormat;
import it.geosolutions.imageio.plugins.arcgrid.raster.AsciiGridRaster;
import it.geosolutions.imageio.plugins.arcgrid.raster.AsciiGridRaster.AsciiGridRasterType;

import java.awt.geom.AffineTransform;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.imageio.stream.FileImageInputStream;
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
import org.geotools.gce.geotiff.GeoTiffReader;
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
public class ImportEsriAsciiOrGeotiffAction
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
                                fileDialog.setFilterExtensions(new String[]{"*.asc", "*.ASC", "*.tif", "*.TIF", "*.tiff",
                                        "*.TIFF", "*.*"});
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
                                    CoordinateReferenceSystem jGrassCrs = mapsetResource.getLocationCrs();
                                    GridCoverage2D geodata = null;
                                    CoordinateReferenceSystem fileCrs = null;
                                    if (mapFile.getName().toLowerCase().endsWith(".asc")) {
                                        ArcGridReader arcGridReader = new ArcGridReader(mapFile);
                                        geodata = arcGridReader.read(null);
                                        geodata = geodata.view(ViewType.GEOPHYSICS);
                                        geodata = JGrassCatalogUtilities.removeNovalues(geodata);
                                        fileCrs = arcGridReader.getCrs();
                                    } else if (mapFile.getName().toLowerCase().endsWith(".tif")
                                            || mapFile.getName().toLowerCase().endsWith(".tiff")) {
                                        GeoTiffReader geotiffGridReader = new GeoTiffReader(mapFile);
                                        geodata = geotiffGridReader.read(null);
                                        geodata = geodata.view(ViewType.GEOPHYSICS);
                                        geodata = JGrassCatalogUtilities.removeNovalues(geodata);
                                        fileCrs = geotiffGridReader.getCrs();
                                    }

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