/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.locationtech.udig.project.ILegendItem;
import org.locationtech.udig.project.internal.Folder;
import org.locationtech.udig.project.internal.LayerLegendItem;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.IDropAction;
import org.locationtech.udig.ui.ViewerDropLocation;

/**
 * Action that moves legend items within the legend items list. This moves layers in and out of
 * folders. And moves layers and folders up and down the order in the view.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class LegendDropAction extends IDropAction {

    /**
     * Flags if source object is a layer.
     */
    private boolean isLayerSource;

    /**
     * Flags if source object is a folder.
     */
    private boolean isFolderSource;

    /**
     * Flags if source object is a mix of layers and/or folders.
     */
    private boolean isMixedSource;

    /**
     * Flags if target object is a layer.
     */
    private boolean isLayerTarget;

    /**
     * Flags if target object is a folder.
     */
    private boolean isFolderTarget;

    /**
     * Source objects.
     */
    private List<Object> sources;

    /**
     * Target object.
     */
    private Object target;

    /**
     * Creates a legend item drop action
     */
    public LegendDropAction() {
        // Nothing
    }

    @Override
    public void init(IConfigurationElement element2, DropTargetEvent event2,
            ViewerDropLocation location2, Object destination2, Object data2) {
        super.init(element2, event2, location2, destination2, data2);
        initDropConditions();
    }
    
    @Override
    public boolean accept() {
        if (isValidObjects() && isValidDropLocation(getViewerLocation())) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the source and target objects are valid for drag and drop action.
     * 
     * @return trur if valid, otherwise false
     */
    private boolean isValidObjects() {
        // Check if either source or destination is null
        if (sources == null || target == null) {
            return false;
        }
        // Check if selection is more than 1
        if (sources.size() > 1) {
            return false;
        }
        // Check if source and destination is the same
        if (sources.size() == 1 && sources.get(0) == getDestination()) {
            return false;
        }
        return true;
    }
    
    /**
     * Checks if the drop location if valid relative to the source and target objects.
     * 
     * @param location
     * @return true if valid, otherwise false
     */
    private boolean isValidDropLocation(ViewerDropLocation location) {
        // Check if there is a drop location
        if (location == ViewerDropLocation.NONE) {
            return false;
        } else if (location == ViewerDropLocation.ON) {
            // Check if data being put inside is mixed
            if (isMixedSource) {
                return false;
            }
            // Check if layer data is being put inside another layer
            if (isLayerSource && isLayerTarget) {
                return false;
            }
            // Check if layer data is being put inside another layer
            if (isFolderSource && isLayerTarget) {
                return false;
            }
            // Check if data is being put inside its own parent
            if (isParent(getData(), getDestination())) {
                return false;
            }
            // Check if folder is being put inside its own descendant
            if (isDescendant(getData(), getDestination())) {
                return false;
            }
        } else {
            // Check if folder is being moved its own descendant
            if (isDescendant(getData(), getDestination())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void perform(IProgressMonitor monitor) {

        for (Object source : sources) {

            final ViewerDropLocation location = getViewerLocation();
            if (ViewerDropLocation.NONE == location) {
                // Do nothing
            } else if (ViewerDropLocation.ON == location) {
                moveIn(source);
            } else {
                move(source, location);
            }

        }

    }

    /**
     * Performs moving in the source object into a folder.
     * 
     * @param source
     */
    private void moveIn(Object source) {
        final ILegendItem sourceItem = (ILegendItem) source;
        final Folder folder = (Folder) target;
        folder.getItems().add(sourceItem);
    }

    /**
     * Performs moving the source object below or above another object. This can also move objects
     * in and out of a folder with a specific drop location.
     * 
     * @param source
     * @param location
     */
    private void move(Object source, ViewerDropLocation location) {

        final ILegendItem sourceItem = (ILegendItem) source;
        
        final List<ILegendItem> sourceSiblings = getSiblings(source);
        final List<ILegendItem> targetSiblings = getSiblings(target);

        if (sourceSiblings == targetSiblings) {

            sourceSiblings.remove(source);
            int targetIndexNew = targetSiblings.indexOf(getDestination());

            if (location == ViewerDropLocation.BEFORE) {
                targetSiblings.add(targetIndexNew, sourceItem);
            } else if (location == ViewerDropLocation.AFTER) {
                targetSiblings.add(targetIndexNew + 1, sourceItem);
            }

        } else {
            int targetIndex = targetSiblings.indexOf(getDestination());
            if (location == ViewerDropLocation.AFTER) {
                targetIndex++;
            }
            targetSiblings.add(targetIndex, sourceItem);
        }

    }
    
    /**
     * Gets siblings of the object. This includes the object in the correct order in their
     * containing list.
     * <p>
     * Examples: map.getLegend() or folder.getItems()
     * 
     * @param object
     * @return siblings
     */
    private List<ILegendItem> getSiblings(Object object) {
        final Object parent = getParent(object);
        if (parent instanceof Map) {
            return ((Map) parent).getLegend();
        } else if (parent instanceof Folder) {
            return ((Folder) parent).getItems();
        }
        return null;
    }
    
    /**
     * Gets the parent of the object inside the legend items list. This returns null if the object
     * is not a descendant of the list.
     * 
     * @param object
     * @return parent
     */
    private Object getParent(Object object) {
        final Map map = getActiveMap();
        final List<ILegendItem> items = map.getLegend();
        if (items.contains(object)) {
            return map;
        }
        for (ILegendItem item : items) {
            if (item instanceof Folder) {
                final Object parent = getParent((Folder) item, object);
                if (parent != null) {
                    return parent;
                }
            }
        }
        return null;
    }
    
    /**
     * Gets the parent of the object inside the folder. This returns null if the object is not a
     * descendant of the folder.
     * 
     * @param folder
     * @param object
     * @return parent
     */
    private Object getParent(Folder folder, Object object) {
        if (folder.getItems().contains(object)) {
            return folder;
        }
        for (ILegendItem folderItem : folder.getItems()) {
            if (folderItem instanceof Folder) {
                final Object parent = getParent((Folder) folderItem, object);
                if (parent != null) {
                    return parent;
                }
            }
        }
        return null;
    }
    
    /**
     * Checks if the target is the parent of the source.
     * 
     * @param source
     * @param target
     * @return true if parent, otherwise false
     */
    private boolean isParent(Object source, Object target) {
        return getParent(source) == target;
    }
    
    /**
     * Checks if the target is a descendant of the source.
     * 
     * @param source
     * @param target
     * @return true if descendant, otherwise false
     */
    private boolean isDescendant(Object source, Object target) {
        if (source instanceof Folder) {
            final Folder folder = (Folder) source;
            for (ILegendItem folderItem : folder.getItems()) {
                if (folderItem == target) {
                    return true;
                } else if (folderItem instanceof Folder) {
                    if (isDescendant(folderItem, target)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets the active map.
     * 
     * @return map
     */
    private Map getActiveMap() {
        return (Map) ApplicationGIS.getActiveMap();
    }
    
    /**
     * Initialises the drag/drop conditions and flags to process the action.
     */
    private void initDropConditions() {
        checkSource(getData());
        checkTarget(getDestination());
    }

    /**
     * Checks the source data then sets flags and variables to process the drop/drop action.
     * 
     * @param data
     */
    private void checkSource(Object data) {

        sources = null;
        isFolderSource = false;
        isLayerSource = false;
        isMixedSource = false;

        if (data != null) {
            sources = new ArrayList<Object>();
            if (data instanceof Object[]) {
                for (Object dataItem : (Object[]) data) {
                    sources.add(dataItem);
                }
                isMixedSource = true;
            } else {
                if (data instanceof LayerLegendItem) {
                    sources.add(data);
                    isLayerSource = true;
                } else if (data instanceof Folder) {
                    sources.add(data);
                    isFolderSource = true;
                }
            }
        }

    }

    /**
     * Checks the target data then sets flags and variables to process the drop/drop action.
     * 
     * @param data
     */
    private void checkTarget(Object data) {

        target = null;
        isFolderTarget = false;
        isLayerTarget = false;

        if (data != null) {
            if (data instanceof LayerLegendItem) {
                target = data;
                isLayerTarget = true;
            } else if (data instanceof Folder) {
                target = data;
                isFolderTarget = true;
            }
        }

    }

}
