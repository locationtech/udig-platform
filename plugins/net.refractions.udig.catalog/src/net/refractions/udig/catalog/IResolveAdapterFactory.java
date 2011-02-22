/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2006, Refractions Research Inc.
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
 * @author Justin Deoliveira, The Open Planning Project, jdeolive@openplans.org
 *
 */
public interface IResolveAdapterFactory {
    /**
     * Determines if a particular adaptation is supported.
     *
     * <p>
     * <b>NOTE</b>  If this factory is declared in an extension point
     * and the {@link ResolveManager} is responsible for canResolve then this method
     * will not be called because the information from the xml will be used instead.
     * </p>
     *
     * @param resolve The handle being adapted.
     * @param adapter The adapting class.
     *
     * @return True if supported, otherwise false.
     */
    boolean canAdapt(IResolve resolve, Class<? extends Object> adapter);

    /**
     * Performs an adaptation to a particular adapter.
     *
     * @param resolve The handle being adapted.
     * @param adapter The adapting class.
     * @param monitor Progress monitor for blocking class.
     *
     * @return The adapter, or null if adaptation not possible.
     *
     * @throws IOException Any I/O errors that occur.
     */
    Object adapt(IResolve resolve, Class<? extends Object> adapter, IProgressMonitor monitor)
        throws IOException;
}
