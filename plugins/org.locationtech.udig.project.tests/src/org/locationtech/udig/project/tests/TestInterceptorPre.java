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

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IResourceInterceptor;

import org.geotools.data.FeatureStore;
import org.junit.Ignore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

@Ignore
public class TestInterceptorPre implements
		IResourceInterceptor<FeatureStore<SimpleFeatureType, SimpleFeature>> {
	public static int runs = 0;

	public FeatureStore<SimpleFeatureType, SimpleFeature> run(
			ILayer layer,
			FeatureStore<SimpleFeatureType, SimpleFeature> resource,
			Class<? super FeatureStore<SimpleFeatureType, SimpleFeature>> requestedType) {
		runs++;
		return resource;
	}

}
