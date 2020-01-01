/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.project.ILegendItem;
import org.locationtech.udig.project.internal.Folder;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerLegendItem;
import org.locationtech.udig.ui.PlatformGIS;

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
    
    public static void updateCheckboxesAsync(final LegendView view) {
        PlatformGIS.asyncInDisplayThread(new Runnable() {
            public void run() {
                updateCheckboxes(view);
            }
        }, true);
    }
    
    public static void updateCheckboxes(final LegendView view) {
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
        setFolderExpansion(viewer, folder);
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
     * Sets the expanded state of the folder on the view.
     * 
     * @param viewer
     * @param folder
     */
    private static void setFolderExpansion(final CheckboxTreeViewer viewer, final Folder folder) {
        final boolean isEmpty = (folder.getItems().size() == 0);
        viewer.setExpandedState(folder, !isEmpty);
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
