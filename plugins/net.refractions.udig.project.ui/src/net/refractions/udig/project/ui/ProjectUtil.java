package net.refractions.udig.project.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;

/**
 * Starting place to add various helper methods to deal with project model.
 * 
 * @author Vitalus
 *
 */
public class ProjectUtil {
    
    
    /**
     * Checks the list of <code>IGeoResource</code>s from the <code>resources</code> list whether there are
     * already layers in <code>map</code> from these georesources.
     * <p>
     * This utility method is used during adding new layers to the map to avoid diplicating the layers
     * from the same georesource.
     * 
     * @param map
     * @param geoResources
     * @return
     *      Returns cleaned list of georesources that do not have layers in specified <code>map</code>.
     */
    public static List<IGeoResource> cleanDuplicateGeoResources(Collection<IGeoResource> geoResources, IMap map){
        

        Set<IGeoResource> goodGeoResources = new HashSet<IGeoResource>();
        for(IGeoResource geoResource : geoResources) {

            boolean toClean = false;

            if(map != null){
                List<ILayer> layers = map.getMapLayers();
                for( ILayer layer : layers ) {

                    if(layer.getID().equals(geoResource.getIdentifier())){
                        /*
                         * There is already layer from this georesource.
                         */
                        toClean = true;
                        break;
                    }

                }
            }
            if(!toClean){
                goodGeoResources.add(geoResource);
            }
        }
        ArrayList<IGeoResource> list = new ArrayList<IGeoResource>(goodGeoResources);
        return list;
    }

}
