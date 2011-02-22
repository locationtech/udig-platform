package net.refractions.udig.project.tests;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceInterceptor;

import org.geotools.data.FeatureSource;

public class TestInterceptorPost2 implements IResourceInterceptor<FeatureSource> {
    public static volatile int runs=0;
    public FeatureSource run( ILayer layer, FeatureSource resource,Class<? super FeatureSource> requestedType ) {
        runs++;
        try {
            resource.getBounds();
        } catch (Throwable e) {
            // that's ok
        }
        return resource;
    }

}
