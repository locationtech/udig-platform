/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.internal;

import java.io.IOException;

import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveAdapterFactory;
import org.locationtech.udig.catalog.tests.DummyGeoResource;
import org.locationtech.udig.catalog.tests.DummyService;

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
