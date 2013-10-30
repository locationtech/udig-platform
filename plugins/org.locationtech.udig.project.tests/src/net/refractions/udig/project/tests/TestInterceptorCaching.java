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
package net.refractions.udig.project.tests;

import org.junit.Ignore;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceCachingInterceptor;

@Ignore
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
