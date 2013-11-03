/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.moved;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IForward;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;

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
