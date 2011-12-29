/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 * 		Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 * 		http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to license under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.internal.ui.util;

import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.render.IViewportModel;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.udig.tools.internal.mediator.AppGISMediator;

/**
 * Map Util
 * <p>
 * Utility functions for Map object
 * </p>
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
public final class MapUtil {
    

    private MapUtil(){
        // util class
    }
    /**
     * @param map
     * @return the Coordinate Reference System of map
     */
    public static CoordinateReferenceSystem getCRS( IMap map ) {
        assert map != null;
        
        IViewportModel viewportModel = map.getViewportModel();
        CoordinateReferenceSystem mapCrs = viewportModel.getCRS();
        return mapCrs;
    }
    
	/**
	 * Adds a new layer to map using the georesource of the feature store
	 * 
	 * @param map
	 * @param geoResource
	 * 
	 * @return the new layer
	 */
	public static ILayer addLayerToMap(IMap map, IGeoResource geoResource) {

		int index = map.getMapLayers().size();
		List<? extends ILayer> listLayer = AppGISMediator.addLayersToMap(map, Collections.singletonList(geoResource),
					index);

		assert listLayer.size() == 1; // creates only one layer

		ILayer layer = listLayer.get(0);

		return layer;
	}
	/**
	 * Returns the selected layer of Map
	 * @param map
	 * @return the selected layer 
	 */
	public static ILayer getSelectedLayer(IMap map) {
		
		if(map == null) return null;
		
		ILayer layer = map.getEditManager().getSelectedLayer();
		return layer;
	}
}
