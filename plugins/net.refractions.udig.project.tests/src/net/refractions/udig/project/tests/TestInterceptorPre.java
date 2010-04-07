package net.refractions.udig.project.tests;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceInterceptor;

import org.geotools.data.FeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

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
