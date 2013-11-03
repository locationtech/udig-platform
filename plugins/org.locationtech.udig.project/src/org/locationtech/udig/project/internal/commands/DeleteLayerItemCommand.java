/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands;

import java.util.List;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Folder;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Deletes a Layer item from the map. More specifically, deletes a Layer item from the LegendItem
 * list of the map.
 * 
 * @author Naz Chan
 * @since 1.3.1
 */
public class DeleteLayerItemCommand extends AbstractCommand implements UndoableMapCommand {

    /**
     * Layer to be deleted
     */
    private Layer layer;
    
    /**
     * Position of the layer to be deleted
     */
    private int index;
    
    /**
     * Folder containing the layer to be deleted
     */
    private Folder folder;
    
    /**
     * Flags if folder is existing in the map. Assigned on run. 
     * true - folder is existing in the map, otherwise, false
     */
    private boolean isInMap = false;
    
    /**
     * Creates a delete layer command
     * @param layer
     */
    public DeleteLayerItemCommand( Layer layer ) {
        this.layer = layer;
    }
    
    @Override
    public String getName() {
        return Messages.DeleteLayerItemCommand_Name;
    }
    
    @Override
    public void run( IProgressMonitor monitor ) throws Exception {
        
        initRunConditions(layer);
        
        if (layer != null && isInMap) {
            if (folder == null) {
                getMap().getLegend().remove(layer);
            } else {
                folder.getItems().remove(layer);
            }
            // TODO - Save currently selected item
            // TODO - If currently selected item is folder, select next item
        }
        
    }

    @Override
    public void rollback( IProgressMonitor monitor ) throws Exception {
        
        if (layer != null && isInMap) {
            if (folder == null) {
                getMap().getLegend().add(index, layer);
            } else {
                folder.getItems().add(index, layer);
            }
            // TODO - Restore currently selected item
        }
        
    }

    /**
     * Checks if layer is existing in the map and initialises isInMap, folder and index variables.
     * @param layer
     */
    private void initRunConditions(Layer layer) {
        
        if (layer != null) {

            //Init folder and isInMap flag
            final Object parent = getParent(getMap(), layer);
            if (parent == null) {
                isInMap = false;
            } else {
                isInMap = true;
                if (parent instanceof Folder) {
                    folder = (Folder) parent;
                }
            }
            
            //Init index
            if (folder == null) {
                index = getMap().getLegend().indexOf(layer);    
            } else {
                index = folder.getItems().indexOf(layer);    
            }
            
        }
                
    }
    
    /**
     * Gets the parent container of the layer. Parent can either be the LegendItem list (in the map)
     * or a folder in the LegendItem list.
     * 
     * @param map
     * @param layer
     * @return map - layer is in the LegendItem list, folder - layer is in this folder inside the
     *         LegendItem list
     */
    private Object getParent(Map map, Layer layer) {

        final List legendItems = map.getLegend();
        for( Object legendItem : legendItems ) {
            if (legendItem instanceof Folder) {
                final List folderItems = ((Folder) legendItem).getItems();
                for( Object folderItem : folderItems ) {
                    if (folderItem == layer) {
                        return legendItem;
                    }
                }
            } else {
                if (legendItem == layer) {
                    return map;
                }
            }
        }

        return null;

    }
    
}
