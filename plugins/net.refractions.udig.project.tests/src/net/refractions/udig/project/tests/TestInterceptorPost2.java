/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.project.tests;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceInterceptor;

import org.geotools.data.FeatureSource;
import org.junit.Ignore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

@Ignore
public class TestInterceptorPost2 implements
		IResourceInterceptor<FeatureSource<SimpleFeatureType, SimpleFeature>> {
	public static volatile int runs = 0;

	public FeatureSource<SimpleFeatureType, SimpleFeature> run(
			ILayer layer,
			FeatureSource<SimpleFeatureType, SimpleFeature> resource,
			Class<? super FeatureSource<SimpleFeatureType, SimpleFeature>> requestedType) {
		runs++;
		try {
			resource.getBounds();
		} catch (Throwable e) {
			// that's ok
		}
		return resource;
	}

}
