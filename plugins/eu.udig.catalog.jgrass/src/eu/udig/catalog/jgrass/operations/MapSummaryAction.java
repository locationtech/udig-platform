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

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;

import net.refractions.udig.ui.ExceptionDetailsDialog;
import net.refractions.udig.ui.PlatformGIS;

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
import org.geotools.coverage.grid.ViewType;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.gce.grassraster.format.GrassCoverageFormatFactory;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.parameter.Parameter;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.udig.catalog.jgrass.JGrassPlugin;
import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;

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

                        final Object object = selection.getFirstElement();
                        try {
                            pm.beginTask("Collecting stats...", IProgressMonitor.UNKNOWN);

                            if (object instanceof JGrassMapGeoResource) {
                                JGrassMapGeoResource mr = (JGrassMapGeoResource) object;
                                File mapFile = mr.getMapFile();
                                JGrassMapEnvironment mapEnvironment = new JGrassMapEnvironment(mapFile);
                                JGrassRegion jGrassRegion = mapEnvironment.getActiveRegion();
                                
                                GeneralParameterValue[] readParams = createGridGeometryGeneralParameter(jGrassRegion.getCols(),
                                        jGrassRegion.getRows(), jGrassRegion.getNorth(), jGrassRegion.getSouth(),
                                        jGrassRegion.getEast(), jGrassRegion.getWest(),
                                        mapEnvironment.getCoordinateReferenceSystem());

                                AbstractGridFormat format = (AbstractGridFormat) new GrassCoverageFormatFactory().createFormat();
                                AbstractGridCoverage2DReader reader = format.getReader(mapEnvironment.getCELL());
                                GridCoverage2D geodata = ((GridCoverage2D) reader.read(readParams));
                                geodata = geodata.view(ViewType.GEOPHYSICS);
                                RandomIter inputIter = RandomIterFactory.create(geodata.getRenderedImage(), null);
                                /*
                                 * calculate mean, max and min
                                 */
                                double mean = 0.0;

                                double[] minMaxMeans = new double[]{Double.MAX_VALUE, Double.MIN_VALUE, 0, 0, 0};
                                int cols = jGrassRegion.getCols();
                                int rows = jGrassRegion.getRows();
                                int validCells = 0;
                                for( int y = 0; y < rows; y++ ) {
                                    for( int x = 0; x < cols; x++ ) {
                                        double value = inputIter.getSampleDouble(x, y, 0);
                                        if (!Double.isNaN(value)) {
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
                                minMaxMeans[4] = validCells * jGrassRegion.getWEResolution() * jGrassRegion.getNSResolution();

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
                                sb.append(jGrassRegion.toString() + "\n");

                                final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                                MessageDialog.openInformation(shell, "Summary", sb.toString());
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