/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests;

import org.geotools.data.FeatureSource;
import org.junit.Ignore;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IResourceInterceptor;
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
