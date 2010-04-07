/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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

import java.util.Collections;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.ui.operation.PostReshapeAction;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

/**
 * Adds the reshaped resource to the current map
 * 
 * @author jesse
 * @since 1.1.0
 */
public class AddReshapedToMap implements PostReshapeAction {


    public void execute( IGeoResource original, IGeoResource reshaped ) {
        IMap map = ApplicationGIS.getActiveMap();
        if( map==ApplicationGIS.NO_MAP ){
            ApplicationGIS.addLayersToMap(null, Collections.singletonList(reshaped), 0);
        } else {
            ApplicationGIS.addLayersToMap(map, Collections.singletonList(reshaped), map.getMapLayers().size());
        }
    }

}
