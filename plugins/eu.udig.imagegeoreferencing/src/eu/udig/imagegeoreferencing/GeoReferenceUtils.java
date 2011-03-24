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

import org.eclipse.jface.action.IAction;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.tool.Tool;

/**
 * Common utility class for use with the Geo Referencing classes
 * 
 * @author GDavis, Refractions Research
 *
 */
public class GeoReferenceUtils {

    public static String IMAGE_REFERENCING_TOOL_CAT_ID = "net.refractions.udig.imagegeoreferencing.tools"; //$NON-NLS-1$

    public static String[] rotateableTools = {SelectImageTool.TOOLID, MoveImageTool.TOOLID, ResizeImageTool.TOOLID,
            PlaceMarkersTool.TOOLID, MoveMarkersTool.TOOLID};

    /**
     * Find the selected geoImage in the hashmap for the given map
     * 
     * @param mapImages
     * @param map
     * @return selected geoImage if found, null otherwise
     */
    public static GeoReferenceImage getSelectedGeoImage( HashMap<IMap, HashMap<String, GeoReferenceImage>> mapImages, IMap map ) {
        HashMap<String, GeoReferenceImage> images = mapImages.get(map);
        for( Iterator<Entry<String, GeoReferenceImage>> iterator = images.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<String, GeoReferenceImage> entry = (Entry<String, GeoReferenceImage>) iterator.next();
            GeoReferenceImage geoImage = entry.getValue();
            if (geoImage.isSelected()) {
                return geoImage;
            }
        }
        return null;
    }

    /**
     * Get the mapgraphic for the given map (or null if none exists)
     * 
     * @param map
     * @return mapgraphic
     */
    public static GeoReferenceMapGraphic getMapGraphic( IMap map ) {
        List<ILayer> mapLayers = map.getMapLayers();
        Iterator<ILayer> iterator = mapLayers.iterator();
        while( iterator.hasNext() ) {
            ILayer layer = iterator.next();
            GeoReferenceMapGraphic mapGraphic;
            try {
                mapGraphic = layer.getResource(GeoReferenceMapGraphic.class, null);
                if (mapGraphic != null) {
                    return mapGraphic;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Try finding the given tool by id
     *
     * @param id
     */
    public static Tool findTool( String toolid ) {
        Tool tool = null;
        try {
            tool = ApplicationGIS.getToolManager().findTool(toolid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tool;
    }

    /**
     * Using the static list of rotate-able tools, rotate to the next tool.
     *
     * @param currentTool
     */
    public static void rotateToNextTool( String currentToolId ) {
        int index = 0;
        for( String toolId : rotateableTools ) {
            if (toolId.equals(currentToolId)) {
                int getIndex = index + 1;
                if (getIndex >= rotateableTools.length) {
                    getIndex = 0;
                }
                IToolManager manager = ApplicationGIS.getToolManager();
                IAction action = manager.getToolAction(rotateableTools[getIndex], IMAGE_REFERENCING_TOOL_CAT_ID);
                if (action != null) {
                    action.run();
                }
                return;
            }
            index++;
        }
    }
}
