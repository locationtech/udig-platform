/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.examples;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.feature.FeatureCollection;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * How to create a temporary layer
 *
 * @author Jesse
 * @since 1.1.0
 */
public class CreateTemporaryLayer {
    /**
     * Q:
     *
     * I'm working on 'transformation' plugin (for RC5) and I've run in some problems during
     * creating the Layer from temporary resource. I'm having the FeatureCollection (result from
     * transformation) and I would like to make a temporary layer from that. I was searching a lot
     * about this in mail list, but still can't understand how I should do it in right way. When I
     * do it this way it works:
     *
     * <pre>
     * map.getLayersInternal().add(newLayer);
     * newLayer.getGeoResource().resolve(FeatureStore.class, null).addFeatures(collection);
     * </pre>
     *
     * but when the Feature collection is big (and usually it is) and uDig starts rendering right
     * after each feature is added, it is very very slow, so I tried to add new features first and
     * then to add the layer into the map, but then I get java.lang.NullPointerException
     *
     * <pre>
     * newLayer.getGeoResource().resolve(FeatureStore.class, null).addFeatures(collection);
     * map.getLayersInternal().add(newLayer);
     * </pre>
     *
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void example(SimpleFeatureType featureType, IProgressMonitor progressMonitor,
            FeatureCollection collection, int addPosition, IMap map) throws IOException {
        IGeoResource resource = CatalogPlugin.getDefault().getLocalCatalog()
                .createTemporaryResource(featureType);
        resource.resolve(FeatureStore.class, progressMonitor).addFeatures(collection);

        ApplicationGIS.addLayersToMap(map, Collections.singletonList(resource), addPosition);

    }
}
