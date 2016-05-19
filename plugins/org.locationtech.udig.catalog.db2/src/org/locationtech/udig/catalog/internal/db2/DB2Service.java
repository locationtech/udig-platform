/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2005, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.db2;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.db2.DB2Plugin;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.DataStore;
import org.geotools.data.db2.DB2NGDataStoreFactory;
import org.geotools.jdbc.JDBCDataStore;

/**
 * Service handle for the DB2 Universal Database.
 * 
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 * @author David Adler, Adtech Geospatial,dadler@adtechgeospatial.com
 * @since 1.0.1
 */
public class DB2Service extends IService {

    /** underlying datastore */
    private volatile JDBCDataStore ds;

    /** members (tables) */
    private volatile List<DB2GeoResource> members;

    /** service url * */
    private URL url;

    /** connection parameters * */
    private Map<String, Serializable> params;

    /** any thrown exception * */
    Throwable msg;

    protected Lock rLock = new UDIGDisplaySafeLock();

    private Lock dsInstantiationLock = new UDIGDisplaySafeLock();

    public DB2Service( URL id, Map<String, Serializable> params2 ) {
        url = id;
        this.params = params2;
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null
                && (adaptee.isAssignableFrom(DataStore.class) || super.canResolve(adaptee));
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(DataStore.class)) {
            return adaptee.cast(getDataStore(monitor)); // use the monitor!
        }
        return super.resolve(adaptee, monitor);
    }
    
    public void dispose( IProgressMonitor monitor ) {
        super.dispose(monitor);
        if (ds != null ){
            ds.dispose();
            ds = null;
        }
    }

    @Override
    public List<DB2GeoResource> resources( IProgressMonitor monitor ) throws IOException {

        if (members == null) {
            JDBCDataStore ds = getDataStore(monitor);
            if (ds == null)
                return null;

            rLock.lock();
            try {
                if (members == null) {
                    members = new ArrayList<DB2GeoResource>();

                    String[] names = ds.getTypeNames();
                    if (names == null || names.length == 0)  // If nothing found, reset so we can try again
                    {
                        members = null;
                        return members;
                    }
                    for( int i = 0; i < names.length; i++ ) {
                        members.add(new DB2GeoResource(this, names[i]));
                    }
                }
            } finally {
                rLock.unlock();
            }
        } else {
            if (!(monitor == null))
                monitor.done();
        }
        return members;
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return this.params;
    }

    public Status getStatus() {
        if( ds == null ){
            return super.getStatus();
        }
        return Status.CONNECTED;
    }

    public Throwable getMessage() {
        return msg;
    }

    public URL getIdentifier() {
        return url;
    }

    @Override
    public DB2ServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (DB2ServiceInfo) super.getInfo(monitor);
    }
    @Override
    protected DB2ServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        JDBCDataStore ds = getDataStore(monitor);
        if (ds == null) {
            return null; // could not connect
        }
        rLock.lock();
        try {
            return new DB2ServiceInfo(null);
        } finally {
            rLock.unlock();
        }
    }
    JDBCDataStore getDataStore( IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (this.ds == null) {
            dsInstantiationLock.lock();
            try {
                if (ds == null) {
                    // We are using DB2NGDataStoreFactory as we do not need to look up in
                    // a JNDI context for a shared connection pool - we will end up using a
                    // an internal connection pool supplied by GeoTools DBCPDataSource.
                    //
                    DB2NGDataStoreFactory dsf = new DB2NGDataStoreFactory();
                    try {
                        // We expect the port value (key '3') to be a String but some of the
                        // extensions (ArcServiceExtension)
                        // change this from a String to an Integer which causes us to fail.
                        // In order to cope with this, we make a local copy of the parameters and
                        // force the port
                        // value to be a String.
                        // Maybe we should change DB2DataStoreFactory.canProcess to accept either
                        // Integer or
                        // String as valid for port.
                        Map<String, Serializable> paramsLocal = new HashMap<String, Serializable>();
                        for( String key : this.params.keySet() ) {
                            String value = this.params.get(key).toString();
                            paramsLocal.put(key, value);
                        }
                        if (dsf.canProcess(paramsLocal)) {
                            this.ds = (JDBCDataStore) dsf.createDataStore(paramsLocal);
                        }
                    } catch (IOException e) {
                        msg = e;
                        throw e;
                    }
                }
            } finally {
                dsInstantiationLock.unlock();
            }
        }

        return this.ds;
    }

    class DB2ServiceInfo extends IServiceInfo {

        public DB2ServiceInfo( IProgressMonitor monitor ) {
            super();

            // make the type names part of the keyword set
            String[] tns = null;
            try {
                tns = getDataStore(monitor).getTypeNames();
            } catch (IOException e) {
                CatalogPlugin.log(e.getLocalizedMessage(), e);
                tns = new String[0];
            }
            if (tns == null)
                tns = new String[]{};

            keywords = new String[tns.length + 1];
            System.arraycopy(tns, 0, keywords, 1, tns.length);
            keywords[0] = "db2"; //$NON-NLS-1$

            try {
                schema = new URI("jdbc://db2/gml"); //$NON-NLS-1$
            } catch (URISyntaxException e) {
                CatalogPlugin.log(e.getLocalizedMessage(), e);
            }

            icon = AbstractUIPlugin.imageDescriptorFromPlugin(DB2Plugin.ID,
                    "icons/obj16/db2_16.gif"); //$NON-NLS-1$
        }

        public String getDescription() {
            return getIdentifier().toString();
        }

        public URI getSource() {
            try {
                return getIdentifier().toURI();
            } catch (URISyntaxException e) {
                // This would be bad
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }

        public String getTitle() {
            return "DB2 " + getIdentifier().getHost(); //$NON-NLS-1$
        }
    }
}
