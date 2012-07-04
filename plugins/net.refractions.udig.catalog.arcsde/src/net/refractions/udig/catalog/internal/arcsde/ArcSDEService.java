/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2010-2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.arcsde;

import java.io.IOException;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.arcsde.jndi.SharedSessionPool;
import org.geotools.arcsde.session.ArcSDEConnectionConfig;
import org.geotools.arcsde.session.ISessionPool;
import org.geotools.arcsde.session.ISessionPoolFactory;
import org.geotools.arcsde.session.SessionPoolFactory;

/**
 * Connect to ArcSDE.
 * 
 * @author Gabriel Roldan
 * @since 1.2
 */
abstract class ArcSDEService {
    
    /** Parent ArcServiceImpl */
    protected final ArcServiceImpl service;

    /** Shared ArcSDEConnectionConfig from service */
    protected final ArcSDEConnectionConfig connectionConfig;

    /**
     * Our own session pool used to submit requests to ArcSDE.
     * <p>
     * We use SharedSessionPool in order to not grab too many resources.
     */
    private volatile ISessionPool sessionPool;

    public ArcSDEService( ArcServiceImpl service ) {
        this.service = service;
        this.connectionConfig = service.getConnectionConfig();
    }

    public final List<IGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        List<IGeoResource> members = createMembers(monitor);
        return members;
    }

    protected abstract List<IGeoResource> createMembers( IProgressMonitor monitor )
            throws IOException;

    protected ISessionPool getSessionPool() throws IOException {
        if (sessionPool == null) {
            synchronized (connectionConfig) {
                if (sessionPool == null) {
                    ISessionPoolFactory fact = SessionPoolFactory.getInstance();
                    sessionPool = SharedSessionPool.getInstance(connectionConfig, fact);
                }
            }
        }
        return sessionPool;
    }
    
    public void dispose(){
        if (sessionPool != null) {
            sessionPool.close();
        }
    }
    @Override
    protected void finalize() {
        dispose();
    }
}
