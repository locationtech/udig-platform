/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.oracle;

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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.oracle.OracleNGDataStoreFactory;
import org.geotools.jdbc.JDBCDataStore;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.catalog.IResolveDelta;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.internal.CatalogImpl;
import org.locationtech.udig.catalog.internal.ResolveChangeEvent;
import org.locationtech.udig.catalog.internal.ResolveDelta;
import org.locationtech.udig.catalog.oracle.internal.Messages;
import org.locationtech.udig.core.internal.CorePlugin;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;

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

    public OracleServiceImpl(URL arg1, Map<String, Serializable> arg2) {
        if (arg1 == null) {
            String jdbc_url = OracleServiceExtension.getJDBCUrl(arg2);
            try {
                url = new URL(null, jdbc_url, CorePlugin.RELAXED_HANDLER);
            } catch (MalformedURLException e) {
                throw new NullPointerException(
                        "id provided and params could not be used to make one"); //$NON-NLS-1$
            }
        } else {
            url = arg1;
        }
        params = arg2;
        checkPort(params);
    }

    /**
     * Required adoptions:
     * <ul>
     * <li>IServiceInfo.class
     * <li>List.class <IGeoResource>
     * </ul>
     *
     * @see org.locationtech.udig.catalog.IService#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
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

    /**
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    @Override
    public <T> boolean canResolve(Class<T> adaptee) {
        if (adaptee == null)
            return false;
        return (adaptee.isAssignableFrom(JDBCDataStore.class)) || super.canResolve(adaptee);
    }

    @Override
    public void dispose(IProgressMonitor monitor) {
        super.dispose(monitor);

        if (ds != null) {
            ds.dispose();
        }
        if (members != null) {
            members = null;
        }
    }

    /**
     * @see org.locationtech.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public List<OracleGeoResource> resources(IProgressMonitor monitor) throws IOException {
        if (members == null) {
            rLock.lock();
            try {
                if (members == null) {
                    getDS(monitor); // load ds
                    members = new LinkedList<>();
                    String[] typenames = ds.getTypeNames();
                    if (typenames != null) {
                        for (int i = 0; i < typenames.length; i++) {
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
    public IServiceOracleInfo getInfo(IProgressMonitor monitor) throws IOException {
        return (IServiceOracleInfo) super.getInfo(monitor);
    }

    /**
     * @see org.locationtech.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
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

    /**
     * @see org.locationtech.udig.catalog.IService#getConnectionParams()
     */
    @Override
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

    JDBCDataStore getDS(IProgressMonitor monitor) throws IOException {
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
            ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog()).fire(
                    new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
        }
        return ds;
    }

    /**
     * @see org.locationtech.udig.catalog.IResolve#getStatus()
     */
    @Override
    public Status getStatus() {
        if (ds == null) {
            return super.getStatus();
        }
        return Status.CONNECTED;
    }

    /**
     * @see org.locationtech.udig.catalog.IResolve#getMessage()
     */
    @Override
    public Throwable getMessage() {
        return msg;
    }

    /**
     * @see org.locationtech.udig.catalog.IResolve#getIdentifier()
     */
    @Override
    public URL getIdentifier() {
        return url;
    }

    private class IServiceOracleInfo extends IServiceInfo {

        IServiceOracleInfo(JDBCDataStore resource) {
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

        @Override
        public String getDescription() {
            return getIdentifier().toString();
        }

        @Override
        public URI getSource() {
            try {
                return getIdentifier().toURI();
            } catch (URISyntaxException e) {
                // This would be bad
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }

        @Override
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
    private void checkPort(Map<String, Serializable> params) {
        String portKey = OracleNGDataStoreFactory.PORT.key;
        if (params != null && params.containsKey(portKey)
                && params.get(portKey) instanceof Integer) {
            Integer val = (Integer) params.get(portKey);
            params.remove(portKey);
            params.put(portKey, val.toString());
        }
    }
}
