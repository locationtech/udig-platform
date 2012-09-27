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

import java.util.Arrays;
import java.util.List;

import net.refractions.udig.project.ILegendItem;
import net.refractions.udig.project.internal.Folder;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerLegendItem;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.ui.PlatformUI;

/**
 * Contains utility methods to manage the checkboxes of the LegendView tree viewer.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public final class LegendViewCheckboxUtils {

    enum FolderState {
        UNCHECKED, CHECKED, BLOCKED;
    }
    
    public static void updateCheckboxesv0Async(final LegendView view) {
        PlatformGIS.asyncInDisplayThread(new Runnable() {
            public void run() {
                updateCheckboxesv0(view);
            }
        }, true);
    }
    
    public static void updateCheckboxesv0(final LegendView view) {
        if (!PlatformUI.getWorkbench().isClosing()) {

            final CheckboxTreeViewer viewer = view.getViewer();
            final List<ILegendItem> items = view.getCurrentMap().getLegend();
            
            for (ILegendItem item : items) {
                updateCheckbox(viewer, item);
            }

        }
    }
    
    private static void updateCheckbox(final CheckboxTreeViewer viewer, final ILegendItem item) {
        if (item instanceof Folder) {
            final Folder folder = (Folder) item;
            updateCheckbox(viewer, folder);
        } else if (item instanceof LayerLegendItem) {
            final LayerLegendItem layerItem = (LayerLegendItem) item; 
            updateCheckbox(viewer, layerItem);
        }
    }

    private static void updateCheckbox(final CheckboxTreeViewer viewer, final Folder folder) {
        for (ILegendItem item : folder.getItems()) {
            updateCheckbox(viewer, item);
        }
        setFolderCheckbox(viewer, folder);
    }

    private static void updateCheckbox(final CheckboxTreeViewer viewer, final LayerLegendItem layerItem) {
        setLayerCheckbox(viewer, layerItem);
    }

    
    /**
     * Sets the checkbox static of the layer item on the viewer.
     * 
     * @param viewer
     * @param layerItem
     */
    private static void setLayerCheckbox(final CheckboxTreeViewer viewer,
            final LayerLegendItem layerItem) {
        final Layer layer = layerItem.getLayer();
        viewer.setChecked(layerItem, layer.isVisible());
    }
    
    /**
     * Sets the checkbox status of the folder on the viewer. This method checks the child layers of
     * the folder to determine the status.
     * 
     * @param viewer
     * @param folder
     */
    private static void setFolderCheckbox(final CheckboxTreeViewer viewer, final Folder folder) {

        switch (getFolderCheckboxDisplay(viewer, folder)) {
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
    private static FolderState getFolderCheckboxDisplay(CheckboxTreeViewer viewer, Folder folder) {
        
        final List<ILegendItem> items = folder.getItems();
        if (items.size() == 0) {
            
            final boolean isGrayed = viewer.getGrayed(folder);
            if (isGrayed) {
                return FolderState.BLOCKED;
            }
            final boolean isChecked = viewer.getChecked(folder);
            if (isChecked) {
                return FolderState.CHECKED;
            }
            
        } else {

            int checkedCnt = 0;
            int grayedCnt = 0;
            final List<Object> grayedElements = Arrays.asList(viewer.getGrayedElements());
            final List<Object> checkedElements = Arrays.asList(viewer.getCheckedElements());
            
            for (ILegendItem item : items) {
                if (grayedElements.contains(item)) {
                    grayedCnt++;
                } else if (checkedElements.contains(item)) {
                    checkedCnt++;
                }
            }
            if (checkedCnt > 0) {
                if (checkedCnt == items.size()) {
                    return FolderState.CHECKED;
                } else {
                    return FolderState.BLOCKED;
                }
            }
            if (grayedCnt > 0) {
                return FolderState.BLOCKED;
            }
            
        }
        
        return FolderState.UNCHECKED; 
        
    }
    
}
