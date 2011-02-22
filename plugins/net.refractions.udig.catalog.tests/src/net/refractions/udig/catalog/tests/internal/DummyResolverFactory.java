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

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.tests.DummyGeoResource;
import net.refractions.udig.catalog.tests.DummyService;

/**
 * A ResolverFactory for allowing IResolves to resolve to other classes.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class DummyResolverFactory implements IResolveAdapterFactory {

    public Object adapt( IResolve resolve, Class adapter, IProgressMonitor monitor )
            throws IOException {
        if( !canAdapt(resolve, adapter))
            return null;

        if( adapter==ResolvedTo.class)
            return new ResolvedTo();
        return null;
    }

    public boolean canAdapt( IResolve resolve, Class adapter ) {
        return ResolvedTo.class==adapter && ((resolve instanceof DummyGeoResource) || (resolve instanceof DummyService));
    }

}
