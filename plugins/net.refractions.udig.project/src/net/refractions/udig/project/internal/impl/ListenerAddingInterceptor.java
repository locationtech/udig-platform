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
package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceInterceptor;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Each Layer is able to listen for feature events; this interceptor hooks them up.
 * <p>
 * This is not really an scalable solution; and should be replaced with an extension point
 * that allows layers to listen to content changing in a generic way.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ListenerAddingInterceptor implements
		IResourceInterceptor<FeatureSource<SimpleFeatureType, SimpleFeature>> {

	public FeatureSource<SimpleFeatureType, SimpleFeature> run(
			ILayer layer,
			FeatureSource<SimpleFeatureType, SimpleFeature> resource,
			Class<? super FeatureSource<SimpleFeatureType, SimpleFeature>> requestedType) {
		if (layer instanceof LayerImpl){
			resource.addFeatureListener(((LayerImpl) layer).featureListener);
		}
		return resource;
	}

}
