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

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;

/**
 * Place markers on the selected image and similarly on the basemap.
 * 
 * @author GDavis, Refractions Research
 *
 */
public class PlaceMarkersTool extends AbstractModalTool implements ModalTool {

    public static final String TOOLID = "net.refractions.udig.imagegeoreferencing.placeMarkersTool"; //$NON-NLS-1$

    // these lists should have an equal number of placemarkers in the same order
    private List<PlaceMarker> imageMarkers = new ArrayList<PlaceMarker>();
    private List<PlaceMarker> basemapMarkers = new ArrayList<PlaceMarker>();
    public static final int MIN_PLACEMARKERS = 6;
    public static final int MAX_PLACEMARKERS = 100;

    // default to 6 placemarkers for now
    private int placemarkCount = MIN_PLACEMARKERS;

    public static final int WARP_MINDEGREE = 1;
    private int warpValue = WARP_MINDEGREE;
    private boolean removeMarkers = true;

    private boolean placingMarkers = false;
    private int addedMarkerCount = 0;

    private static final Color[] COLORS = {Color.GREEN, Color.BLUE, Color.CYAN, Color.RED, Color.MAGENTA, new Color(128, 0, 128),
            new Color(204, 204, 204), new Color(255, 255, 0), new Color(255, 97, 1), Color.BLACK};

