/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.catalog.internal.ui;

import java.io.IOException;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.core.IBlockingAdaptable;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * An IAdaptable object that adapts IResolves to other objects. This is used by the framework. The
 * IResolveAdapterFactory will adapt IResolves to objects of this class so that the frame work can
 * try to
 * 
 * @author jeichar
 */
public class IResolveAdaptable implements IBlockingAdaptable {

    private IResolve resolve;

    /**
     * 
     */
    public IResolveAdaptable( IResolve resolve ) {
        this.resolve = resolve;
    }

    /**
     * @see net.refractions.udig.ui.operations.IBlockingAdaptable#getAdapter(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T getAdapter( Class<T> adapter, IProgressMonitor monitor ) throws IOException {
        return resolve.resolve(adapter, monitor);
    }

    /**
     * @see net.refractions.udig.ui.operations.IBlockingAdaptable#canAdaptTo(java.lang.Class)
     */
    public <T> boolean canAdaptTo( Class<T> adapter ) {
        return resolve.canResolve(adapter);
    }

}
