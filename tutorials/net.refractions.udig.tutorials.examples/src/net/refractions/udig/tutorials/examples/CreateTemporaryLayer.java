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
package net.refractions.udig.tutorials.examples;

import java.io.IOException;
import java.util.Collections;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;

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
     * I'm working on 'transformation' plugin (for RC5) and I've run in some problems during creating the Layer from temporary resource. I'm having the FeatureCollection (result from transformation) and I would like to make a temporary layer from that. I was searching a lot about this in mail list, but still can't understand how I should do it in right way.
     * When I do it this way it works:
     *
     *     <pre>
     *     map.getLayersInternal().add(newLayer);
     *     newLayer.getGeoResource().resolve(FeatureStore.class,null).addFeatures(collection);
     *     </pre>
     * but when the Feature collection is big (and ussualy it is)  and uDig starts rendering right after each feature is added, it is very very slow, so I tryed to add  new features first and then to add the layer into the map, but then I get java.lang.NullPointerException
     *     <pre>
     *     newLayer.getGeoResource().resolve(FeatureStore.class,null).addFeatures(collection);
     *          map.getLayersInternal().add(newLayer);
     * </pre>
     *
     */
    public void example( FeatureType featureType, IProgressMonitor progressMonitor,
            FeatureCollection collection, int addPosition, IMap map ) throws IOException {
        IGeoResource resource = CatalogPlugin.getDefault().getLocalCatalog()
                .createTemporaryResource(featureType);
        resource.resolve(FeatureStore.class, progressMonitor).addFeatures(collection);

        ApplicationGIS.addLayersToMap(map, Collections.singletonList(resource), addPosition);

    }
}
