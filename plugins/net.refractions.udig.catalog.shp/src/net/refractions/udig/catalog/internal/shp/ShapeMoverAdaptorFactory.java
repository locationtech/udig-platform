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
package net.refractions.udig.catalog.internal.shp;

import java.io.IOException;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class has the responsibility of looking at the provided
 * ShpServiceImpl and deciding if it is is movable.
 * <p>
 * ShpServiceImpl by itself can represent a shape file on disk
 * (which is movable) or on a web service (which is not).
 * 
 * @author Jody Garnett (Refractions Research)
 */
public class ShapeMoverAdaptorFactory implements IResolveAdapterFactory {

	/**
	 * Check the provided resolve (should be a ShpServiceImpl) and check
	 * if we provided the requested adapter.
	 */
    public <T> T adapt( IResolve resolve, Class<T> adapter, IProgressMonitor monitor )
            throws IOException {

        if (adapter.isAssignableFrom(ShapeMover.class)) {
        	// Note we create a new adapter each time; adapters
        	// are not supposed to hold much in the way of resources
        	// (ie the are lightweight)
        	// If that is too hard a programming model please let us
        	// know and we can cache this result (or provided session
        	// properties for you do store your adapter in
            return adapter.cast( new ShapeMover(resolve) );
        }
        return null;
    }


	/**
	 * We only know how to to the ShapeMover class, so client code
	 * can ask for ShapeMover by name; or the more generic ServiceMover.
	 * @param resolve IResolve handle; should be a ShpService
	 * @param adapter Interface we are being asked to adapt to
	 * @return true if the requested adapter is ShapeMover or SeviceMover.
	 */
	public boolean canAdapt(IResolve resolve, Class<? extends Object> adapter) {
		return adapter.isAssignableFrom(ShapeMover.class);
	}

}
