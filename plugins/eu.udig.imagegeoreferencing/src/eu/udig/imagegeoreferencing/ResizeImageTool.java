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
import net.refractions.udig.project.ui.tool.Tool;

/**
 * This tool allows the user to resize the frame of the selected geoImage on the map.
 * By using a glasspane to draw the frame overtop of the map, resizing an object overtop
 * of the other layers works.
 * 
 * @author GDavis, Refractions Research
 *
 */
public class ResizeImageTool extends AbstractModalTool implements ModalTool {

    public static final String TOOLID = "net.refractions.udig.imagegeoreference.tools.resizeImageTool"; //$NON-NLS-1$

    private boolean resizing = false;

    private GeoReferenceMapGraphic activeMapGraphic = null;
    private GeoReferenceGlassPane activeGlassPane = null;
    private GeoReferenceImage selectedGeoImage = null;

    public ResizeImageTool() {
        super(MOUSE | MOTION);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseDragged( MapMouseEvent e ) {
        // update the glasspane if resizing
        if (resizing && activeGlassPane != null && selectedGeoImage != null) {
            activeGlassPane.setResizeTranslation(e.x, e.y);
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

        // resizing, so remove any current image markers
        IMap map = getContext().getMap();
        deleteMapGraphicPoints(map);

        // force a redraw of the composed map image before panning so that we can
        // remove the image on top
        List<ILayer> mapLayers = map.getMapLayers();
        Iterator<ILayer> iterator = mapLayers.iterator();
        while( iterator.hasNext() ) {
            ILayer layer = iterator.next();
            GeoReferenceMapGraphic mapGraphic;
            try {
                mapGraphic = layer.getResource(GeoReferenceMapGraphic.class, null);
                if (mapGraphic != null) {
                    activeMapGraphic = mapGraphic;
                    activeMapGraphic.setResizingImage(true);
                    GlassPane glass = getContext().getViewportPane().getGlass();
                    if (glass instanceof GeoReferenceGlassPane) {
                        activeGlassPane = (GeoReferenceGlassPane) glass;
                        selectedGeoImage = GeoReferenceUtils.getSelectedGeoImage(activeMapGraphic.getImages(), map);
                    }
                    layer.refresh(null); // refresh to ensure the selected graphic disappears
                    break;
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        resizing = true;
        if (activeGlassPane != null && activeMapGraphic != null && selectedGeoImage != null) {
            // activeGlassPane.startResizing(activeMapGraphic, selectedGeoImage.getPosX(),
            // selectedGeoImage.getPosY());
            activeGlassPane.startResizingImage(activeMapGraphic, e.x, e.y);
        }

    }

    /*
     * Delete any mapgraphic points if resizing the image
     */
    private void deleteMapGraphicPoints( IMap map ) {
        Tool placeMarkersTool = GeoReferenceUtils.findTool(PlaceMarkersTool.TOOLID);
        if (placeMarkersTool != null && placeMarkersTool instanceof PlaceMarkersTool) {
            ((PlaceMarkersTool) placeMarkersTool).clearPointVars();
        }

        // GeoReferenceMapGraphic mapGraphic = GeoReferenceUtils.getMapGraphic(map);
        // if (mapGraphic != null) {
        // if (mapGraphic.getImageMarkers() != null) {
        // mapGraphic.getImageMarkers().clear();
        // }
        // if (mapGraphic.getBasemapMarkers() != null) {
        // mapGraphic.getBasemapMarkers().clear();
        // }
        // }
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
            GeoReferenceUtils.rotateToNextTool(ResizeImageTool.TOOLID);
            return false;
        }
        return e.buttons == MapMouseEvent.BUTTON1 && !(e.modifiersDown());
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseReleased( MapMouseEvent e ) {
        // turn resizing off in the map's mapgraphic
        if (activeMapGraphic != null) {
            activeMapGraphic.setResizingImage(false);
            activeMapGraphic.getLayer(getContext().getMap()).refresh(null); // refresh to ensure the
                                                                            // selected graphic
                                                                            // reappears
        }

        if (resizing) {

            if (activeGlassPane != null) {
                activeGlassPane.endResizingImage(e.x, e.y);
            }

            // clear any events before we try to pan. This dramatically reduces the number
            // of images drawn to the screen in the wrong spot
            ((ViewportPane) getContext().getMapDisplay()).update();

            resizing = false;
        }

    }

    /**
     * @see net.refractions.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        super.dispose();
    }
}
