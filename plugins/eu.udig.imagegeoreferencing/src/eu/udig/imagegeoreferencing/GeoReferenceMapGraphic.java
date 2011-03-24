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
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.swt.graphics.RGB;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.commands.DeleteLayerCommand;
import net.refractions.udig.project.internal.impl.MapImpl;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.Tool;
import net.refractions.udig.ui.graphics.ViewportGraphics;

/**
 * This mapgraphic is used for drawing the geoImages and placemarkers while they are
 * static (not being dragged or resized).
 * 
 * @author GDavis, Refractions Research
 * @since 1.1.0
 */
public class GeoReferenceMapGraphic implements MapGraphic {

    private HashMap<IMap, HashMap<String, GeoReferenceImage>> mapImages = null;
    private boolean isDraggingImage = false;
    private boolean isResizingImage = false;
    private LoadImageTool loadImageTool = null;
    final static int BORDERWIDTH = 3;
    final static RGB BORDERRGB = new RGB(255, 0, 0);

    private List<PlaceMarker> imageMarkers = new ArrayList<PlaceMarker>();
    private List<PlaceMarker> basemapMarkers = new ArrayList<PlaceMarker>();
    private int warpValue = PlaceMarkersTool.WARP_MINDEGREE;

    public GeoReferenceMapGraphic() {
        // load the tool
        loadImageTool();
    }

    public GeoReferenceMapGraphic( LoadImageTool loadImageTool ) {
        this.loadImageTool = loadImageTool;
        this.mapImages = loadImageTool.getMapImages();
    }

