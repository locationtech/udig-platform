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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.udig.imagegeoreferencing.i18n.Messages;

/**
 * This tool will pop-up a dialog to load a new image onto the map.  It will
 * be loaded onto a geoRef mapgraphic (which will be created if it is not yet).  A
 * glasspane will also be created (if it is not yet) to use for moving the frame of
 * the image around ontop of the map and for moving placemarkers as well.
 * 
 * @author GDavis, Refractions Research
 *
 */
public class LoadImageTool extends AbstractModalTool implements ModalTool {

    public static final String TOOLID = "net.refractions.udig.imagegeoreference.tools.loadImageTool"; //$NON-NLS-1$

    /** determines if we are using SWT or AWT images **/
    private final static boolean USESWT = false;

    /** each uDig map should have only one geo imaging mapgraphic layer **/
    private HashMap<IMap, ILayer> mapGeoRefLayers = new HashMap<IMap, ILayer>();

    /** each uDig map will have a list of geo referencing images **/
    private HashMap<IMap, HashMap<String, GeoReferenceImage>> mapImages = new HashMap<IMap, HashMap<String, GeoReferenceImage>>();

    /** each uDig map will have a glasspane for use when moving the map's selected image
     * frame overtop of the map for placemenent.
     */
    private HashMap<IMap, GeoReferenceGlassPane> mapGlassPanes = new HashMap<IMap, GeoReferenceGlassPane>();

    public LoadImageTool() {
        this(MOUSE);
    }

    public LoadImageTool( int targets ) {
        super(targets);
    }

    /**
     * Look through the GeoImage hashmap and see if the given filename is there for
     * the given map.  If it is,
     * return the matching GeoReferenceImage.  Otherwise return null.
     * 
     * @param filename
     * @return matching GeoReferenceImage, or null
     */
    public GeoReferenceImage findImageInMap( IMap map, String filename ) {
        HashMap<String, GeoReferenceImage> geoImages = mapImages.get(map);
        if (geoImages == null) {
            return null;
        }
        for( Iterator<Entry<String, GeoReferenceImage>> iterator = geoImages.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<String, GeoReferenceImage> entry = (Entry<String, GeoReferenceImage>) iterator.next();
            GeoReferenceImage geoImage = entry.getValue();
            if (filename.equals(geoImage.getFilename())) {
                return geoImage;
            }
        }
        return null;
    }

    /**
     * Opens a file dialog to select an image to load onto the given map as
     * a geo referencing image mapgraphic.
     * 
     * @param map
     */
    private void loadImageOnMap( IMap map ) {
        String filename = openImageDialog();
        if (filename != null && !filename.equals("")) { //$NON-NLS-1$
            LoadImageOnMap imageLoader = new LoadImageOnMap(filename, map, this, USESWT);

            // load the image onto the map with a progress dialog
            PlatformGIS.runInProgressDialog("Loading Image", false, imageLoader, false); //$NON-NLS-1$

            // if loading failed then show error dialog
            if (imageLoader.isLoadingError()) {
                showErrorDialog();
            }
        }
    }

    public HashMap<IMap, ILayer> getMapGeoRefLayers() {
        return mapGeoRefLayers;
    }

    public HashMap<IMap, HashMap<String, GeoReferenceImage>> getMapImages() {
        return mapImages;
    }

    public HashMap<IMap, GeoReferenceGlassPane> getGlassPanes() {
        return mapGlassPanes;
    }

    /**
     * Unselect every geo image on the given map except for the given one, which will be
     * explicitly selected.
     * 
     * @param geoImage
     */
    public void selectGeoRefImage( IMap map, GeoReferenceImage geoImage ) {
        HashMap<String, GeoReferenceImage> geoImages = mapImages.get(map);
        if (geoImages == null) {
            return;
        }
        for( Iterator<Entry<String, GeoReferenceImage>> iterator = geoImages.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<String, GeoReferenceImage> entry = (Entry<String, GeoReferenceImage>) iterator.next();
            GeoReferenceImage thisGeoImage = entry.getValue();
            if (thisGeoImage == geoImage) {
                thisGeoImage.setSelected(true);
            } else {
                thisGeoImage.setSelected(false);
            }
        }
    }

