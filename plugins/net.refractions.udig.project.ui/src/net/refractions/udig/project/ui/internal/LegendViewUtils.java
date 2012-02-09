package net.refractions.udig.project.ui.internal;

import org.eclipse.core.runtime.NullProgressMonitor;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IRepository;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Layer;

public final class LegendViewUtils {

    private static final String MAP_GRAPHIC_PROTOCOL = "mapgraphic"; //$NON-NLS-1$
    private static final String GRID_ID_STR = "grid"; //$NON-NLS-1$
    private static final String GRID_URL = "mapgraphic:/localhost/mapgraphic#grid"; //$NON-NLS-1$
    private static final ID GRID_ID = new ID(GRID_URL, null);
    
    public static boolean isBackgroundLayer(ILayer layer) {
        return false;
    }
    
    public static boolean isMapGraphicLayer(ILayer layer) {
        if(MAP_GRAPHIC_PROTOCOL.equals(layer.getID().getProtocol())) {
            return true;
        }
        return false;
    }
    
    public static boolean isGridLayer(ILayer layer) {
        if(isMapGraphicLayer(layer) && GRID_ID_STR.equals(layer.getID().getRef())) {
            return true;
        }
        return false;
    }
    
    public static Layer findGridLayer(IMap map) {
        
        if (map != null) {
            for( ILayer layer : map.getMapLayers() ) {
                if (isGridLayer(layer)) {
                    return (Layer) layer;
                }
            }
        }

        return null;
        
    }
    
    public static IGeoResource getGridMapGraphic() {
        final IRepository local = CatalogPlugin.getDefault().getLocal();
        return local.getById(IGeoResource.class, GRID_ID, new NullProgressMonitor() );
    }
    
}
