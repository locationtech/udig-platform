/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.interceptor;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IResourceInterceptor;
import org.locationtech.udig.project.internal.impl.UDIGFeatureStore;
import org.locationtech.udig.project.internal.impl.UDIGSimpleFeatureStore;
import org.locationtech.udig.project.internal.impl.UDIGStore;

import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Wraps a FeatureStore in a UDIGFeatureStore (ensuring that the transaction is only set once!).
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class WrapFeatureStore implements IResourceInterceptor<FeatureStore< ? , ? >> {

    public FeatureStore< ? , ? > run( ILayer layer, FeatureStore< ? , ? > resource,
            Class< ? super FeatureStore< ? , ? >> requestedType ) {
        
        if( resource instanceof UDIGStore ){
            return resource;
        }
        
        if (requestedType.isAssignableFrom(SimpleFeatureStore.class) ||
                requestedType.isAssignableFrom(FeatureStore.class) ){
            if( resource instanceof SimpleFeatureStore){
                return new UDIGSimpleFeatureStore(resource, layer);
            }
            else {
                @SuppressWarnings("unchecked")
                FeatureStore<FeatureType,Feature> prep = (FeatureStore<FeatureType,Feature>) resource;
                return new UDIGFeatureStore( prep, layer);
            }
        }
        return resource;
    }
}
