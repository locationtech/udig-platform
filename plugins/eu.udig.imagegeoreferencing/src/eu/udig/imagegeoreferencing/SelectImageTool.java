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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;

/**
 * This tool selects the geoImage clicked (and unselects all others)
 * 
 * @author GDavis, Refractions Research
 * @since 1.1.0
 */
public class SelectImageTool extends AbstractModalTool implements ModalTool {

    public static final String TOOLID = "net.refractions.udig.imagegeoreference.tools.selectImageTool"; //$NON-NLS-1$

    public SelectImageTool() {
        super(MOUSE);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed( MapMouseEvent e ) {
        // validate the mouse click (only left mouse clicks should work for dragging)
        if (!validModifierButtonCombo(e)) {
            return;
        }

        // get a list of mapgraphic images for the current map and see if
        // any are under the clicked spot. If they are, select the top most one.
        IMap map = getContext().getMap();
        List<ILayer> mapLayers = map.getMapLayers();
        Iterator<ILayer> iterator = mapLayers.iterator();
        HashMap<String, GeoReferenceImage> images;
        while( iterator.hasNext() ) {
            ILayer layer = iterator.next();
            GeoReferenceMapGraphic mapGraphic;
            try {
                mapGraphic = layer.getResource(GeoReferenceMapGraphic.class, null);
                if (mapGraphic != null) {
                    images = mapGraphic.getImages().get(map);
                    // can't determine top one for now, so just select the first one
                    // that matches and unselect all others.
                    if (images != null && !images.isEmpty()) {
                        Iterator<Entry<String, GeoReferenceImage>> iterator2 = images.entrySet().iterator();
                        GeoReferenceImage selected = null;
                        while( iterator2.hasNext() ) {
                            Entry<String, GeoReferenceImage> next = iterator2.next();
                            GeoReferenceImage image = next.getValue();
                            int scaledWidth = image.getScaledWidth();
                            int scaledHeight = image.getScaledHeight();
                            int posX = image.getPosX();
                            int posY = image.getPosY();
                            // select this image if it is within the bounds and
                            // no image has yet been selected from this method
                            if (selected == null && e.x >= posX && e.x <= (posX + scaledWidth) && e.y >= posY
                                    && e.y <= (posY + scaledHeight)) {
                                image.setSelected(true);
                                selected = image;
                            } else {
                                image.setSelected(false);
                            }
                        }
                        layer.refresh(null); // refresh
                    }
                    break;
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
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
            GeoReferenceUtils.rotateToNextTool(SelectImageTool.TOOLID);
            return false;
        }
        return e.buttons == MapMouseEvent.BUTTON1 && !(e.modifiersDown());
    }

}
