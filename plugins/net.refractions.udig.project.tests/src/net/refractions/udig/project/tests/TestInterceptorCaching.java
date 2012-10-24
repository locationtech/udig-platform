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
