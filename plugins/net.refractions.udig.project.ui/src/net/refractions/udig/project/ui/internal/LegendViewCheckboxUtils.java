/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ILegendItem;
import net.refractions.udig.project.internal.Folder;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerLegendItem;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.PlatformUI;

/**
 * Contains utility methods to manage the checkboxes of the LegendView tree viewer.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public final class LegendViewCheckboxUtils {

    public static final int UNCHECKED = 0;
    public static final int CHECKED = 1;
    public static final int BLOCKED = 2;

    /**
     * Updates the checkbox of the input layer and checks if the parent folder (if inside a folder)
     * also needs update.
     * 
     * @param viewer
     * @param layer
     */
    public static void updateCheckbox( final LegendView view, final Layer layer ) {

        PlatformGIS.asyncInDisplayThread(new Runnable(){
            public void run() {
                
                if (!PlatformUI.getWorkbench().isClosing()) {
                    
                    final CheckboxTreeViewer viewer = view.getViewer();
                    // Set layer checkbox
                    setLayerCheckbox(viewer, layer);
                    // Set folder checkbox, if parent is folder
                    final Object parent = getParent(view.getCurrentMap(), layer);
                    if (parent instanceof Folder) {
                        setFolderCheckbox(viewer, (Folder) parent);
                    }
                    
                }
                
            }
        }, true);

    }

    /**
     * Updates the checkbox of the input folder and its child layers.
     * 
     * @param viewer
     * @param folder
     */
    public static void updateCheckbox( final LegendView view, final Folder folder, final boolean updateLayers ) {

        PlatformGIS.asyncInDisplayThread(new Runnable(){
            public void run() {
                
                if (!PlatformUI.getWorkbench().isClosing()) {
                    
                    final CheckboxTreeViewer viewer = view.getViewer();
                    if (updateLayers) {
                        for( ILegendItem folderItem : folder.getItems() ) {
                            if (folderItem instanceof LayerLegendItem) {
                                setLayerCheckbox(viewer, (LayerLegendItem) folderItem);
                            }
                        }    
                    }
                    setFolderCheckbox(viewer, folder);
                    
                }
                
            }
        }, true);

    }
    
    /**
     * Updates the checkboxes of the viewer of the input view.
     * 
     * @param view
     */
    public static void updateCheckboxes( final LegendView view ) {

        PlatformGIS.asyncInDisplayThread(new Runnable(){
            public void run() {

                if (!PlatformUI.getWorkbench().isClosing()) {

                    final CheckboxTreeViewer viewer = view.getViewer();
                    final List<ILegendItem> items = view.getCurrentMap().getLegend();

                    for( ILegendItem item : items ) {
                        if (item instanceof Folder) {
                            final Folder folder = (Folder) item;
                            for( ILegendItem folderItem : folder.getItems() ) {
                                if (folderItem instanceof LayerLegendItem) {
                                    setLayerCheckbox(viewer, (LayerLegendItem) folderItem);
                                }
                            }
                            setFolderCheckbox(viewer, folder);
                        } else if (item instanceof LayerLegendItem) {
                            setLayerCheckbox(viewer, (LayerLegendItem) item);
                        }
                    }

                }

            }

        }, true);

    }
    
    /**
     * Sets the checkbox static of the layer item on the viewer.
     * 
     * @param viewer
     * @param layerItem
     */
    private static void setLayerCheckbox(final CheckboxTreeViewer viewer, final LayerLegendItem layerItem ) {
        viewer.setChecked(layerItem, layerItem.getLayer().isVisible());
    }
    
    /**
     * Sets the checkbox status of the layer on the viewer.
     * 
     * @param viewer
     * @param layer
     */
    private static void setLayerCheckbox(final CheckboxTreeViewer viewer, final Layer layer ) {
        final Map map = (Map) viewer.getInput();
        if (map != null) {
            setLayerCheckbox(viewer, getLayerLegendItem(map, layer));    
        }
    }
    
    /**
     * Gets the legend item that references the layer.
     * 
     * @param map
     * @param layer
     */
    private static LayerLegendItem getLayerLegendItem(Map map, Layer layer) {
        LayerLegendItem referredLayerLegendItem = null;
        for (ILegendItem legendItem : map.getLegend()) {
            if (legendItem instanceof LayerLegendItem) {
                final LayerLegendItem layerLegendItem = (LayerLegendItem) legendItem;
                if (layer == layerLegendItem.getLayer()) {
                    referredLayerLegendItem = layerLegendItem;
                    break;
                }
            }
        }
        return referredLayerLegendItem;
    }
    
    /**
     * Sets the checkbox status of the folder on the viewer. This method checks the child layers of
     * the folder to determine the status.
     * 
     * @param viewer
     * @param folder
     */
    public static void setFolderCheckbox(final CheckboxTreeViewer viewer, final Folder folder ) {
        
        switch( getFolderCheckboxDisplay(viewer, folder) ) {
        case CHECKED:
            viewer.setChecked(folder, true);
            viewer.setGrayed(folder, false);
            break;
        case BLOCKED:
            viewer.setGrayChecked(folder, true);
            break;
        case UNCHECKED:
            viewer.setChecked(folder, false);
            viewer.setGrayed(folder, false);
            break;
        default:
            break;
        }

    }
    
    /**
     * Returns what the status of the folder's checkbox should be with respect to its child layers.
     * 
     * @param viewer
     * @param folder
     * @return CHECKED - the folder should be checked, BLOCKED - folder should be blocked, UNCHECKED
     *         - folder should be unchecked
     */
    private static int getFolderCheckboxDisplay( CheckboxTreeViewer viewer, Folder folder ) {
        
        final List<Layer> filterVisibleLayers = getFilterVisibleLayers(viewer, folder); 
        
        int checkedCnt = 0;
        for( Layer layer : filterVisibleLayers ) {
            if (layer.isVisible()) {
                checkedCnt++;
            }
        }
        
        if (checkedCnt > 0) {
            if (checkedCnt == filterVisibleLayers.size()) {
                return CHECKED;
            } else {
                return BLOCKED;
            }
        }
        
        return UNCHECKED;
    }
    
    /**
     * Gets the list of visible layers with the given filters on the view
     * @param viewer
     * @param folder
     * @return list of filter visible layers
     */
    private static List<Layer> getFilterVisibleLayers( CheckboxTreeViewer viewer, Folder folder ) {
        
        final List<Layer> filterVisibleLayers = new ArrayList<Layer>();
        
        for( ILegendItem item : folder.getItems() ) {
            if (item instanceof LayerLegendItem) {
                final LayerLegendItem layerItem = (LayerLegendItem) item;
                final Layer layer = layerItem.getLayer();
                if (isFilterVisible(viewer, layer)) {
                    filterVisibleLayers.add(layer);
                }    
            }
        }
            
        return filterVisibleLayers;
    }
    
    /**
     * Checks if the layer is visible with the given filters on the viewer.
     * @param viewer
     * @param layer
     * @return true if layer is visible, false otherwise
     */
    private static boolean isFilterVisible( CheckboxTreeViewer viewer, Layer layer ) {
        
        for( int i = 0; i < viewer.getFilters().length; i++ ) {
            final ViewerFilter filter = viewer.getFilters()[i];
            if (!filter.select(viewer, null, layer)) {
                return false;
            }
        }
        
        return true;
        
    }
    
    /**
     * Gets the parent object of the input object.
     * 
     * @param map
     * @param obj
     * @return map - if object is inside the LegendItems list, folder - if object is a layer inside a
     *         folder
     */
    public static Object getParent(Map map, Object obj) {
        
        if (obj instanceof Folder) {
            return map;
        } else if (obj instanceof LayerLegendItem) {
            for (Object legendItem : map.getLegend()) {
                if (legendItem instanceof Folder) {
                    final List<ILegendItem> folderItems = ((Folder) legendItem).getItems();
                    for (Object folderItem : folderItems) {
                        if (folderItem == obj) {
                            return legendItem;
                        }
                    }
                } else {
                    if (legendItem == obj) {
                        return map;
                    }
                }
            }
        }
        
        return null;
        
    }
    
    /**
     * Gets the parent object of the input object.
     * 
     * @param object
     * @return map - if object is inside the LegendItems list, folder - if object is a layer inside a
     *         folder
     */
    public static Object getParent(Object object) {
        return getParent(((Map) ApplicationGIS.getActiveMap()), object);
    }
    
}
