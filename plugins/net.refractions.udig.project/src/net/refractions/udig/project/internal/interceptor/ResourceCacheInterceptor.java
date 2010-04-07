/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.interceptor;

import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceCachingInterceptor;

import org.geotools.styling.Style;
import org.opengis.coverage.grid.GridCoverage;

/**
 * Caches all resources and returns the cached instance.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ResourceCacheInterceptor implements IResourceCachingInterceptor {

    public static final String ID = "net.refractions.udig.project.caching"; //$NON-NLS-1$
    private Map<Class, Object> resources = new HashMap<Class, Object>();

    @SuppressWarnings("unchecked")
    private <T> void registerClasses( Class<T> clazz, Object obj ) {
        if( obj instanceof Style || obj instanceof GridCoverage){
            return;
        }
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class)
            registerClasses(clazz.getSuperclass(), obj);
        for( int i = 0; i < clazz.getInterfaces().length; i++ ) {
            registerClasses(clazz.getInterfaces()[i], obj);
        }
        resources.put(clazz, obj);
    }


    @SuppressWarnings("unchecked")
    public <T> T get( ILayer layer, Class<T> requestedType ) {
        return (T) resources.get(requestedType);
    }


    public <T> boolean isCached( ILayer layer, IGeoResource resource, Class<T> requestedType ) {
//        return resources.containsKey(resource.getClass());
        return resources.containsKey(requestedType);
    }

    public <T> void put( ILayer layer, T resource, Class<T> requestedType ) {
        if( resource!=null )
            registerClasses(resource.getClass(), resource);
    }

}
