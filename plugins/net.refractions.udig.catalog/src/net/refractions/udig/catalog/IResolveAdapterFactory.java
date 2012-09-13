/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2006-2012, Refractions Research Inc.
 *    (C) 2006, GeoTools Project Management Committee (PMC).
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
package net.refractions.udig.catalog;

import java.io.IOException;

import net.refractions.udig.catalog.internal.ResolveManager;

import org.eclipse.core.runtime.IProgressMonitor;


/**
 * Adapts a resolve handle into another type of object.
 * <p>
 * This API differs from the generic eclipse adaptable api:
 * <ul>
 * <li>we need to explicitly account for contacting external resources
 * </ul>
 * 
 * @author Justin Deoliveira, The Open Planning Project
 * @since 1.0
 * @version 1.3.2
 */
public interface IResolveAdapterFactory {
    
    /**
     * Check to determine if a connecting using the specified adapterType is possible.
     * <p>
     * Please be advised that a return value of true only indicates that a connection
     * can be attempted using the information on hand. As an example if a service is offline
     * we will get an IOException when connecting using the {@link #adapt(IResolve, Class, IProgressMonitor)}
     * method even though we have enough information to try and contact the service.   
     * 
     * <p>
     * <b>NOTE</b>  If this factory is declared in an extension point
     * and the {@link ResolveManager} is responsible for canResolve then this method 
     * will not be called because the information from the xml will be used instead.
     * </p>
     *
     * @param resolve The handle being adapted.
     * @param adapterType The type of adapter to connect to
     *
     * @return True if supported, otherwise false.
     */
    boolean canAdapt(IResolve resolve, Class<? extends Object> adapterType);

    /**
     * Connect to an IResolve using the requested adapter API.
     * <p>
     * This method is expected to involve considerable deplay, and is not suitable for
     * use from the display thread. A full progress monitor is supported if you need
     * to advise the user of progress during the connection process.
     * <p>
     * In the event connection is successful an object of the requested adapter type
     * is returned. Do not assume this value is cached, and keep hold of this object
     * as it is considered expensive to request.
     *
     * @param resolve IResolve handle being adapted
     * @param adapterType The type of adapter to connect to
     * @param monitor monitor used to monitor the process of connecting
     *
     * @return The adapter, or null if connection was not available
     *
     * @throws IOException If connection failed an IOException is provided
     */
    <T> T adapt(IResolve resolve, Class<T> adapterType, IProgressMonitor monitor)
        throws IOException;

}