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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IRepository;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILegendItem;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.internal.Folder;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerLegendItem;

/**
 * The utility class of the Legend View. This contains static helper methods for Legend View
 * functions.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public final class LegendViewUtils {

    private static final String MAP_GRAPHIC_PROTOCOL = "mapgraphic"; //$NON-NLS-1$
    private static final String GRID_ID_STR = "grid"; //$NON-NLS-1$
    private static final String GRID_URL = MAP_GRAPHIC_PROTOCOL + ":/localhost/mapgraphic#" + GRID_ID_STR; //$NON-NLS-1$
    private static final ID GRID_ID = new ID(GRID_URL, null);

    /**
     * Checks if layer is a background layer
     * 
     * @param layer
     * @return true - if layer is a background layer, otherwise false
     */
    public static boolean isBackgroundLayer( ILayer layer ) {
        if (layer.getInteraction(Interaction.BACKGROUND)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if layer is a map graphics layer
     * 
     * @param layer
     * @return true - if layer is a map graphics layer, otherwise false
     */
    public static boolean isMapGraphicLayer( ILayer layer ) {
        if (layer != null && layer.getID() != null) {
            if (MAP_GRAPHIC_PROTOCOL.equals(layer.getID().getProtocol())) {
                return true;
            }    
        }
        return false;
    }

    /**
     * Checks if layer is a grid layer
     * 
     * @param layer
     * @return true - if layer is a grid layer, otherwise false
     */
    public static boolean isGridLayer( ILayer layer ) {
        if (layer != null && layer.getID() != null) {
            if (isMapGraphicLayer(layer) && GRID_ID_STR.equals(layer.getID().getRef())) {
                return true;
            }    
        }
        return false;
    }

    public static Layer findGridLayer( IMap map ) {

        if (map != null) {
            for( ILayer layer : map.getMapLayers() ) {
                if (isGridLayer(layer)) {
                    return (Layer) layer;
                }
            }
        }

        return null;

    }

    /**
     * Retrieves the an IGeoResource reference of the grid map graphic from the catalog.
     * 
     * @return grid map graphic, returns null if service is not up or service does not hold the resource
     */
    public static IGeoResource getGridMapGraphic() {
        final IRepository local = CatalogPlugin.getDefault().getLocal();
        final IGeoResource gridResource = local.getById(IGeoResource.class, GRID_ID, new NullProgressMonitor()); 
        if (gridResource == null) {
            System.out.println("[LegendViewUtils] Grid resource not found. Either service is not up or it does not hold the resource."); //$NON-NLS-1$
        }
        return gridResource;
    }

    /**
     * Checks if the selection input is a folder.
     * @param selection
     * @return true if selection is a folder, else false
     */
    public static boolean isFolderSelected(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            final IStructuredSelection strucSelection = (IStructuredSelection) selection;
            if (strucSelection.size() == 1 && strucSelection.getFirstElement() instanceof Folder) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the list of layers from the legendItems. Option can be specified to return an ordered
     * list by z-order.
     * 
     * @param items
     * @param isOrdered
     * @return list of layers
     */
    public static List<Layer> getLayers(List<ILegendItem> items, boolean isOrdered) {
        // Gets the layers from the LegendItems list
        final List<Layer> layers = new ArrayList<Layer>();
        for (ILegendItem item : items) {
            layers.addAll(getLayers(item));
        }
        // Sorts the layers according to z-order (defined in LayerImpl)
        if (isOrdered) {
            Collections.sort(layers);
        }
        return layers;
    }
    
    /**
     * Checks the item type and filters all the legend items that references layers.
     * 
     * @param item
     * @return layers
     */
    private static List<Layer> getLayers(ILegendItem item) {
        final List<Layer> layers = new ArrayList<Layer>();
        if (item instanceof Folder) {
            layers.addAll(getLayers((Folder) item));
        } else if (item instanceof LayerLegendItem) {
            final LayerLegendItem layerItem = (LayerLegendItem) item;
            layers.add(layerItem.getLayer());
        }
        return layers;
    }
    
    /**
     * Traverse the contents of the folder and gets all the layers that legend items refer to.
     * 
     * @param folder
     * @return layers
     */
    private static List<Layer> getLayers(Folder folder) {
        final List<Layer> layers = new ArrayList<Layer>();
        for (ILegendItem item : folder.getItems()) {
            if (item instanceof Folder) {
                layers.addAll(getLayers((Folder) item));
            } else if (item instanceof LayerLegendItem) {
                final LayerLegendItem layerItem = (LayerLegendItem) item;
                layers.add(layerItem.getLayer());
            }
        }
        return layers;
    }
    
    /**
     * Filters and gets the referenced grid layers from the legend item.
     * 
     * @param legendItems
     * @return grid layers
     */
    public static List<Layer> getGridLayers( List<ILegendItem> legendItems ) {

        // Gets the grid layers from the LegendItems list
        final List<Layer> layers = new ArrayList<Layer>();
        for (ILegendItem item : legendItems) {
            if (item instanceof Folder) {
                final Folder folder = (Folder) item;
                for (ILegendItem folderItem : folder.getItems()) {
                    if (folderItem instanceof LayerLegendItem) {
                        final LayerLegendItem layerItem = (LayerLegendItem) folderItem;
                        final Layer layer = layerItem.getLayer();
                        if (isGridLayer(layer)) {
                            layers.add(layer);
                        }
                    }
                }
            } else if (item instanceof LayerLegendItem) {
                final LayerLegendItem layerItem = (LayerLegendItem) item;
                final Layer layer = layerItem.getLayer();
                if (isGridLayer(layer)) {
                    layers.add(layer);
                }
            }
        }

        return layers;

    }
    
}
