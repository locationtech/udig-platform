/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.catalog.internal.oracle;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.IResolve.Status;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;
import net.refractions.udig.catalog.oracle.internal.Messages;
import net.refractions.udig.core.internal.CorePlugin;
import net.refractions.udig.ui.ErrorManager;
import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.oracle.OracleNGDataStoreFactory;
import org.geotools.jdbc.JDBCDataStore;

/**
 * Service handle representing an oracle database.
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class OracleServiceImpl extends IService {

    private URL url = null;
    private Map<String, Serializable> params = null;
    private Throwable msg = null;
    private volatile JDBCDataStore ds = null;
    protected Lock rLock = new UDIGDisplaySafeLock();
    private static final Lock dsLock = new UDIGDisplaySafeLock();
    public OracleServiceImpl( URL arg1, Map<String, Serializable> arg2 ) {
        if (arg1 == null) {
            String jdbc_url = OracleServiceExtension.getJDBCUrl(arg2);
            try {
                url = new URL(null, jdbc_url, CorePlugin.RELAXED_HANDLER);
            } catch (MalformedURLException e) {
                throw new NullPointerException(
                        "id provided and params could not be used to make one");
            }
        } else {
            url = arg1;
        }
        params = arg2;
        checkPort(params);
    }

    /*
     * Required adaptions: <ul> <li>IServiceInfo.class <li>List.class <IGeoResource> </ul>
     * @see net.refractions.udig.catalog.IService#resolve(java.lang.Class,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }

        if (adaptee.isAssignableFrom(JDBCDataStore.class)) {
            return adaptee.cast(getDS(monitor));
        }
        return super.resolve(adaptee, monitor);
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;
        return (adaptee.isAssignableFrom(JDBCDataStore.class)) || super.canResolve(adaptee);
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
     * @see net.refractions.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<OracleGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        if (members == null) {
            rLock.lock();
            try {
                if (members == null) {
                    getDS(monitor); // load ds
                    members = new LinkedList<OracleGeoResource>();
                    String[] typenames = ds.getTypeNames();
                    if (typenames != null){
                        for( int i = 0; i < typenames.length; i++ ) {
                            String typeName = typenames[i];
                            members.add(new OracleGeoResource(this, typeName));
                        }
                    }
                }
            } finally {
                rLock.unlock();
            }
        }
        return members;
    }
    private volatile List<OracleGeoResource> members = null;

    @Override
    public IServiceOracleInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (IServiceOracleInfo) super.getInfo(monitor);
    }
    /*
     * @see net.refractions.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        JDBCDataStore dataStore = getDS(monitor); // load ds
        if (dataStore == null) {
            return null;
        }
        rLock.lock();
        try {
            info = new IServiceOracleInfo(dataStore);

        } finally {
            rLock.unlock();

        }
        return info;
    }
    /*
     * @see net.refractions.udig.catalog.IService#getConnectionParams()
     */
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }
    JDBCDataStore getDS( IProgressMonitor monitor ) throws IOException {
        if (ds == null) {
            dsLock.lock();
            try {
                if (ds == null) {
                    OracleNGDataStoreFactory dsf = new OracleNGDataStoreFactory();
                    checkPort(params);
                    assert params.get("port") instanceof String; //$NON-NLS-1$
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
                dsLock.unlock();
            }
            IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
            ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
                    .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
        }
        return ds;
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        if( ds == null ){
            return super.getStatus();
        }
        return Status.CONNECTED;
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return msg;
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return url;
    }

    private class IServiceOracleInfo extends IServiceInfo {

        IServiceOracleInfo( JDBCDataStore resource ) {
            super();
            String[] tns = null;
            try {
                tns = resource.getTypeNames();
            } catch (IOException e) {
                OraclePlugin.log(null, e);
                tns = new String[0];
            }
            keywords = new String[tns.length + 1];
            System.arraycopy(tns, 0, keywords, 1, tns.length);
            keywords[0] = "oracle"; //$NON-NLS-1$

            try {
                schema = new URI("jdbc://oracle/gml"); //$NON-NLS-1$
            } catch (URISyntaxException e) {
                OraclePlugin.log(null, e);
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
            return Messages.OracleServiceImpl_oracle_spatial + getIdentifier().getHost();
        }
    }

    /**
     * Checks to make sure the port hasn't been switched from String to Integer and corrects if
     * necessary.
     * 
     * @param params Parameters object
     */
    private void checkPort( Map<String, Serializable> params ) {
        String portKey = OracleNGDataStoreFactory.PORT.key;
        if (params != null && params.containsKey(portKey) && params.get(portKey) instanceof Integer) {
            Integer val = (Integer) params.get(portKey);
            params.remove(portKey);
            params.put(portKey, val.toString());
        }
    }
}
