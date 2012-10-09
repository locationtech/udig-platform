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
package net.refractions.udig.catalog.moved;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IForward;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class represents a service that has been moved to a new location.
 * <p>
 * For details please see the IForward interface.
 * </p>
 * @author Jody Garnett
 */
public class MovedService extends IService implements IForward  {
    ID identifier;
    ID forward;

    public MovedService( ID id, ID forward ){
        this.identifier = id;
        this.forward = forward;
    }
    public Map<String, Serializable> getConnectionParams() {
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put( MovedServiceExtention.ID_KEY, identifier );
        params.put( MovedServiceExtention.FORWARD_KEY, forward );
        return params;
    }

    protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        return null;
    }

    @Override
    public List< ? extends IGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        return Collections.emptyList();
    }
    
    public URL getIdentifier() {
        return identifier.toURL();
    }
    public ID getID() {
        return identifier;
    }
    public Throwable getMessage() {
        return new IllegalStateException("Service has moved to "+forward );
    }

    public Status getStatus() {
        return Status.BROKEN;
    }

    public ID getForward() {
        return forward;
    }
}
