/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.locationtech.udig.catalog.IResolve.Status;
import org.locationtech.udig.catalog.internal.CatalogImpl;
import org.locationtech.udig.catalog.internal.ResolveChangeEvent;
import org.locationtech.udig.catalog.internal.ResolveDelta;
import org.locationtech.udig.catalog.internal.mysql.MySQLPlugin;
import org.locationtech.udig.ui.ErrorManager;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.jdbc.JDBCDataStore;

/**
 * Provides an ISerivce so that MySQL can show up in service lists
 * <p>
 * This code mainly copies the PostgisServiceImpl
 * </p>
 * 
 * @author David Zwiers, Refractions Research
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySQLServiceImpl extends IService {

    private URL url = null;
    private Map<String, Serializable> params = null;
    protected Lock rLock = new UDIGDisplaySafeLock();

    private volatile List<MySQLGeoResource> members = null;
    private Throwable msg = null;
    private volatile JDBCDataStore ds = null;
    private Lock dsInstantiationLock = new UDIGDisplaySafeLock();

    /**
     * Construct <code>MySQLServiceImpl</code>.
     * 
     * @param arg1
     * @param arg2
     */
    public MySQLServiceImpl( URL arg1, Map<String, Serializable> arg2 ) {
        url = arg1;
        params = arg2;
    }

    /*
     * Required adaptations: <ul> <li>IServiceInfo.class <li>List.class <IGeoResource> </ul>
     * @see org.locationtech.udig.catalog.IService#resolve(java.lang.Class,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(JDBCDataStore.class))
            return adaptee.cast(getDS());
        /*
         * if (adaptee.isAssignab6leFrom(Connection.class)){ Connection connection; try { connection
         * = getDS().getConnectionPool().getConnection(); } catch (SQLException e) { throw
         * (IOException) new IOException(e.getLocalizedMessage()).initCause(e); } return
         * adaptee.cast(connection); }
         */
        return super.resolve(adaptee, monitor);
    }
    /*
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;
        return adaptee.isAssignableFrom(JDBCDataStore.class)
                || adaptee.isAssignableFrom(Connection.class) || super.canResolve(adaptee);
    }

    public void dispose( IProgressMonitor monitor ) {
        super.dispose(monitor);
        if( ds != null ){
            ds.dispose();
        }
        if( members != null ){
            members = null;
        }
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<MySQLGeoResource> resources( IProgressMonitor monitor ) throws IOException {

        JDBCDataStore ds = getDS();
        rLock.lock();
        try {
            if (members == null) {
                members = new LinkedList<MySQLGeoResource>();
                if (ds.getDataSource() != null) {
                    String[] typenames = ds.getTypeNames();
                    if (typenames != null) {
                        for( int i = 0; i < typenames.length; i++ ) {
                            members.add(new MySQLGeoResource(this, typenames[i]));
                        }
                    }
                }
            }
        } finally {
            rLock.unlock();
        }
        return members;
    }
    @Override
    public IServiceMySQLInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (IServiceMySQLInfo) super.getInfo(monitor);
    }
    /*
     * @see org.locationtech.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IServiceMySQLInfo createInfo( IProgressMonitor monitor ) throws IOException {
        JDBCDataStore dataStore = getDS(); // load DataStore
        if (dataStore == null) {
            return null; // could not connect to provide info
        }
        rLock.lock();
        try {
            return new IServiceMySQLInfo(dataStore);
        } finally {
            rLock.unlock();
        }
    }
    /*
     * @see org.locationtech.udig.catalog.IService#getConnectionParams()
     */
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }
    JDBCDataStore getDS() throws IOException {
        boolean changed = false;
        dsInstantiationLock.lock();
        try {
            if (ds == null) {
                changed = true;
                MySQLDataStoreFactory dsf = MySQLServiceExtension.getFactory();
                if (dsf.canProcess(params)) {
                    try {
                        ds = dsf.createDataStore(params);
                    } catch (IOException e) {
                        msg = e;
                        throw e;
                    }
                }
            }
        } finally {
            dsInstantiationLock.unlock();
        }
        if (changed) {
            IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
            ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
                    .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
        }
        if (ds != null) {
            MySQLPlugin.addDataStore(ds);
        }
        return ds;
    }
    /*
     * @see org.locationtech.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        if( ds == null ){
            return super.getStatus();
        }
        return Status.CONNECTED;
    }
    /*
     * @see org.locationtech.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return msg;
    }
    /*
     * @see org.locationtech.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return url;
    }

    
    private class IServiceMySQLInfo extends IServiceInfo {

        IServiceMySQLInfo( JDBCDataStore resource ) {
            super();
            String[] tns = null;
            try {
                tns = resource.getTypeNames();
            } catch (IOException e) {
                MySQLPlugin.log("Unable to read typenames", e); //$NON-NLS-1$
                tns = new String[0];
            }
            keywords = new String[tns.length + 1];

            System.arraycopy(tns, 0, keywords, 1, tns.length);
            keywords[0] = "mysql"; //$NON-NLS-1$

            try {
                schema = new URI("jdbc://mysql/gml"); //$NON-NLS-1$
            } catch (URISyntaxException e) {
                MySQLPlugin.log(null, e);
            }
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
            return "MySQL " + getDisplayID(); //$NON-NLS-1$
        }

    }
}
