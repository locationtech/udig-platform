/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package eu.udig.imagegeoreferencing;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultDerivedCRS;
import org.geotools.referencing.cs.DefaultCartesianCS;
import org.geotools.referencing.operation.transform.WarpTransform2D;
import org.opengis.coverage.Coverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

import eu.udig.imagegeoreferencing.i18n.Messages;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.tool.Tool;
import net.refractions.udig.ui.PlatformGIS;

/**
 * Warp the selected image based on the placemarkers and load the result onto the map
 * 
 * @author GDavis, Refractions Research
 * @since 1.1.0
 */
public class WarpImageTool extends AbstractModalTool implements ModalTool {

    public static final String TOOLID = "net.refractions.udig.imagegeoreference.tools.warpImageTool"; //$NON-NLS-1$

    private List<PlaceMarker> imageMarkers;
    private List<PlaceMarker> basemapMarkers;
    private int warpValue = PlaceMarkersTool.WARP_MINDEGREE;

    public WarpImageTool() {
        this(MOUSE);
    }

    public WarpImageTool( int targets ) {
        super(targets);
    }

    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
        if (active) {
            // warp the selected image
            IMap activeMap = ApplicationGIS.getActiveMap();
            warpImage(activeMap);
        }
    }

    private void loadMarkers( IMap map ) {
        GeoReferenceMapGraphic mapGraphic = GeoReferenceUtils.getMapGraphic(map);
        if (mapGraphic != null) {
            imageMarkers = mapGraphic.getImageMarkers();
            basemapMarkers = mapGraphic.getBasemapMarkers();
            warpValue = mapGraphic.getWarpValue();
        }
    }

    /**
     * Warp the currently selected image with the current markers
     * 
     * @param map
     */
    private void warpImage( IMap map ) {
        // load the current markers and warp value
        loadMarkers(map);

        if (!validateMarkers()) {
            showErrorDialog();
            return;
        }

        // get the selected Image to warp
        GeoReferenceImage selectedGeoImage = null;
        GeoReferenceMapGraphic mapGraphic = GeoReferenceUtils.getMapGraphic(map);
        if (mapGraphic != null && mapGraphic.getImages() != null) {
            selectedGeoImage = GeoReferenceUtils.getSelectedGeoImage(mapGraphic.getImages(), map);
        }
        if (selectedGeoImage == null) {
            showErrorDialog();
            return;
        }

        // create a WarpTransform2D of the source and dest points
        Point2D[] dstCoords = getImagePoints(selectedGeoImage, imageMarkers);
        Point2D[] srcCoords = getBasemapPoints(map, basemapMarkers);
        // the best warping results seem to occur when the points are ordered from
        // left to right and up to down, so order them in that fashion as best as possible first.
        int[] order = getSortOrder(dstCoords);
        dstCoords = sortArray(dstCoords, order);
        srcCoords = sortArray(srcCoords, order);

        // warp the image within a progress dialog process
        WarpImageProcess warpProcess = new WarpImageProcess(map, selectedGeoImage, srcCoords, dstCoords, warpValue);
        PlatformGIS.runInProgressDialog("Warping Image", false, warpProcess, false); //$NON-NLS-1$
        GridCoverage2D warpedCoverage = warpProcess.getWarpedCoverage();

        // regardless of results, clear the points from the map and reset the point vars
        Tool placeMarkersTool = GeoReferenceUtils.findTool(PlaceMarkersTool.TOOLID);
        if (placeMarkersTool != null && placeMarkersTool instanceof PlaceMarkersTool) {
            ((PlaceMarkersTool) placeMarkersTool).clearPointVars();
        }

        // show dialog to confirm saving the displayed result or to cancel and restart
        if (warpedCoverage != null) {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

            FileDialog fd = new FileDialog(shell, SWT.SAVE);
            fd.setFilterExtensions(new String[]{"*.tif"}); //$NON-NLS-1$
            fd.setText(Messages.saveFile_title);
            String filename = fd.open();
            if (filename != null && filename != "") { //$NON-NLS-1$
                WriteTifAndLoadProcess writeTifAndLoadProcess = new WriteTifAndLoadProcess(map, warpedCoverage, filename);
                PlatformGIS.runInProgressDialog("Saving and Loading TIF", false, writeTifAndLoadProcess, false); //$NON-NLS-1$

                // now remove the selected geoImage from the mapgraphic layer
                HashMap<IMap, HashMap<String, GeoReferenceImage>> mapimages = mapGraphic.getImages();
                selectedGeoImage.setSelected(false);
                HashMap<String, GeoReferenceImage> images = mapimages.get(map);
                if (images != null)
                    images.remove(selectedGeoImage.getFilename());
            }

            // refresh the map
            if (mapGraphic != null)
                mapGraphic.getLayer(map).refresh(null);
        }
    }

    /**
     * Ensure at least the min amount of markers are set
     *
     * @return
     */
    private boolean validateMarkers() {
        if (imageMarkers == null || imageMarkers.size() < PlaceMarkersTool.MIN_PLACEMARKERS || basemapMarkers == null
                || basemapMarkers.size() < PlaceMarkersTool.MIN_PLACEMARKERS || imageMarkers.size() != basemapMarkers.size()) {
            return false;
        }
        // loop through markers and make sure they are set
        for( int i = 0; i < imageMarkers.size(); i++ ) {
            PlaceMarker iMarker = imageMarkers.get(i);
            PlaceMarker bMarker = basemapMarkers.get(i);
            if (iMarker.getPoint() == null || bMarker.getPoint() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Show a dialog with the error explaining why the warp would not process
     */
    private void showErrorDialog() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final Dialog dialog = new Dialog(shell){
            @Override
            protected Control createDialogArea( Composite parent ) {
                Composite container = (Composite) super.createDialogArea(parent);
                GridLayout layout = new GridLayout(1, true);
                container.setLayout(layout);
                GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
                layoutData.widthHint = 450;
                container.setLayoutData(layoutData);

                Label descLabel = new Label(container, SWT.WRAP);
                descLabel.setText(MessageFormat.format(Messages.WarpDialog_desc, PlaceMarkersTool.MIN_PLACEMARKERS));
                layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
                layoutData.verticalSpan = 1;
                layoutData.horizontalSpan = 1;
                descLabel.setLayoutData(layoutData);

                return container;
            }

            @Override
            protected void configureShell( Shell newShell ) {
                super.configureShell(newShell);
                newShell.setText(Messages.WarpDialog_title);
                GridLayout layout = new GridLayout();
                layout.numColumns = 1;
                newShell.setLayout(layout);
            }
        };
        dialog.setBlockOnOpen(true);
        dialog.open();
    }

    /**
     * Sort the given point array in the order of the provided index array
     * 
     * @param ar
     * @param order array
     */
    private Point2D[] sortArray( Point2D[] ar, int[] order ) {
        Point2D[] ordered = new Point2D[ar.length];
        for( int i = 0; i < ar.length; i++ ) {
            ordered[i] = ar[order[i]];
        }
        return ordered;
    }

    /**
     * Determine the order the array should be sorted in
     * 
     * @param ar
     * @return new order array
     */
    private int[] getSortOrder( Point2D[] ar ) {
        Point2D[] sorted = sortPointsX(ar);
        sorted = sortPointsY(sorted);

        // determine the new sort order
        int[] order = new int[ar.length];
        for( int i = 0; i < sorted.length; i++ ) {
            Point2D point = sorted[i];
            for( int y = 0; y < ar.length; y++ ) {
                if (point.equals(ar[y])) {
                    order[i] = y;
                    break;
                }
            }
        }
        return order;
    }

    /**
     * Sort the point array from "left" to "right" as that
     * seems to give better results.
     * 
     * @param ar
     * @return sorted array
     */
    private Point2D[] sortPointsX( Point2D[] ar ) {
        Point2D[] sorted = new Point2D[ar.length];
        for( int i = 0; i < ar.length; i++ ) {
            Point2D point = ar[i];
            for( int y = 0; y < sorted.length; y++ ) {
                Point2D point2 = sorted[y];
                if (point2 == null) {
                    sorted[y] = point;
                    break;
                } else {
                    if (point2.getX() < point.getX()) {
                        for( int z = sorted.length - 1; z > y; z-- ) {
                            sorted[z] = sorted[z - 1];
                        }
                        sorted[y] = point;
                        break;
                    }
                }
            }
        }
        return sorted;
    }

    /**
     * Sort the point array from "top" to "bottom" as that
     * seems to give better results.
     * 
     * @param ar
     * @return sorted array
     */
    private Point2D[] sortPointsY( Point2D[] ar ) {
        Point2D[] sorted = new Point2D[ar.length];
        for( int i = 0; i < ar.length; i++ ) {
            Point2D point = ar[i];
            for( int y = 0; y < sorted.length; y++ ) {
                Point2D point2 = sorted[y];
                if (point2 == null) {
                    sorted[y] = point;
                    break;
                } else {
                    if (point2.getY() < point.getY()) {
                        for( int z = sorted.length - 1; z > y; z-- ) {
                            sorted[z] = sorted[z - 1];
                        }
                        sorted[y] = point;
                        break;
                    }
                }
            }
        }
        return sorted;
    }

    /**
     * Convert the placemarkers into an array of Point2D points relative to the image's
     * top-left corner.
     * 
     * @param selectedGeoImage
     * @param markers
     * @return
     */
    private Point2D[] getImagePoints( GeoReferenceImage selectedGeoImage, List<PlaceMarker> markers ) {
        int topleft_x = selectedGeoImage.getPosX();
        int topleft_y = selectedGeoImage.getPosY();
        Point2D[] points = new Point2D[markers.size()];
        int index = 0;
        for( PlaceMarker marker : markers ) {
            java.awt.Point point = marker.getPoint();
            // flip the y coordinate
            int y = point.y - topleft_y;
            y = selectedGeoImage.getScaledHeight() - y;
            java.awt.Point scaledPoint = new java.awt.Point(point.x - topleft_x, y);
            // the above point is for the scaled image, so adjust it for the full size image
            double xScale = (double) scaledPoint.x / (double) selectedGeoImage.getScaledWidth();
            double yScale = (double) scaledPoint.y / (double) selectedGeoImage.getScaledHeight();
            Image image = selectedGeoImage.getAWTImage();
            java.awt.Point fullPoint = new java.awt.Point((int) (image.getWidth(null) * xScale),
                    (int) (image.getHeight(null) * yScale));
            points[index] = fullPoint;
            index++;
        }
        return points;
    }

    /**
     * Convert the placemarkers into an array of Point2D points in the basemap's CRS
     * 
     * @param map
     * @param markers
     * @return
     */
    private Point2D[] getBasemapPoints( IMap map, List<PlaceMarker> markers ) {
        Point2D[] points = new Point2D[markers.size()];
        int index = 0;
        for( PlaceMarker marker : markers ) {
            java.awt.Point point = marker.getPoint();
            Coordinate coord = map.getViewportModel().pixelToWorld(point.x, point.y);
            points[index] = new Point2D.Double(coord.x, coord.y);
            index++;
        }
        return points;
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed( MapMouseEvent e ) {
        // validate the mouse click
        if (!validModifierButtonCombo(e)) {
            return;
        }
    }

    /**
     * Checks if the user clicked the right mouse button, and if they did then
     * rotate the selected image referencing tool.
     * 
     * Otherwise it returns true if the combination of buttons and modifiers 
     * are legal with a left-mouse-click.
     * 
     * @param e
     * @return
     */
    protected boolean validModifierButtonCombo( MapMouseEvent e ) {
        if (e.buttons == MapMouseEvent.BUTTON3) {
            // rotate to the next tool after the move markers tool
            GeoReferenceUtils.rotateToNextTool(MoveMarkersTool.TOOLID);
            return false;
        }
        return e.buttons == MapMouseEvent.BUTTON1 && !(e.modifiersDown());
    }

}

/**
 * Warps the given geoImage.
 * 
 * @author GDavis
 *
 */
final class WarpImageProcess implements IRunnableWithProgress {

    private IMap map;
    private GeoReferenceImage geoImage;
    private Point2D[] srcCoords;
    private Point2D[] dstCoords;
    private int warpValue;
    private GridCoverage2D warpedCoverage;

    public WarpImageProcess( IMap map, GeoReferenceImage geoImage, Point2D[] srcCoords, Point2D[] dstCoords, int warpValue ) {
        this.map = map;
        this.geoImage = geoImage;
        this.srcCoords = srcCoords;
        this.dstCoords = dstCoords;
        this.warpValue = warpValue;
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
        WarpTransform2D warpTransform = new WarpTransform2D(srcCoords, dstCoords, warpValue);

        // create a DefaultDerivedCRS for the CRS of the image
        CoordinateReferenceSystem crs = map.getViewportModel().getCRS();
        DefaultDerivedCRS derivedCRS = new DefaultDerivedCRS("imageCRS", crs, warpTransform, //$NON-NLS-1$
                DefaultCartesianCS.GENERIC_2D);

        // now create a gridcoverage for the new warped image
        BufferedImage image = (BufferedImage) geoImage.getAWTImage();
        GridCoverageFactory factory = new GridCoverageFactory();
        ReferencedEnvelope ref = new ReferencedEnvelope(0, image.getWidth(null), 0, image.getHeight(null), derivedCRS);
        GridCoverage2D coverage = (GridCoverage2D) factory.create("GridCoverage", image, ref); //$NON-NLS-1$

        // resample the new image with the world CRS
        warpedCoverage = null;
        // RenderedImage rImage = null;
        try {
            Operations ops = new Operations(null);
            Coverage resample = ops.resample(coverage, crs);
            warpedCoverage = (GridCoverage2D) resample;

            // write out a jpg version for testing
            // rImage = warpedCoverage.getRenderedImage();
            // ImageIO.write(rImage, "jpg", new File("blahblah.jpg"));

            // // add the result as a layer to the map by first saving it as a temp tiff file
            // // and then loading that file as a layer and deleting the temp file
            //          File tempfile = new File("warptemp.tif"); //$NON-NLS-1$
            // GeoTiffWriter writer = new GeoTiffWriter(tempfile);
            // writer.write(warpedCoverage.geophysics(true), null);
            // IGeoResource tiffResource = getTiffResource(tempfile.toURL());
            // if (tiffResource != null) {
            // ApplicationGIS.addLayersToMap(map, Collections.singletonList(tiffResource), -1);
            // }
            // delete temp file
            // tempfile.delete();

            // IGeoResource resource =
            // CatalogPlugin.getDefault().getLocalCatalog().createTemporaryResource(tempfile);
            // ApplicationGIS.addLayersToMap(map, Collections.singletonList(resource), -1);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // // create a bufferedImage of the result using the type from the current image
        // Image image2 = geoImage.getAWTImage();
        // int type = 0;
        // if (image2 instanceof BufferedImage) {
        // type = ((BufferedImage)image2).getType();
        // }
        // if (type == 0) {
        // type = BufferedImage.TYPE_INT_RGB;
        // }
        // BufferedImage buffImage = new BufferedImage(rImage.getWidth(),
        // rImage.getHeight(), type);
        // Graphics2D graphics = buffImage.createGraphics();
        // graphics.drawRenderedImage(rImage, new AffineTransform());
        // try {
        // ImageIO.write(buffImage, "jpg", new File("testtest.jpg"));
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // rImage = null;
        // //selectedGeoImage.setScaledAWTImage(buffImage);
    }

    public IGeoResource getTiffResource( URL url ) {
        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
        List<IService> services = factory.createService(url);

        if (services.isEmpty()) {
            return null;
        }
        for( IService service : services ) {
            IResolve resolve;
            IGeoResource geoR;
            try {
                resolve = service.members(null).get(0);
                geoR = resolve.resolve(IGeoResource.class, new NullProgressMonitor());
                return geoR;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public GridCoverage2D getWarpedCoverage() {
        return warpedCoverage;
    }
}

/**
 * Writes the given coverage to file as a tif and then loads it on the map.
 * 
 * @author GDavis
 *
 */
final class WriteTifAndLoadProcess implements IRunnableWithProgress {

    private IMap map;
    private GridCoverage2D warpedCoverage;
    private String filename;

    public WriteTifAndLoadProcess( IMap map, GridCoverage2D warpedCoverage, String filename ) {
        this.map = map;
        this.warpedCoverage = warpedCoverage;
        this.filename = filename;
    }

    @SuppressWarnings("deprecation")
    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
        if (warpedCoverage == null || filename == null || filename == "") { //$NON-NLS-1$
            monitor.done();
            return;
        }

        monitor.beginTask("Writing Warped Image to TIF file...", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
        GeoTiffWriter writer;
        if (!filename.contains(".tif")) { //$NON-NLS-1$
            filename += ".tif"; //$NON-NLS-1$
        }
        try {
            writer = new GeoTiffWriter(new File(filename));
            writer.write(warpedCoverage.geophysics(true), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        monitor.setTaskName("Loading TIF file into uDig..."); //$NON-NLS-1$

        // add the result as a layer to the map by
        // loading that file as a layer
        File tempfile = new File(filename);
        IGeoResource tiffResource = null;
        try {
            writer = new GeoTiffWriter(tempfile);
            writer.write(warpedCoverage.geophysics(true), null);
            tiffResource = getTiffResource(tempfile.toURL());
        } catch (IOException e) {
            e.printStackTrace();
        }

        monitor.setTaskName("Updating Map..."); //$NON-NLS-1$

        if (tiffResource != null) {
            ApplicationGIS.addLayersToMap(map, Collections.singletonList(tiffResource), -1);
        }

    }

    private IGeoResource getTiffResource( URL url ) {
        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
        List<IService> services = factory.createService(url);

        if (services.isEmpty()) {
            return null;
        }
        for( IService service : services ) {
            IResolve resolve;
            IGeoResource geoR;
            try {
                resolve = service.members(null).get(0);
                geoR = resolve.resolve(IGeoResource.class, new NullProgressMonitor());
                return geoR;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}