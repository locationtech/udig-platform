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
package net.refractions.udig.tutorials.urladapter;

import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.IAdapterFactory;

public class URLAdapterFactory implements IAdapterFactory {

    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if( adapterType==URL.class ){
            Class<?> adaptableClass = adaptableObject.getClass();
            if(IGeoResource.class.isAssignableFrom(adaptableClass)) {
                return ((IGeoResource)adaptableObject).getIdentifier();
            } else if(IService.class.isAssignableFrom(adaptableClass)) {
                return ((IService)adaptableObject).getIdentifier();
            }
        }
        return null;
    }

    public Class<?>[] getAdapterList() {
        return new Class[]{URL.class} ;
    }

}
