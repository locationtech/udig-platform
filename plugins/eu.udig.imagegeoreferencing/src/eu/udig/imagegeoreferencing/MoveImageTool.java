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

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.render.glass.GlassPane;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;

/**
 * This tool allows the user to move the frame of the selected geoImage around the map.
 * By using a glasspane to draw the frame overtop of the map, moving an object overtop
 * of the other layers works.
 * 
 * Possible upgrades involve making the basemap (all layers) pan when the user drags the 
 * geoImage towards the corners/sides of the screen (could possibly be done using the 
 * TranslateCommand provided).
 * 
 * @author GDavis, Refractions Research
 *
 */
public class MoveImageTool extends AbstractModalTool implements ModalTool {

    public static final String TOOLID = "net.refractions.udig.imagegeoreference.tools.moveImageTool"; //$NON-NLS-1$

    private boolean dragging = false;
    private Point mouseStart = null;
    // private TranslateCommand command;

    private GeoReferenceMapGraphic activeMapGraphic = null;
    private GeoReferenceGlassPane activeGlassPane = null;
    private GeoReferenceImage selectedGeoImage = null;

    public MoveImageTool() {
        super(MOUSE | MOTION);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseDragged( MapMouseEvent e ) {
        // update the glasspane if dragging
        if (dragging && activeGlassPane != null && selectedGeoImage != null) {
            activeGlassPane.setDragTranslation(selectedGeoImage.getPosX() + (e.x - mouseStart.x), selectedGeoImage.getPosY()
                    + (e.y - mouseStart.y));
            context.getViewportPane().repaint();
        }

        // code for translateCommand
        // if (dragging) {
        // command.setTranslation(e.x- start.x, e.y - start.y);
        // context.getViewportPane().repaint();
        // }
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed( MapMouseEvent e ) {
        // validate the mouse click (only left mouse clicks should work for dragging)
        if (!validModifierButtonCombo(e)) {
            return;
        }

        // force a redraw of the composed map image before panning so that we can
        // remove the image on top
        List<ILayer> mapLayers = getContext().getMap().getMapLayers();
        Iterator<ILayer> iterator = mapLayers.iterator();
        while( iterator.hasNext() ) {
            ILayer layer = iterator.next();
            GeoReferenceMapGraphic mapGraphic;
            try {
                mapGraphic = layer.getResource(GeoReferenceMapGraphic.class, null);
                if (mapGraphic != null) {
                    activeMapGraphic = mapGraphic;
                    activeMapGraphic.setDraggingImage(true);
                    GlassPane glass = getContext().getViewportPane().getGlass();
                    if (glass instanceof GeoReferenceGlassPane) {
                        activeGlassPane = (GeoReferenceGlassPane) glass;
                        selectedGeoImage = GeoReferenceUtils.getSelectedGeoImage(activeMapGraphic.getImages(), getContext()
                                .getMap());
                    }
                    layer.refresh(null); // refresh to ensure the selected graphic disappears
                    break;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        dragging = true;
        mouseStart = e.getPoint();
        if (activeGlassPane != null && activeMapGraphic != null && selectedGeoImage != null) {
            activeGlassPane.startDraggingImage(activeMapGraphic, selectedGeoImage.getPosX(), selectedGeoImage.getPosY());
        }

        // code for translateCommand
        // if (validModifierButtonCombo(e)) {
        // //((ViewportPane)context.getMapDisplay()).enableDrawCommands(false);
        // dragging = true;
        // start = e.getPoint();
        // command= new TranslateCommand(0, 0);
        // context.sendASyncCommand(command);
        // }
        // super.mousePressed(e);
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
            GeoReferenceUtils.rotateToNextTool(MoveImageTool.TOOLID);
            return false;
        }
        return e.buttons == MapMouseEvent.BUTTON1 && !(e.modifiersDown());
    }

    /*
     * Update any mapgraphic image points to correspond to the moved image
     */
    private void updateMapGraphicPoints( IMap map, int xMovement, int yMovement ) {
        GeoReferenceMapGraphic mapGraphic = GeoReferenceUtils.getMapGraphic(map);
        if (mapGraphic != null) {
            List<PlaceMarker> imageMarkers = mapGraphic.getImageMarkers();
            for( PlaceMarker marker : imageMarkers ) {
                if (marker != null && marker.getPoint() != null) {
                    Point point = marker.getPoint();
                    point.x += xMovement;
                    point.y += yMovement;
                    marker.setPoint(point);
                }
            }
        }
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseReleased( MapMouseEvent e ) {
        // turn dragging off in the map's mapgraphic
        if (activeMapGraphic != null) {
            activeMapGraphic.setDraggingImage(false);
            activeMapGraphic.getLayer(getContext().getMap()).refresh(null); // refresh to ensure the
                                                                            // selected graphic
                                                                            // reappears
        }

        if (dragging) {

            if (activeGlassPane != null && selectedGeoImage != null) {
                activeGlassPane.endDraggingImage(selectedGeoImage.getPosX() + (e.x - mouseStart.x), selectedGeoImage.getPosY()
                        + (e.y - mouseStart.y));
                updateMapGraphicPoints(activeGlassPane.getSite().getMap(), (e.x - mouseStart.x), (e.y - mouseStart.y));
            }

            // clear any events before we try to pan. This dramatically reduces the number
            // of images drawn to the screen in the wrong spot
            ((ViewportPane) getContext().getMapDisplay()).update();

            dragging = false;
        }

        // code for translateCommand
        // if (dragging) {
        // ((ViewportPane)context.getMapDisplay()).enableDrawCommands(true);
        // Point end=e.getPoint();
        // NavigationCommandFactory r = context.getNavigationFactory();
        // NavCommand finalPan = new PanCommand((start.x-end.x), (start.y-end.y));
        //
        // //clear any events before we try to pan. This dramatically reduces the number
        // //of images drawn to the screen in the wrong spot
        // ((ViewportPane) getContext().getMapDisplay()).update();
        //
        // context.sendASyncCommand(new PanAndInvalidate(finalPan, command));
        //
        // dragging = false;
        //
        // }
    }

    /**
     * @see net.refractions.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        super.dispose();
    }

    // /**
    // * Executes the specified pan command, and only after it is executed, expires the last
    // translate command
    // */
    // private class PanAndInvalidate implements Command, NavCommand {
    //
    // private NavCommand command;
    // private TranslateCommand expire;
    //
    // PanAndInvalidate(NavCommand command, TranslateCommand expire) {
    // this.command = command;
    // this.expire = expire;
    // }
    //
    // public Command copy() {
    // return new PanAndInvalidate(command, expire);
    // }
    //
    // public String getName() {
    // return "PanAndDiscard";
    // }
    //
    // public void run( IProgressMonitor monitor ) throws Exception {
    // //we need to expire the translate command first otherwise
    // //the image gets drawn in the wrong spot the first time
    // // and we see weird affects
    // expire.setValid(false);
    //
    // command.run(monitor);
    //
    // }
    //
    // public void setViewportModel( ViewportModel model ) {
    // command.setViewportModel(model);
    // }
    //
    // public Map getMap() {
    // return command.getMap();
    // }
    //
    // public void setMap( IMap map ) {
    // command.setMap(map);
    // }
    //
    // public void rollback( IProgressMonitor monitor ) throws Exception {
    // command.rollback(monitor);
    // }
    //
    // }
}
