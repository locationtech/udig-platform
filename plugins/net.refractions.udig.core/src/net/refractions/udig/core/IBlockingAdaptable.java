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
package net.refractions.udig.core;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Objects that implement this interface can adapt to other objects but require that it be done is a
 * separate job because the adaptation may be blocking.
 * 
 * @see org.eclipse.core.runtime.IAdaptable
 * @author jeichar
 */
public interface IBlockingAdaptable {
    /**
     * The class will attempt to adapt into an object of the adapter class. This method may be
     * blocking.
     * 
     * @param adapter The class that the object will attempt to change into.
     * @param monitor A monitor to track the progress of the adaptation.
     * @return an object of type T or null if the adaptation is not possible.
     * @throws IOException may throw an IOException if the adaptation fails.
     */
    public <T> T getAdapter( Class<T> adapter, IProgressMonitor monitor ) throws IOException;

    /**
     * Returns true if this class can adapt to an object of type <code>Class<T></code>
     * <p>
     * It does not guarantee that the object can adapt, just that it believes it can
     * </p>
     * 
     * @param adapter the adapter to adapt to.
     * @return true if the object believes that it can adapt to an object class T.
     */
    public <T> boolean canAdaptTo( Class<T> adapter );
}
