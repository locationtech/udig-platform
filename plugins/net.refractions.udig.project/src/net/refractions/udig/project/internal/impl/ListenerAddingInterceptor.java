/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceInterceptor;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Each Layer is able to listen for feature events; this interceptor hooks them up.
 * <p>
 * This is not really an scalable solution; and should be replaced with an extension point that
 * allows layers to listen to content changing in a generic way.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ListenerAddingInterceptor
        implements
            IResourceInterceptor<FeatureSource<SimpleFeatureType, SimpleFeature>> {

    public FeatureSource<SimpleFeatureType, SimpleFeature> run( ILayer layer,
            FeatureSource<SimpleFeatureType, SimpleFeature> resource,
            Class< ? super FeatureSource<SimpleFeatureType, SimpleFeature>> requestedType ) {
        if (layer instanceof LayerImpl) {
            LayerImpl layerInternal = (LayerImpl) layer;
            resource.addFeatureListener( layerInternal.featureListener);
        }
        return resource;
    }

}
