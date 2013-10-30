/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.arcsde;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.IGeoResource;

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
