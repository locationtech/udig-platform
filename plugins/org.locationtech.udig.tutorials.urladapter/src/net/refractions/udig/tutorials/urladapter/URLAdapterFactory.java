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
