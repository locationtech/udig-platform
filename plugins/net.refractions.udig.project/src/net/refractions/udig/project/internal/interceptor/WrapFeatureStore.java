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
package net.refractions.udig.project.internal.interceptor;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceInterceptor;
import net.refractions.udig.project.internal.impl.UDIGFeatureStore;

import org.geotools.data.FeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Wraps a FeatureStore in a UDIGFeatureStore
 * @author Jesse
 * @since 1.1.0
 */
public class WrapFeatureStore implements IResourceInterceptor<FeatureStore<SimpleFeatureType, SimpleFeature>> {

    @SuppressWarnings("unchecked")
    public FeatureStore<SimpleFeatureType, SimpleFeature> run(
			ILayer layer,
			FeatureStore<SimpleFeatureType, SimpleFeature> resource,
			Class<? super FeatureStore<SimpleFeatureType, SimpleFeature>> requestedType) {
        if( !(resource instanceof UDIGFeatureStore)){
            if( requestedType.isAssignableFrom(FeatureStore.class))
                return new UDIGFeatureStore(resource, layer);
            else 
                return resource;
        }
        return resource;
    }

}
