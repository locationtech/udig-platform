/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
