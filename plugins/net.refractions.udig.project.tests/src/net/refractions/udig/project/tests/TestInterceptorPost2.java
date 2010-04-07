package net.refractions.udig.project.tests;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceInterceptor;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

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