    /**
     * Open a file dialog and return the selected image file name
     * 
     * @return image file name
     */
    private String openImageDialog() {
        String file = null;
        try {
            FileDialog openDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
            // only show image formats that are supported
            String[] supportedSuffixes = ImageIO.getReaderFileSuffixes();
            int index = 0;
            String allFormats = ""; //$NON-NLS-1$
            ArrayList<String> exts = new ArrayList<String>();
            for( String suffix : supportedSuffixes ) {
                if (!suffix.equals("")) { //$NON-NLS-1$
                    if (index > 0) {
                        allFormats += ";"; //$NON-NLS-1$
                    }
                    allFormats += "*." + suffix; //$NON-NLS-1$
                    suffix = "*." + suffix; //$NON-NLS-1$
                    exts.add(suffix);
                    index++;
                }
            }
            exts.add(0, allFormats);
            String[] extsArr = new String[exts.size() - 1];
            extsArr = exts.toArray(extsArr);
            openDialog.setFilterExtensions(extsArr);
            openDialog.setFilterNames(new String[]{"All supported formats " + exts.toString()}); //$NON-NLS-1$
            file = openDialog.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
        if (active) {
            // load an image on the active map
            IMap activeMap = ApplicationGIS.getActiveMap();
            loadImageOnMap(activeMap);
        }
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

    /**
     * Show the error dialog
     */
    private void showErrorDialog() {
        try {
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
                    descLabel.setText(Messages.LoadImageError_desc);
                    layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
                    layoutData.verticalSpan = 1;
                    layoutData.horizontalSpan = 1;
                    descLabel.setLayoutData(layoutData);

                    return container;
                }

                @Override
                protected void configureShell( Shell newShell ) {
                    super.configureShell(newShell);
                    newShell.setText(Messages.LoadImageError_title);
                    GridLayout layout = new GridLayout();
                    layout.numColumns = 1;
                    newShell.setLayout(layout);
                }
            };
            dialog.setBlockOnOpen(true);
            dialog.open();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

/**
 * Loads the given file name into an image and loads it onto the map.
 * 
 * @author GDavis
 *
 */
final class LoadImageOnMap implements IRunnableWithProgress {

    private String filename;
    private BufferedImage AWTImage;
    private org.eclipse.swt.graphics.Image SWTImage;
    private IMap map;
    private LoadImageTool imageTool;
    private boolean useSWT = false;
    private GeoReferenceImage geoImage;
    private boolean loadingError = true;

    public LoadImageOnMap( String filename, IMap map, LoadImageTool imageTool, boolean useSWT ) {
        this.filename = filename;
        this.map = map;
        this.imageTool = imageTool;
        this.useSWT = useSWT;
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        monitor.beginTask("Loading Image...", IProgressMonitor.UNKNOWN); //$NON-NLS-1$

        // determine if we are loading an SWT or AWT image
        if (this.useSWT) {
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(new File(filename)));
                SWTImage = new org.eclipse.swt.graphics.Image(Display.getCurrent(), bis);
            } catch (Exception e) {
                loadingError = true;
                e.printStackTrace();
            } finally {
                monitor.done();
            }
        } else {
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(new File(filename)));
                AWTImage = ImageIO.read(bis);
                // if this image does not already have alpha transparency settings,
                // then copy it to a new image that does have an alpha color map
                if (AWTImage.getColorModel() != null && !AWTImage.getColorModel().hasAlpha()) {
                    monitor.setTaskName("Converting Image to support Transparency..."); //$NON-NLS-1$
                    AWTImage = createTransparentAWTImage(AWTImage);
                }
                // } catch (IOException e) {
                // e.printStackTrace();
            } catch (Exception e) {
                loadingError = true;
                e.printStackTrace();
            } finally {
                monitor.done();
            }
        }

        // if the mapgraphic layer currently isn't on the map (may have been deleted) then
        // clear the mapImages to avoid any complications
        HashMap<IMap, ILayer> mapGeoRefLayers = imageTool.getMapGeoRefLayers();
        HashMap<IMap, HashMap<String, GeoReferenceImage>> mapImages = imageTool.getMapImages();
        ILayer graphicLayer = mapGeoRefLayers.get(map);
        List<ILayer> mapLayers = map.getMapLayers();
        if ((graphicLayer == null || !mapLayers.contains(graphicLayer)) && !mapImages.isEmpty()) {
            mapImages.clear();
        }

        // create the geoImage and set it
        createGeoImage();

        // set this geoImage to be selected, and unselect others
        imageTool.selectGeoRefImage(map, geoImage);

        // load the geoImage into a mapgraphic
        loadMapGraphic();

        // create the glasspane for this map if it is not yet created
        createGlassPane(map);

