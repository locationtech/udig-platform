/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.ui;

import java.io.IOException;

import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.core.IBlockingAdaptable;

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
     * @see org.locationtech.udig.ui.operations.IBlockingAdaptable#getAdapter(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T getAdapter( Class<T> adapter, IProgressMonitor monitor ) throws IOException {
        return resolve.resolve(adapter, monitor);
    }

    /**
     * @see org.locationtech.udig.ui.operations.IBlockingAdaptable#canAdaptTo(java.lang.Class)
     */
    public <T> boolean canAdaptTo( Class<T> adapter ) {
        return resolve.canResolve(adapter);
    }

}
