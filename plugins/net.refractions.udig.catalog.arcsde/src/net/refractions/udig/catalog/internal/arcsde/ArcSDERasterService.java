package net.refractions.udig.catalog.internal.arcsde;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.arcsde.session.ISession;
import org.geotools.arcsde.session.ISessionPool;
import org.geotools.arcsde.session.UnavailableConnectionException;

class ArcSDERasterService extends ArcSDEService {

    public ArcSDERasterService( ArcServiceImpl service ) {
        super(service);
    }

    @Override
    protected List<IGeoResource> createMembers( IProgressMonitor monitor ) throws IOException {
        ISessionPool pool = getSessionPool();
        ISession session;
        try {
            session = pool.getSession(false);
        } catch (UnavailableConnectionException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        }
        List<IGeoResource> members;
        try {
            List<String> rasterColumns = session.getRasterColumns();
            members = new ArrayList<IGeoResource>(rasterColumns.size());
            ArcSDERasterGeoResource resource;
            for( String rasterName : rasterColumns ) {
                resource = new ArcSDERasterGeoResource(service, rasterName);
                members.add(resource);
            }
        } finally {
            session.dispose();
        }
        return members;
    }

}
