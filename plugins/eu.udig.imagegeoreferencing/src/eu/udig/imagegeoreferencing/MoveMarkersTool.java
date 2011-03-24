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

import java.awt.Point;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.render.glass.GlassPane;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;

/**
 * This tool allows the user to move placemarkers.  A glasspane is used to draw
 * the point as you drag it.  If the user clicks and holds
 * on a placemarker, they can drag it around the map and release the button
 * to drop it.
 * 
 * If multiple markers are in the vicinity of the click, it will select the closest
 * one.  If the placemarker is an image one (not a basemap one) it will restrict
 * the movements to within the image bounds.
 * 
 * @author GDavis, Refractions Research
 *
 */
public class MoveMarkersTool extends AbstractModalTool implements ModalTool {

    public static final String TOOLID = "net.refractions.udig.imagegeoreferencing.moveMarkersTool"; //$NON-NLS-1$

    private boolean dragging = false;
    private Point mouseStart = null;

    private GeoReferenceMapGraphic activeMapGraphic = null;
    private GeoReferenceGlassPane activeGlassPane = null;
    private PlaceMarker draggingPlaceMarker = null;

    public MoveMarkersTool() {
        super(MOUSE | MOTION);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseDragged( MapMouseEvent e ) {
        // update the glasspane if dragging
        if (dragging && activeGlassPane != null && draggingPlaceMarker != null) {
            Point point = null;
            if (draggingPlaceMarker.isBasemapMarker() && draggingPlaceMarker.getCoord() != null) {
                point = getContext().worldToPixel(draggingPlaceMarker.getCoord());
            } else {
                point = draggingPlaceMarker.getPoint();
            }
            int x = point.x + (e.x - mouseStart.x);
            int y = point.y + (e.y - mouseStart.y);
            // make sure any image markers stay within the bounds of the image
            Point validatedPoint = new Point(x, y);
            if (!draggingPlaceMarker.isBasemapMarker() && draggingPlaceMarker.getImage() != null) {
                GeoReferenceImage image = draggingPlaceMarker.getImage();
                validatedPoint = validateImagePoint(image, x, y);
            }
            activeGlassPane.setDragTranslation(validatedPoint.x, validatedPoint.y);
            context.getViewportPane().repaint();
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

        // set the active objects and also force a redraw of
        // the composed map image before panning so that we can
        // remove the placemarker being dragged
        List<ILayer> mapLayers = getContext().getMap().getMapLayers();
        Iterator<ILayer> iterator = mapLayers.iterator();
        while( iterator.hasNext() ) {
            ILayer layer = iterator.next();
            GeoReferenceMapGraphic mapGraphic;
            try {
                mapGraphic = layer.getResource(GeoReferenceMapGraphic.class, null);
                if (mapGraphic != null) {
                    activeMapGraphic = mapGraphic;
                    GlassPane glass = getContext().getViewportPane().getGlass();
                    if (glass instanceof GeoReferenceGlassPane) {
                        activeGlassPane = (GeoReferenceGlassPane) glass;
                    }
                    layer.refresh(null); // refresh to ensure the selected graphic disappears
                    break;
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        // determine if the user clicked on a placemarker or not. If multiple
        // placemarkers are clicked on, choose the closest one to the click
        draggingPlaceMarker = null;
        if (activeMapGraphic != null) {
            Point point = e.getPoint();
            List<PlaceMarker> imageMarkers = activeMapGraphic.getImageMarkers();
            List<PlaceMarker> basemapMarkers = activeMapGraphic.getBasemapMarkers();
            if (imageMarkers == null || imageMarkers.size() < 1) {
                return;
            }
            double currentdistance = PlaceMarker.DRAWING_SIZE + 1;
            for( PlaceMarker marker : imageMarkers ) {
                Point markerPoint = marker.getPoint();
                if (markerPoint != null) {
                    double distance = point.distance(markerPoint.x, markerPoint.y);
                    if (distance <= PlaceMarker.DRAWING_SIZE && distance < currentdistance) {
                        draggingPlaceMarker = marker;
                        currentdistance = distance;
                    }
                }
            }

            // check basemap markers if we haven't matched one yet
            if (draggingPlaceMarker == null && basemapMarkers != null) {
                for( PlaceMarker marker : basemapMarkers ) {
                    // compare the map coordinate not the screen point since
                    // the screen point could be out of whack if the user
                    // panned/zoomed the map
                    Coordinate markerCoord = marker.getCoord();
                    if (markerCoord != null) {
                        Point markerPoint = getContext().worldToPixel(markerCoord);
                        if (markerPoint != null) {
                            double distance = point.distance(markerPoint.x, markerPoint.y);
                            if (distance <= PlaceMarker.DRAWING_SIZE && distance < currentdistance) {
                                draggingPlaceMarker = marker;
                                currentdistance = distance;
                            }
                        }
                    }
                }
            }
        }

        // did we find a marker?
        if (draggingPlaceMarker == null) {
            return;
        }

        // marker found, set dragging vars
        dragging = true;
        draggingPlaceMarker.setDragging(true);
        mouseStart = e.getPoint();
        if (activeGlassPane != null && draggingPlaceMarker != null) {
            Point point = null;
            if (draggingPlaceMarker.isBasemapMarker() && draggingPlaceMarker.getCoord() != null) {
                point = getContext().worldToPixel(draggingPlaceMarker.getCoord());
            } else {
                point = draggingPlaceMarker.getPoint();
            }
            activeGlassPane.startDraggingMarker(draggingPlaceMarker, point.x, point.y);
        }
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
            GeoReferenceUtils.rotateToNextTool(MoveMarkersTool.TOOLID);
            return false;
        }
        return e.buttons == MapMouseEvent.BUTTON1 && !(e.modifiersDown());
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseReleased( MapMouseEvent e ) {
        // turn dragging off
        if (dragging) {
            if (activeGlassPane != null) {
                Point point = null;
                if (draggingPlaceMarker.isBasemapMarker() && draggingPlaceMarker.getCoord() != null) {
                    point = getContext().worldToPixel(draggingPlaceMarker.getCoord());
                } else {
                    point = draggingPlaceMarker.getPoint();
                }
                int x = point.x + (e.x - mouseStart.x);
                int y = point.y + (e.y - mouseStart.y);
                // make sure any image markers stay within the bounds of the image
                Point validatedPoint = new Point(x, y);
                if (!draggingPlaceMarker.isBasemapMarker() && draggingPlaceMarker.getImage() != null) {
                    GeoReferenceImage image = draggingPlaceMarker.getImage();
                    validatedPoint = validateImagePoint(image, x, y);
                }
                activeGlassPane.endDraggingMarker(validatedPoint.x, validatedPoint.y);
            }

            // clear any events before we try to pan. This dramatically reduces the number
            // of images drawn to the screen in the wrong spot
            ((ViewportPane) getContext().getMapDisplay()).update();

            dragging = false;
            if (draggingPlaceMarker != null) {
                draggingPlaceMarker.setDragging(false);
            }
        }

        // refresh the layer
        if (activeMapGraphic != null) {
            activeMapGraphic.getLayer(getContext().getMap()).refresh(null); // refresh to ensure the
                                                                            // dragged placemarker
                                                                            // reappears
        }

    }

    /**
     * Ensure the given x and y coordinates are within the image's bounds
     *
     * @param image
     * @param x
     * @param y
     * @return
     */
    private Point validateImagePoint( GeoReferenceImage image, int x, int y ) {
        if (image == null)
            return null;
        if (x > image.getPosX() + image.getScaledWidth()) {
            x = image.getPosX() + image.getScaledWidth();
        } else if (x < image.getPosX()) {
            x = image.getPosX();
        }
        if (y > image.getPosY() + image.getScaledHeight()) {
            y = image.getPosY() + image.getScaledHeight();
        } else if (y < image.getPosY()) {
            y = image.getPosY();
        }
        return new Point(x, y);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        super.dispose();
    }

}
