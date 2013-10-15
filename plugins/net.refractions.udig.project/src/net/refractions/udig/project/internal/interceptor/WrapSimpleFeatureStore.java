/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
