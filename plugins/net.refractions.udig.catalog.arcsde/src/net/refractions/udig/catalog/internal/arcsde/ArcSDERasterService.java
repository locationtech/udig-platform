/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2010, Refractions Research Inc.
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
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.arcsde.session.ISession;
import org.geotools.arcsde.session.ISessionPool;
import org.geotools.arcsde.session.UnavailableConnectionException;

/**
 * List ArcSDE raster contents.
 * 
 * @author Gabriel Roldan
 * @since 1.2
 */
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
