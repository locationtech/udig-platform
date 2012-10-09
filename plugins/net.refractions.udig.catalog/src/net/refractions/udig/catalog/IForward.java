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
package net.refractions.udig.catalog;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * IForward is a place holder stored in the catalog after a
 * service has been moved.
 * <p>
 * The catalog will issue a REPLACE event as a service is moved. 
 * IForward is used as a place holder in the catalog to help client
 * code that was not around to listen to the event during the moved.
 * <p>
 * When client code (such as a layer) tries to find the IResolve
 * with this ID the catalog will turn around and look up the new ID
 * as returned by IForward.getForward().
 * <p>
 * @author Jody Garnett (Refractions Research Inc)
 */
public interface IForward extends IResolve {
    /**
     * IForward is not going to resolve to anything
     * @return false
     */
    public <T> boolean canResolve( Class<T> adaptee );

    /**
     * IForward does not maintain any resources.
     */
    public void dispose( IProgressMonitor monitor );
    
    /**
     * This is the original ID of the resource before it was moved.
     * @return ID for this IResolve, should not be null. 
     */
    public URL getIdentifier();
    
    /**
     * This is the ID of the replacement resource.
     * @return ID of the replacement resource
     */
    public ID getForward();
    
    /** 
     * IForward cannot connect; ever.
     * @return Status.NOTCONNECTED
     */
    public Status getStatus();

    /**
     * IForward should contain any children; to locate children
     * please use the the getForward() ID to connect
     * to the actual service.
     */
    public List<IResolve> members( IProgressMonitor monitor ) throws IOException;

    /**
     * IForward cannot resolve to any thing.
     * @return null
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException;
}