    public PlaceMarkersTool() {
    }

    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
        if (active) {
            // show marker dialog
            showDialog();
        } else {
            // set inactive, so remove markers
            // clearPointVars();

            // refresh the map
            IMap map = ApplicationGIS.getActiveMap();
            GeoReferenceMapGraphic mapGraphic = GeoReferenceUtils.getMapGraphic(map);
            if (mapGraphic != null)
                mapGraphic.getLayer(map).refresh(null);
        }
    }

    /**
     * Clear the point related variables
     */
    public void clearPointVars() {
        imageMarkers.clear();
        basemapMarkers.clear();
        placingMarkers = false;
        addedMarkerCount = 0;
    }

    /**
     * Show the marker placing dialog
     */
    private void showDialog() {
        // show marker dialog
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        MarkerDialog dial = new MarkerDialog(shell, MIN_PLACEMARKERS);
        dial.setBlockOnOpen(true);
        // check if the user canceled
        if (dial.open() == Window.OK && dial.getState() == MarkerDialog.STATE_GOOD) {
            // restart the marker vars
            placingMarkers = true;
            warpValue = dial.getWarpValue();
            removeMarkers = dial.isRemoveMarkers();
            // create placemarkers
            if (removeMarkers) {
                addedMarkerCount = 0;
                createNewMarkers(MIN_PLACEMARKERS);
            }
            updateMapGraphicPoints(ApplicationGIS.getActiveMap());
        } else {
            // canceled
            placingMarkers = false;
            addedMarkerCount = 0;

            // canceled, so switch tools to the move tool
            // there is a bug with this in trunk, check later
            // IToolManager manager = ApplicationGIS.getToolManager();
            //        	IAction action = manager.getToolAction(MoveImageTool.TOOLID, "net.refractions.udig.imagegeoreferencing.tools"); //$NON-NLS-1$
            // action.run();
        }
    }

    /**
     * Create new markers (min amount) and delete any previous ones
     * 
     * @param number
     */
    private void createNewMarkers( int number ) {
        imageMarkers.clear();
        basemapMarkers.clear();
        placemarkCount = number;
        if (placemarkCount > MAX_PLACEMARKERS)
            placemarkCount = MAX_PLACEMARKERS;
        if (placemarkCount < MIN_PLACEMARKERS)
            placemarkCount = MIN_PLACEMARKERS;

        // refresh the map
        IMap map = ApplicationGIS.getActiveMap();
        GeoReferenceMapGraphic mapGraphic = GeoReferenceUtils.getMapGraphic(map);
        if (mapGraphic != null) {
            mapGraphic.getLayer(map).refresh(null);
            // create the new markers
            GeoReferenceImage selectedGeoImage = GeoReferenceUtils.getSelectedGeoImage(mapGraphic.getImages(), map);
            for( int i = 0; i < placemarkCount; i++ ) {
                createAndAddNewMarker(i, selectedGeoImage);
            }
        }
    }

    /**
     * Create and add a new marker for both the imageMarkers and basemapMarkers
     */
    private void createAndAddNewMarker( int index, GeoReferenceImage selectedGeoImage ) {
        Color color = createColor(PlaceMarker.DEFAULT_COLOR, index);
        PlaceMarker marker1 = new PlaceMarker(null, color);
        marker1.setBasemapMarker(false);
        marker1.setImage(selectedGeoImage);
        PlaceMarker marker2 = new PlaceMarker(null, color);
        marker2.setBasemapMarker(true);
        imageMarkers.add(marker1);
        basemapMarkers.add(marker2);
    }

    /**
     * Take a starting color and based on the number, create a new color (0 returns the
     * same color).
     * 
     * @param start
     * @param number
     * @return new Color
     */
    private Color createColor( Color start, int number ) {
        if (number >= COLORS.length) {
            number = number % (COLORS.length);
        }
        return COLORS[number];

        // the following code incrementally selects different shades of colors but
        // it is not very good for more than 6 colors
        // if (number == 0) return start;
        // int baseInc = 200;
        // int inc = baseInc * number;
        // int maxColorValue = 255;
        // if (inc < 0) inc = 0;
        // if (inc > maxColorValue) {
        // inc = inc % maxColorValue;
        // }
        // int red = start.getRed();
        // int green = start.getGreen();
        // int blue = start.getBlue();
        //
        // // change the high value to the next of 3 colors
        // if (number == 1 || number%4 == 0) { // red
        // red = 0;
        // green += inc;
        // blue = baseInc;
        // }
        // else if (number%2 == 0) { // green
        // red = baseInc;
        // green = 0;
        // blue += inc;
        // }
        // else { // blue
        // red += inc;
        // green = baseInc;
        // blue = 0;
        // }
        //
        // // check if values are too high, and keep rotating them until they are valid
        // int count = 0;
        // while (red > maxColorValue || green > maxColorValue || blue > maxColorValue) {
        // count++;
        // if (red > maxColorValue) {
        // int rem = red % maxColorValue;
        // red = rem;
        // green += inc;
        // }
        // if (green > maxColorValue) {
        // int rem = green % maxColorValue;
        // green = rem;
        // blue += inc;
        // }
        // if (blue > maxColorValue) {
        // int rem = blue % maxColorValue;
        // blue = rem;
        // red += inc;
        // }
        // if (count > 10) {
        // red = red % maxColorValue;
        // green = green % maxColorValue;
        // blue = blue % maxColorValue;
        // }
        // }
        //
        // return new Color(red, green, blue);
    }

    /**
     * Update the points to draw on the mapgraphic (for both the image and basemap)
     * 
     * @param map
     */
    private void updateMapGraphicPoints( IMap map ) {
        GeoReferenceMapGraphic mapGraphic = GeoReferenceUtils.getMapGraphic(map);
        if (mapGraphic != null) {
            mapGraphic.setImageMarkers(imageMarkers);
            mapGraphic.setBasemapMarkers(basemapMarkers);
            mapGraphic.setWarpValue(warpValue);
        }
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed( MapMouseEvent e ) {
        // validate the mouse click (only left mouse clicks should work for dragging)
        if (!validModifierButtonCombo(e)) {
            return;
        }

        // if we aren't in marker placing mode, then pop-up the dialog
        if (!placingMarkers) {
            showDialog();
            return;
        }

        // if we are placing markers, then update the current marker and count
        IMap map = ApplicationGIS.getActiveMap();
        GeoReferenceMapGraphic mapGraphic = GeoReferenceUtils.getMapGraphic(map);
        GeoReferenceImage selectedGeoImage = GeoReferenceUtils.getSelectedGeoImage(mapGraphic.getImages(), map);
        if (selectedGeoImage == null)
            return;

        // if we are updating an image marker (odd number) and the click was not
        // within the currently active image, do nothing.
        int scaledWidth = selectedGeoImage.getScaledWidth();
        int scaledHeight = selectedGeoImage.getScaledHeight();
        int posX = selectedGeoImage.getPosX();
        int posY = selectedGeoImage.getPosY();
        if ((addedMarkerCount + 1) % 2 == 1
                && !(e.x >= posX && e.x <= (posX + scaledWidth) && e.y >= posY && e.y <= (posY + scaledHeight))) {
            return;
        }

        // update next marker
        addedMarkerCount++;
        // if the max markers is reached, do nothing
        if (addedMarkerCount > MAX_PLACEMARKERS * 2) { // placemarkCount*2 ) {
            return;
            // placingMarkers = false;
            // addedMarkerCount = 0;
        } else if (addedMarkerCount % 2 == 1) {
            // image marker
            int index = addedMarkerCount / 2;
            if (index >= imageMarkers.size()) {
                // create new marker and add it
                createAndAddNewMarker(imageMarkers.size(), selectedGeoImage);
            }
            PlaceMarker placeMarker = imageMarkers.get(index);
            placeMarker.setPoint(e.getPoint());
        } else {
            // basemap marker
            int index = (addedMarkerCount / 2) - 1;
            PlaceMarker placeMarker = basemapMarkers.get(index);
            Point point = e.getPoint();
            placeMarker.setPoint(point);
            // also set the map coord of this point since it is a basemap marker
            placeMarker.setCoord(map.getViewportModel().pixelToWorld(point.x, point.y));
        }

        // refresh the map
        mapGraphic.getLayer(map).refresh(null);

        /*
        // if that was the last marker, reset the status and begin the warp process
        if ( addedMarkerCount > MAX_PLACEMARKERS*2 ) {  //placemarkCount*2 ) {
        	placingMarkers = false;
        	addedMarkerCount = 0;
        	warpImage(map, selectedGeoImage);
        }
        */
    }

    /**
     * First checks if the user clicked the right mouse button, and if they did then
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
            GeoReferenceUtils.rotateToNextTool(PlaceMarkersTool.TOOLID);
            return false;
        }
        return e.buttons == MapMouseEvent.BUTTON1 && !(e.modifiersDown());
    }

}
