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

abstract class ArcSDEService {

    protected final ArcServiceImpl service;

    protected final ArcSDEConnectionConfig connectionConfig;

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
                    ArcSDEConnectionConfig config = connectionConfig;
                    ISessionPoolFactory fact = SessionPoolFactory.getInstance();
                    sessionPool = SharedSessionPool.getInstance(config, fact);
                }
            }
        }
        return sessionPool;
    }

    @Override
    protected void finalize() {
        if (sessionPool != null) {
            sessionPool.close();
        }
    }
}
