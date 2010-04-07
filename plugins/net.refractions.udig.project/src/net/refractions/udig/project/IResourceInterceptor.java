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
package net.refractions.udig.project;

/**
 * Modifies an IGeoResource based before returned to caller of 
 * {@link ILayer#getResource(Class, org.eclipse.core.runtime.IProgressMonitor)}.
 * 
 * @param <T> the type of resources that this interceptor can work on.
 * @author Jesse
 * @since 1.1.0
 */
public interface IResourceInterceptor<T> {
    /**
     * Modifies the resource that is returned.  The returned value is not necessarily the value passed in as a parameter
     *
     * @param layer the layer that the resources is being obtained from.
     * @param resource The resource obtained from the IGeoResource.
     * @param requestedType the type that the caller requested.
     * @return the resource to return to the caller.  May be a new instance or the same instance.
     */
    T run( ILayer layer, T resource, Class<? super T> requestedType );
}
