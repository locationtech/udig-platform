package net.refractions.udig.project.tests;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceInterceptor;

import org.geotools.data.FeatureStore;

public class TestInterceptorPre implements IResourceInterceptor<FeatureStore> {
    public static int runs=0;
    public FeatureStore run( ILayer layer, FeatureStore resource,Class<? super FeatureStore> requestedType ) {
        runs++;
        return resource;
    }

}
