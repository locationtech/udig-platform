/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;

/**
 * Starting place to add various helper methods to deal with project model.
 *
 * @author Vitalus
 *
 */
public class ProjectUtil {

    private ProjectUtil() {
        // helper class
    }

    /**
     * Checks the list of <code>IGeoResource</code>s from the <code>resources</code> list whether
     * there are already layers in <code>map</code> from these IGeoResources.
     * <p>
     * This utility method is used during adding new layers to the map to avoid duplicating the
     * layers from the same GeoResource.
     *
     * @param map
     * @param geoResources
     * @return Returns cleaned list of IGeoResources that do not have layers in specified
     *         <code>map</code>.
     */
    public static List<IGeoResource> cleanDuplicateGeoResources(
            Collection<IGeoResource> geoResources, IMap map) {

        Set<IGeoResource> goodGeoResources = new HashSet<>();
        for (IGeoResource geoResource : geoResources) {

            boolean toClean = false;

            if (map != null) {
                List<ILayer> layers = map.getMapLayers();
                for (ILayer layer : layers) {

                    if (URLUtils.urlEquals(layer.getID(), geoResource.getIdentifier(), false)) {
                        // There is already layer from this resource.
                        toClean = true;
                        break;
                    }

                }
            }
            if (!toClean) {
                goodGeoResources.add(geoResource);
            }
        }
        return new ArrayList<>(goodGeoResources);
    }

}