        // if we get here then the image was loaded on the map successfully
        loadingError = false;
    }

    private BufferedImage createTransparentAWTImage( Image image ) {
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Copy image to new buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    private void createGlassPane( IMap map ) {
        // create glasspane if it is not yet created
        HashMap<IMap, GeoReferenceGlassPane> glassPanes = imageTool.getGlassPanes();
        GeoReferenceGlassPane glassPane = glassPanes.get(map);
        if (glassPane == null) {
            ViewportPane viewer = (ViewportPane) map.getRenderManager().getMapDisplay();
            glassPane = new GeoReferenceGlassPane(viewer);
            viewer.setGlass(glassPane);
            glassPanes.put(map, glassPane);
        }
    }

    /**
     * Create (or update the currently existing) geoImage for this process and 
     * store it.
     */
    private void createGeoImage() {
        // see if this image already exists on the given map(by filename) and if
        // it does replace it, otherwise add it
        HashMap<IMap, HashMap<String, GeoReferenceImage>> mapImages = imageTool.getMapImages();
        geoImage = imageTool.findImageInMap(map, filename);
        if (geoImage != null) {
            // see if we are using SWT or AWT
            if (useSWT) {
                geoImage.setSWTImage(SWTImage);
            } else {
                // AWT
                geoImage.setAWTImage(AWTImage);
            }
        } else {
            // see if we are using SWT or AWT
            HashMap<String, GeoReferenceImage> geoImages = mapImages.get(map);
            if (geoImages == null) {
                geoImages = new HashMap<String, GeoReferenceImage>();
            }
            if (useSWT) {
                geoImage = new GeoReferenceImage(SWTImage, filename);
                geoImage.createScaledSWTImageToFitMap(map);
                geoImages.put(filename, geoImage);
            } else {
                // AWT
                geoImage = new GeoReferenceImage(AWTImage, filename);
                geoImage.createScaledAWTImageToFitMap(map);
                geoImages.put(filename, geoImage);
            }
            mapImages.put(map, geoImages);
        }
    }

    /**
     * Load this geoImage onto a mapGraphic and store it.
     */
    private void loadMapGraphic() {
        // create the mapgraphic layer for this map if it is not yet created, and
        // recreate it if it was previously removed from the map to avoid
        // complications
        HashMap<IMap, ILayer> mapGeoRefLayers = imageTool.getMapGeoRefLayers();
        HashMap<IMap, HashMap<String, GeoReferenceImage>> mapImages = imageTool.getMapImages();
        ILayer graphicLayer = mapGeoRefLayers.get(map);
        List<ILayer> mapLayers = map.getMapLayers();
        if (graphicLayer == null || !mapLayers.contains(graphicLayer)) {
            // create a new mapgraphic and add it to the map as a layer
            graphicLayer = null;
            List<IResolve> mapgraphics = CatalogPlugin.getDefault().getLocalCatalog().find(MapGraphicService.SERVICE_URL, null);
            List<IResolve> members = new ArrayList<IResolve>();
            try {
                members = mapgraphics.get(0).members(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for( IResolve resolve : members ) {
                if (resolve.canResolve(GeoReferenceMapGraphic.class)) {
                    try {
                        IGeoResource resolved = resolve.resolve(IGeoResource.class, null);
                        ApplicationGIS.addLayersToMap(map, Collections.singletonList(resolved), -1);
                        // System.out.println("added");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Get the newly added mapgraphic layer, add the current images to it, and
            // store it
            mapLayers = map.getMapLayers();
            Iterator<ILayer> iterator = mapLayers.iterator();
            while( iterator.hasNext() ) {
                ILayer layer = iterator.next();
                IGeoResource geoResource = layer.findGeoResource(GeoReferenceMapGraphic.class);
                if (geoResource != null) {
                    try {
                        GeoReferenceMapGraphic mapGraphic = geoResource.resolve(GeoReferenceMapGraphic.class, null);
                        mapGraphic.setImages(mapImages);
                        mapGeoRefLayers.put(map, layer);
                        graphicLayer = layer;
                        // System.out.println("updated");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } // end if mapgraphic layer null

        // ensure the mapgraphic is on the map (the layer may have been removed from the map
        // previously
        mapLayers = map.getMapLayers();
        if (!mapLayers.contains(graphicLayer)) {
            try {
                IGeoResource resolved = graphicLayer.getResource(IGeoResource.class, null);
                ApplicationGIS.addLayersToMap(map, Collections.singletonList(resolved), -1);
                // System.out.println("added");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // redraw so the geo image appears
        if (graphicLayer != null) {
            graphicLayer.refresh(null);
        }
    }

    public GeoReferenceImage getGeoImage() {
        return this.geoImage;
    }

    public boolean isLoadingError() {
        return loadingError;
    }

}