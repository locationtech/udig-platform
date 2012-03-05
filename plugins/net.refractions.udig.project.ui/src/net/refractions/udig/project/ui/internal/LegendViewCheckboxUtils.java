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
import net.refractions.udig.project.internal.Map;
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
                    final Object parent = LegendViewUtils.getParent(view.getCurrentMap(), layer);
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
                            final Layer layer = (Layer) folderItem;
                            setLayerCheckbox(viewer, layer);
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

                    final Map map = view.getCurrentMap();
                    final CheckboxTreeViewer viewer = view.getViewer();
                    final List<? extends ILegendItem> legendItems = map.getLegend();

                    for( ILegendItem legendItem : legendItems ) {
                        if (legendItem instanceof Folder) {
                            final Folder folder = (Folder) legendItem;
                            for( ILegendItem folderItem : folder.getItems() ) {
                                final Layer layer = (Layer) folderItem;
                                setLayerCheckbox(viewer, layer);
                            }
                            setFolderCheckbox(viewer, folder);
                        } else if (legendItem instanceof Layer) {
                            final Layer layer = (Layer) legendItem;
                            setLayerCheckbox(viewer, layer);
                        }
                    }

                }

            }

        }, true);

    }
    
    /**
     * Sets the checkbox status of the layer on the viewer.
     * 
     * @param viewer
     * @param layer
     */
    private static void setLayerCheckbox(final CheckboxTreeViewer viewer, final Layer layer ) {
        viewer.setChecked(layer, layer.isVisible());
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
            final Layer layer = (Layer) item;
            if (isFilterVisible(viewer, layer)) {
                filterVisibleLayers.add(layer);
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
    
}
