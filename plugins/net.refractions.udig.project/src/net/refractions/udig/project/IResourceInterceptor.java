/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
