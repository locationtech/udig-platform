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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.media.jai.Interpolation;

import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.grassraster.GrassCoverageReadParam;
import org.geotools.gce.grassraster.GrassCoverageReader;
import org.geotools.gce.grassraster.GrassCoverageWriter;
import org.geotools.gce.grassraster.JGrassConstants;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.builder.GridToEnvelopeMapper;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Envelope;

import eu.udig.catalog.jgrass.core.ChooseCoordinateReferenceSystemDialog;
import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class GrassReprojectOperation implements IOp {
    private String locationName;
    private String mapsetName;
    private String xRes;
    private String yRes;
    private CoordinateReferenceSystem crs;
    private File mapsetFile;
    private int open;

    public void op( final Display display, Object target, final IProgressMonitor monitor ) throws Exception {
        final JGrassMapGeoResource[] mapResourcesArray = (JGrassMapGeoResource[]) target;

        display.syncExec(new Runnable(){

            public void run() {

                Dialog dialog = new Dialog(display.getActiveShell()){
                    private Text locNameText;
                    private Text mapsetNameText;
                    private Text crsText;
                    private Text xresText;
                    private Text yresText;

                    protected Control createDialogArea( Composite maxparent ) {
                        Composite parent = new Composite(maxparent, SWT.None);
                        parent.setLayout(new GridLayout());
                        parent.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

                        // the location name group
                        Group locNameGroup = new Group(parent, SWT.None);
                        locNameGroup.setLayout(new GridLayout(2, false));
                        locNameGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                                | GridData.GRAB_VERTICAL));
                        locNameGroup.setText("new location name");

                        locNameText = new Text(locNameGroup, SWT.BORDER);
                        locNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL
                                | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_CENTER));
                        locNameText.setText("newLocation");

                        // the mapset name group
                        Group mapsetNameGroup = new Group(parent, SWT.None);
                        mapsetNameGroup.setLayout(new GridLayout(2, false));
                        mapsetNameGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                                | GridData.GRAB_VERTICAL));
                        mapsetNameGroup.setText("new mapset name");

                        mapsetNameText = new Text(mapsetNameGroup, SWT.BORDER);
                        mapsetNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL
                                | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_CENTER));
                        mapsetNameText.setText("newMapset");

                        // the crs choice group
                        Group crsGroup = new Group(parent, SWT.None);
                        crsGroup.setLayout(new GridLayout(2, false));
                        crsGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                                | GridData.GRAB_VERTICAL));
                        crsGroup.setText("choose the coordinate reference system for the new location");

                        crsText = new Text(crsGroup, SWT.BORDER);
                        crsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL
                                | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_CENTER));
                        crsText.setEditable(false);

                        final Button crsButton = new Button(crsGroup, SWT.BORDER);
                        crsButton.setText(" Choose CRS ");
                        crsButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
                            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                                final ChooseCoordinateReferenceSystemDialog crsChooser = new ChooseCoordinateReferenceSystemDialog();
                                crsChooser.open(new Shell(Display.getDefault()));
                                CoordinateReferenceSystem readCrs = crsChooser.getCrs();
                                if (readCrs == null)
                                    return;
                                crsText.setText(readCrs.getName().toString());
                                crsText.setData(readCrs);
                            }
                        });

                        // the location name group
                        Group resolutionGroup = new Group(parent, SWT.None);
                        resolutionGroup.setLayout(new GridLayout(2, false));
                        resolutionGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                                | GridData.GRAB_VERTICAL));
                        resolutionGroup.setText("output map resolution");

                        String res = "";
                        if (mapResourcesArray.length > 0) {
                            try {
                                JGrassRegion activeWindow = mapResourcesArray[0].getActiveWindow();
                                res = String.valueOf(activeWindow.getNSResolution());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        Label xresLabel = new Label(resolutionGroup, SWT.NONE);
                        xresLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
                        xresLabel.setText("X resolution");

                        xresText = new Text(resolutionGroup, SWT.BORDER);
                        xresText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL
                                | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_CENTER));
                        xresText.setText(res);

                        Label yresLabel = new Label(resolutionGroup, SWT.NONE);
                        yresLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
                        yresLabel.setText("Y resolution");

                        yresText = new Text(resolutionGroup, SWT.BORDER);
                        yresText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL
                                | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_CENTER));
                        yresText.setText(res);

                        return parent;
                    }

                    protected void okPressed() {
                        locationName = locNameText.getText();
                        if (locationName == null || locationName.length() < 1) {
                            locationName = "newLocation";
                        }
                        mapsetName = mapsetNameText.getText();
                        if (mapsetName == null || mapsetName.length() < 1) {
                            mapsetName = "newMapset";
                        }
                        xRes = xresText.getText();
                        yRes = yresText.getText();
                        Object crsData = crsText.getData();
                        if (crsData instanceof CoordinateReferenceSystem) {
                            crs = (CoordinateReferenceSystem) crsData;
                        }
                        super.okPressed();
                    }
                };
                dialog.setBlockOnOpen(true);
                open = dialog.open();

            }

        });

        /*
         * run with backgroundable progress monitoring
         */
        IRunnableWithProgress operation = new IRunnableWithProgress(){

            public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                if (open == SWT.CANCEL) {
                    return;
                }
                if (locationName == null || mapsetName == null || crs == null || mapResourcesArray.length < 1) {
                    MessageBox msgBox = new MessageBox(display.getActiveShell(), SWT.ICON_ERROR);
                    msgBox.setMessage("An error occurred in processing the user supplied data.");
                    msgBox.open();
                    return;
                }
                /*
                 * finally do some processing
                 */
                int mapNum = mapResourcesArray.length;
                monitor.beginTask("Reprojecting maps to new Location...", mapNum);
                for( int i = 0; i < mapNum; i++ ) {
                    JGrassMapGeoResource tmpMap = mapResourcesArray[i];
                    JGrassMapEnvironment jgMEnv = new JGrassMapEnvironment(tmpMap.getMapFile());

                    try {
                        /*
                         * TODO get envelope from original region and reproject it.
                         * then adapt it to the output resolution and
                         * finally reproject the map on the new grid.
                         */
                        JGrassRegion sourceRegion = jgMEnv.getActiveRegion();
                        CoordinateReferenceSystem sourceCrs = jgMEnv.getCoordinateReferenceSystem();
                        Envelope sourceEnvelope = sourceRegion.getEnvelope();
                        MathTransform tr = CRS.findMathTransform(sourceCrs, crs);
                        Envelope outputEnvelope = JTS.transform(sourceEnvelope, tr);

                        double west = outputEnvelope.getMinX();
                        double east = outputEnvelope.getMaxX();
                        double south = outputEnvelope.getMinY();
                        double north = outputEnvelope.getMaxY();

                        double xResolution = Double.parseDouble(xRes);
                        double yResolution = Double.parseDouble(yRes);

                        // if in metric, snap to integer bounds
                        int intWest = (int) Math.floor(west);
                        int intSouth = (int) Math.floor(south);
                        if (west - intWest < xResolution && south - intSouth < yResolution) {
                            west = intWest;
                            south = intSouth;
                        }

                        double w = east - west;
                        double h = north - south;
                        // modify the envelope to be in the requested resolution
                        double cols = Math.floor(w / xResolution) + 1.0;
                        double rows = Math.floor(h / yResolution) + 1.0;

                        double newEast = west + cols * xResolution;
                        double newNorth = south + rows * yResolution;

                        ReferencedEnvelope referencedEnvelope = new ReferencedEnvelope(west, newEast, south, newNorth, crs);

                        GridToEnvelopeMapper g2eMapper = new GridToEnvelopeMapper();
                        g2eMapper.setEnvelope(referencedEnvelope);
                        GridEnvelope2D gridEnvelope2D = new GridEnvelope2D(0, 0, (int) cols, (int) rows);
                        g2eMapper.setGridRange(gridEnvelope2D);
                        g2eMapper.setPixelAnchor(PixelInCell.CELL_CENTER);
                        MathTransform gridToEnvelopeTransform = g2eMapper.createTransform();

                        GridGeometry outputGridGeometry = new GridGeometry2D(gridEnvelope2D, gridToEnvelopeTransform, crs);

                        GridCoverage2D coverage2D = JGrassCatalogUtilities.getGridcoverageFromGrassraster(jgMEnv, sourceRegion);
                        GrassCoverageReadParam gcReadParam = new GrassCoverageReadParam(sourceRegion);
                        GridCoverage2D reprojected = (GridCoverage2D) Operations.DEFAULT.resample(coverage2D, crs,
                                outputGridGeometry, Interpolation.getInstance(Interpolation.INTERP_BICUBIC));

                        JGrassRegion jgRegion = new JGrassRegion(west, newEast, south, newNorth, xResolution, yResolution);

                        // GridCoverage2D coverage2D = tmp.read(null);
                        // GridCoverage2D reprojected = (GridCoverage2D)
                        // Operations.DEFAULT.resample(
                        // coverage2D, crs);
                        // Envelope2D envelope2D = reprojected.getEnvelope2D();
                        // JGrassRegion jgRegion = new JGrassRegion(envelope2D);

                        if (i == 0) {
                            // create the location structure
                            File grassDbFile = jgMEnv.getLOCATION().getParentFile();
                            File newLocationFile = new File(grassDbFile, locationName);
                            try {
                                JGrassCatalogUtilities.createLocation(newLocationFile.getAbsolutePath(), crs, jgRegion);
                                JGrassCatalogUtilities
                                        .createMapset(newLocationFile.getAbsolutePath(), mapsetName, null, jgRegion);
                            } catch (Exception e) {
                                // ignore this for now
                            }
                            mapsetFile = new File(newLocationFile, mapsetName);
                        }
                        File newMapFile = new File(mapsetFile, JGrassConstants.CELL + File.separator
                                + tmpMap.getMapFile().getName());

                        JGrassCatalogUtilities.writeGridCoverageFromGrassraster(newMapFile, jgRegion, reprojected);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    monitor.worked(1);
                }
                monitor.done();

                JGrassCatalogUtilities.addServiceToCatalog(mapsetFile.getParent() + File.separator
                        + JGrassCatalogUtilities.JGRASS_WORKSPACE_FILENAME, new NullProgressMonitor());

            }
        };
        PlatformGIS.runInProgressDialog("Reprojecting maps to new Location...", true, operation, true);

    }
}
