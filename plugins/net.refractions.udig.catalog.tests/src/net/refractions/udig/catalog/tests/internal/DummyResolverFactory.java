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
package net.refractions.udig.catalog.tests.internal;

import java.io.IOException;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.tests.DummyGeoResource;
import net.refractions.udig.catalog.tests.DummyService;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A ResolverFactory for allowing IResolves to resolve to other classes.
 * <p>
 * This factory is used to teach DummyGeoResource how to adapt to the
 * the target "ResolvedTo" interface.
 * <p>
 * @author Jesse
 * @since 1.1.0
 */
public class DummyResolverFactory implements IResolveAdapterFactory {
    /**
     * Actually adapt to the requested interface.
     */
    public <T> T adapt( IResolve resolve, Class<T> adapter, IProgressMonitor monitor )
            throws IOException {
    	if( resolve instanceof DummyGeoResource ){
    		return adapter.cast( new ResolvedTo( resolve ) );
    	}
    	else if( resolve instanceof DummyService){
    		return adapter.cast( new ResolvedTo( resolve ) );
    	}
    	return null;
    }
    
    /**
     * CanAdapt is called by the framework to further test
     * resolve handles that have made it past the XML checks.
     * @param resolve handle to the resource being tested
     * @param 
     * @return true if this factory can adapt the provided resolve handle
     */
    public boolean canAdapt( IResolve resolve, Class<?> adapter ) {
    	if( resolve instanceof DummyGeoResource ){
    		return adapter.isAssignableFrom(ResolvedTo.class);
    	}
    	else if( resolve instanceof DummyService){
    		return adapter.isAssignableFrom(ResolvedTo.class);
    	}
    	return false;
    }

}
