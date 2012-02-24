/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
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

import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IRepository;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.Interaction;
import net.refractions.udig.project.internal.Folder;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * The utility class of the Legend View. This contains static helper methods for Legend View
 * functions.
 * 
 * @author nchan
 * @since 1.3.1
 */
@SuppressWarnings("nls")
public final class LegendViewUtils {

    private static final String MAP_GRAPHIC_PROTOCOL = "mapgraphic"; //$NON-NLS-1$
    private static final String MAP_GRAPHIC_URL = "mapgraphic:/localhost"; //$NON-NLS-1$
    private static final String GRID_ID_STR = "grid"; //$NON-NLS-1$
    private static final String GRID_URL = "mapgraphic:/localhost/mapgraphic#grid"; //$NON-NLS-1$
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
        if (MAP_GRAPHIC_PROTOCOL.equals(layer.getID().getProtocol())) {
            return true;
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
        if (isMapGraphicLayer(layer) && GRID_ID_STR.equals(layer.getID().getRef())) {
            return true;
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
            System.out.println("[LegendViewUtils] Grid resource not found. Either service is not up or it does not hold the resource.");
        }
        return gridResource;
    }

    public static boolean isFolderSelected(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            final IStructuredSelection strucSelection = (IStructuredSelection) selection;
            if (strucSelection.size() == 1 && strucSelection.getFirstElement() instanceof Folder) {
                return true;
            }
        }
        return false;
    }
    
    public static Object getParent(Map map, Object object) {
        
        if (object instanceof Folder) {
            return map;
        } else if (object instanceof Layer) {
            final List legendItems = map.getLegend();
            for( Object legendItem : legendItems ) {
                if (legendItem instanceof Folder) {
                    final List folderItems = ((Folder) legendItem).getItems();
                    for( Object folderItem : folderItems ) {
                        if (folderItem == object) {
                            return legendItem;
                        }
                    }
                } else {
                    if (legendItem == object) {
                        return map;
                    }
                }
            }
        }
        
        return null;
        
    }
    
    public static Object getParent(Object object) {
        return getParent(((Map) ApplicationGIS.getActiveMap()), object);
    }
    
}
