/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
