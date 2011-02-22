package net.refractions.udig.project.tests;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceCachingInterceptor;

public class TestInterceptorCaching implements IResourceCachingInterceptor {


    public static boolean cached;
    public static boolean obtained;
    private Object cache;

    @SuppressWarnings("unchecked")
    public <T> T get( ILayer layer, Class<T> requestedType ) {
        obtained=true;
        return (T) cache;
    }

    public <T> boolean isCached( ILayer layer, IGeoResource resource, Class<T> requestedType ) {
        if( cached ){
            return true;
        }
        return false;
    }

    public <T> void put( ILayer layer, T resource, Class<T> requestedType ) {
        cached=true;
        this.cache=resource;
    }


}
