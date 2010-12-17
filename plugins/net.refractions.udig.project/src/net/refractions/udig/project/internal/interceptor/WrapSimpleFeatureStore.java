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
import net.refractions.udig.project.internal.impl.UDIGSimpleFeatureStore;
import net.refractions.udig.project.internal.impl.UDIGStore;

import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Wraps a SimpleFeatureStore in a UDIGSimpleFeatureStore.
 * <p>
 * This is done to ensure that (ensuring that the transaction is only set once!).
 * 
 * @author Jesse
 * @since 1.2.1
 */
public class WrapSimpleFeatureStore
        implements
            IResourceInterceptor<SimpleFeatureStore> {

    public SimpleFeatureStore run( ILayer layer,
            SimpleFeatureStore resource,
            Class< ? super SimpleFeatureStore> requestedType ) {
        
        if( resource instanceof UDIGStore ){
            return resource;
        }
        
        if (requestedType.isAssignableFrom(SimpleFeatureStore.class)){
            return new UDIGSimpleFeatureStore(resource, layer);
        }
        return resource;
    }
    
}
