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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.arcsde.ArcSDEDataStoreFactory;
import org.geotools.arcsde.data.ArcSDEDataStore;
import org.geotools.arcsde.data.ArcSDEDataStoreConfig;
import org.geotools.arcsde.session.ISessionPool;
import org.geotools.data.DataStore;

/**
 * Connect to ArcSDE and list Vector contents.
 * 
 * @author Gabriel Roldan
 * @since 1.2
 */
class ArcSDEVectorService extends ArcSDEService {

    private volatile ArcSDEDataStore ds;

    /**
     * Construct <code>PostGISServiceImpl</code>.
     * 
     * @param arg1
     * @param params
     */
    public ArcSDEVectorService( ArcServiceImpl service ) {
        super(service);
    }

    @Override
    protected List<IGeoResource> createMembers( IProgressMonitor monitor ) throws IOException {
        getDS(null); // load ds

        String[] typenames = ds.getTypeNames();
        List<IGeoResource> members = new ArrayList<IGeoResource>(typenames.length);

        ArcSDEVectorGeoResource resource;
        for( int i = 0; i < typenames.length; i++ ) {
            resource = new ArcSDEVectorGeoResource(service, typenames[i]);
            members.add(resource);
        }
        return members;
    }

    /**
     * This method will lazily connect to ArcSDE making use of the connection parameters.
     * 
     * @param monitor
     * @return
     * @throws IOException
     */
    ArcSDEDataStore getDS( IProgressMonitor monitor ) throws IOException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        if (ds == null) {
            synchronized (ArcSDEDataStoreFactory.class) {
                // please copy a better example from WFS
                if (ds == null) {
                    final ISessionPool sessionPool = getSessionPool();
                    try {
                        ds = connect(sessionPool, monitor);
                    } catch (IOException e) {
                        throw e;
                    }
                }
            }
            // IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
            // ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
            // .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
        }
        return ds;
    }

    private ArcSDEDataStore connect( final ISessionPool sessionPool, IProgressMonitor monitor )
            throws IOException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        ArcSDEDataStoreConfig dsConfig = service.getDataStoreConfig();
        String namespaceUri = dsConfig.getNamespaceUri();
        String versionName = dsConfig.getVersion();
        boolean allowNonSpatialTables = dsConfig.isAllowNonSpatialTables();
        ArcSDEDataStore ds = new ArcSDEDataStore(sessionPool, namespaceUri, versionName,
                allowNonSpatialTables);
        return ds;
    }

    public DataStore getDataStore( IProgressMonitor monitor ) throws IOException {
        return getDS(monitor);
    }

}