    /**
     * Try loading the image tool
     */
    private void loadImageTool() {
        Tool tool = ApplicationGIS.getToolManager().findTool(LoadImageTool.TOOLID);
        if (tool != null) {
            try {
                loadImageTool = (LoadImageTool) tool;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setImages( HashMap<IMap, HashMap<String, GeoReferenceImage>> mapImages ) {
        this.mapImages = mapImages;
    }

    public HashMap<IMap, HashMap<String, GeoReferenceImage>> getImages() {
        return this.mapImages;
    }

    /**
     * Try setting the mapImages from the LoadImageTool
     * 
     * @param map
     */
    private void loadMapImagesFromTool( IMap map ) {
        // if the mapImages have not yet been set, try getting them from the loadimagetool
        if (mapImages == null && loadImageTool != null) {
            try {
                ILayer layer = loadImageTool.getMapGeoRefLayers().get(map);
                if (layer != null) {
                    IGeoResource geoResource = layer.findGeoResource(GeoReferenceMapGraphic.class);
                    if (geoResource != null) {
                        GeoReferenceMapGraphic mapGraphic = geoResource.resolve(GeoReferenceMapGraphic.class, null);
                        if (!mapGraphic.equals(this)) {
                            // mapgraphic layer already exists, remove this one
                            removeFromMapAndDelete(map, layer);
                            return;
                        }
                    }
                }
                // mapgraphic layer does not already exist, so set the geoimages on this one
                setImages(loadImageTool.getMapImages());
                // System.out.println("images loaded from LoadImageTool");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Validates this mapgraphic (if there is already another mapgraphic for geo images for
     * this map, or this mapgraphic has empty images then it is invalid.
     * 
     * @param map
     * @param thisLayer
     * @return true if valid, false otherwise
     */
    private boolean validateMapGraphic( IMap map, ILayer thisLayer ) {
        // if the images are null or are empty for this map, or this mapgraphic layer
        // is not the only geo image mapgrahic then return false
        if (loadImageTool == null || map == null || thisLayer == null)
            return false;
        if (mapImages == null || mapImages.isEmpty() || mapImages.get(map) == null) {
            return false;
        }

        // Get the count of geo image mapgraphics on this map (should not be more than 1)
        List<ILayer> mapLayers = map.getMapLayers();
        Iterator<ILayer> iterator = mapLayers.iterator();
        int geoLayerCount = 0;
        while( iterator.hasNext() ) {
            ILayer layer = iterator.next();
            IGeoResource geoResource = layer.findGeoResource(GeoReferenceMapGraphic.class);
            if (geoResource != null) {
                geoLayerCount++;
            }
        }

        // if this layer not set as the map's mapgrahic at this point, it is invalid.
        ILayer layer = loadImageTool.getMapGeoRefLayers().get(map);
        if (geoLayerCount > 1 && layer != null && !layer.equals(thisLayer)) {
            return false;
        }

        return true;
    }

    public void draw( MapGraphicContext context ) {
        // ensure this mapgraphic is the topmost layer (in case any new layers
        // have since been added.. This is the quick/dirty way to do this rather than
        // setting up a listener to listen to all map events and determine if something
        // changed/moved.
        IMap map = context.getMap();
        if (map instanceof MapImpl) {
            MapImpl mapImpl = (MapImpl) map;
            ILayer layer = getLayer(map);
            if (layer instanceof Layer) {
                Layer layer2 = (Layer) layer;
                while( layer2.getZorder() < (map.getMapLayers().size() - 1) ) {
                    mapImpl.raiseLayer(layer2);
                }
            }
        }

        // if the loadImageTool is not set for this mapgraphic,
        // let's remove it from the map and delete it (there should never be an empty
        // geoimagemapgraphic if it was created properly). It is possible the user
        // dragged the mapgraphic onto the map, or reloaded uDig and the mapgraphic
        // remains but is empty and needs to be removed.
        if (loadImageTool == null) {
            removeFromMapAndDelete(map, context.getLayer());
            return;
        }

        // if the mapImages have not yet been set, try getting them from the loadimagetool
        loadMapImagesFromTool(map);

        // if the images are still null or are empty for this map, or this mapgraphic layer
        // is not the only geo image mapgrahic then delete this
        // mapgraphic from the map
        if (!validateMapGraphic(map, context.getLayer())) {
            removeFromMapAndDelete(map, context.getLayer());
            return;
        }

        // start rendering
        ViewportGraphics graphics = context.getGraphics();

        // render any basemap markers
        int count = 0;
        for( PlaceMarker marker : basemapMarkers ) {
            count++;
            // don't draw markers that are being dragged as they are rendered on
            // the glasspane layer
            if (!marker.isDragging()) {
                graphics.setColor(marker.getColor());
                // get the map coordinate and
                // convert that to a screen point to draw (this way anytime
                // the map is updated with zooming or panning, etc, the point
                // will always be drawn relative to the map and not the screen.
                java.awt.Point point = null;
                if (marker.getCoord() != null) {
                    point = context.worldToPixel(marker.getCoord());
                } else {
                    point = marker.getPoint();
                }
                if (point != null) {
                    int halfsize = PlaceMarker.DRAWING_SIZE / 2;
                    graphics.fillOval((int) (point.getX() - halfsize), (int) (point.getY() - halfsize), PlaceMarker.DRAWING_SIZE,
                            PlaceMarker.DRAWING_SIZE);
                    Rectangle2D stringBounds = graphics.getStringBounds(String.valueOf(count));
                    graphics.drawString(String.valueOf(count), (int) point.getX(),
                            (int) (point.getY() + stringBounds.getHeight()), ViewportGraphics.ALIGN_MIDDLE,
                            ViewportGraphics.ALIGN_BOTTOM);
                }
            }
        }

        // render each geo image for the current map
        HashMap<String, GeoReferenceImage> images = mapImages.get(map);
        for( Iterator<Entry<String, GeoReferenceImage>> iterator = images.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<String, GeoReferenceImage> entry = (Entry<String, GeoReferenceImage>) iterator.next();
            GeoReferenceImage geoImage = entry.getValue();

            // use the scaled image if there is one
            Image image = geoImage.getScaledAWTImage();

            if (image != null) {
                try {
                    int offsetx = geoImage.getPosX();
                    int offsety = geoImage.getPosY();

                    // if this is the selected image and we are dragging/resizing then
                    // don't draw it as normal. Otherwise if this is the selected
                    // image and we are not dragging/resizing, then draw it with a selection
                    // box around it.
                    if ((isDraggingImage || isResizingImage) && geoImage.isSelected()) {
                        continue;
                    } else {
                        graphics.drawImage(image, offsetx, offsety);
                    }

                    if (geoImage.isSelected()) {
                        graphics.setColor(new Color(BORDERRGB.red, BORDERRGB.green, BORDERRGB.blue));
                        graphics.setLineWidth(BORDERWIDTH);
                        graphics.drawRect(offsetx, offsety, image.getWidth(null), image.getHeight(null));
                    }

                    // render any image markers as long as no image is being dragged
                    // or resized (we could reach here because placemarkers for the
                    // selected image would still be drawn if an unselected image is
                    // present).
                    count = 0;
                    if (!(isDraggingImage || isResizingImage)) {
                        for( PlaceMarker marker : imageMarkers ) {
                            count++;
                            // don't draw markers that are being dragged as they are
                            // rendered on the glasspane layer
                            if (!marker.isDragging()) {
                                graphics.setColor(marker.getColor());
                                java.awt.Point point = marker.getPoint();
                                if (point != null) {
                                    int halfsize = PlaceMarker.DRAWING_SIZE / 2;
                                    graphics.fillOval((int) (point.getX() - halfsize), (int) (point.getY() - halfsize),
                                            PlaceMarker.DRAWING_SIZE, PlaceMarker.DRAWING_SIZE);
                                    Rectangle2D stringBounds = graphics.getStringBounds(String.valueOf(count));
                                    graphics.drawString(String.valueOf(count), (int) point.getX(),
                                            (int) (point.getY() + stringBounds.getHeight()), ViewportGraphics.ALIGN_MIDDLE,
                                            ViewportGraphics.ALIGN_BOTTOM);
                                }
                            }
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

    }

    /**
     * Remove this mapgraphic from the map and delete it
     */
    private void removeFromMapAndDelete( IMap map, ILayer removeLayer ) {
        if (map == null || removeLayer == null)
            return;

        // remove the entry from the map geoimages if this layer is
        // the entry for layers (otherwise leave it because another layer exists
        // for this map).
        if (loadImageTool != null && mapImages != null && loadImageTool.getMapGeoRefLayers() != null) {
            HashMap<IMap, ILayer> mapGeoRefLayers = loadImageTool.getMapGeoRefLayers();
            ILayer layer = mapGeoRefLayers.get(map);
            if (layer != null && layer.equals(removeLayer)) {
                mapImages.remove(map);
            }
        }

        // remove any mapmarkers
        deleteMapGraphicPoints(map);

        // remove the mapgraphic layer from the map
        List<ILayer> mapLayers = map.getMapLayers();
        Iterator<ILayer> iterator = mapLayers.iterator();
        while( iterator.hasNext() ) {
            ILayer layer = iterator.next();
            IGeoResource geoResource = layer.findGeoResource(GeoReferenceMapGraphic.class);
            if (geoResource != null) {
                try {
                    GeoReferenceMapGraphic mapGraphic = geoResource.resolve(GeoReferenceMapGraphic.class, null);
                    if (mapGraphic.equals(this)) {
                        map.sendCommandASync(new DeleteLayerCommand((Layer) layer));
                        break;
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
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

    public void setDraggingImage( boolean b ) {
        isDraggingImage = b;
    }

    public void setResizingImage( boolean b ) {
        isResizingImage = b;
    }

    /**
     * Get the Layer associated with this mapGrahic for the given map
     * 
     * @return layer
     */
    public ILayer getLayer( IMap map ) {
        if (map == null)
            return null;
        List<ILayer> mapLayers = map.getMapLayers();
        Iterator<ILayer> iterator = mapLayers.iterator();
        while( iterator.hasNext() ) {
            ILayer layer = iterator.next();
            GeoReferenceMapGraphic mapGraphic;
            try {
                mapGraphic = layer.getResource(GeoReferenceMapGraphic.class, null);
                if (mapGraphic != null) {
                    return layer;
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return null;
    }

    public List<PlaceMarker> getImageMarkers() {
        return imageMarkers;
    }

    public void setImageMarkers( List<PlaceMarker> imageMarkers ) {
        this.imageMarkers = imageMarkers;
    }

    public List<PlaceMarker> getBasemapMarkers() {
        return basemapMarkers;
    }

    public void setBasemapMarkers( List<PlaceMarker> basemapMarkers ) {
        this.basemapMarkers = basemapMarkers;
    }

    public void setWarpValue( int warpValue ) {
        this.warpValue = warpValue;
    }

    public int getWarpValue() {
        return warpValue;
    }

}
