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
package org.locationtech.udig.catalog.internal.wfs;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.catalog.IResolveDelta;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.internal.CatalogImpl;
import org.locationtech.udig.catalog.internal.ResolveChangeEvent;
import org.locationtech.udig.catalog.internal.ResolveDelta;
import org.locationtech.udig.catalog.wfs.internal.Messages;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;

/**
 * Handle for a WFS service.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class WFSServiceImpl extends IService {

    private URL identifier = null;

    private Map<String, Serializable> params = null;

    private volatile List<WFSGeoResourceImpl> members = null;

    protected Lock rLock = new UDIGDisplaySafeLock();

    private Throwable msg = null;

    private volatile WFSDataStoreFactory dsf;

    private volatile WFSDataStore ds = null;

    private static final Lock dsLock = new UDIGDisplaySafeLock();

    public WFSServiceImpl(URL identifier, Map<String, Serializable> dsParams) {
        this.identifier = identifier;
        this.params = dsParams;
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
        if (adaptee == null)
            return null;
        if (adaptee.isAssignableFrom(WFSDataStore.class)) {
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
        return adaptee.isAssignableFrom(WFSDataStore.class) || super.canResolve(adaptee);
    }

    @Override
    public void dispose(IProgressMonitor monitor) {
        super.dispose(monitor);
        if (members != null) {
            members = null;
        }
        if (ds != null) {
            ds.dispose();
            ds = null;
        }
    }

    /**
     * @see org.locationtech.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public List<WFSGeoResourceImpl> resources(IProgressMonitor monitor) throws IOException {

        if (members == null) {
            rLock.lock();
            try {
                if (members == null) {
                    getDS(monitor); // load ds
                    members = new LinkedList<>();
                    String[] typenames = ds.getTypeNames();
                    if (typenames != null)
                        for (int i = 0; i < typenames.length; i++) {
                            try {
                                members.add(new WFSGeoResourceImpl(this, typenames[i]));
                            } catch (Exception e) {
                                WfsPlugin.log("", e); //$NON-NLS-1$
                            }
                        }
                }
            } finally {
                rLock.unlock();
            }
        }
        return members;
    }

    @Override
    public IServiceInfo getInfo(IProgressMonitor monitor) throws IOException {
        return super.getInfo(monitor);
    }

    /**
     * @see org.locationtech.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
        DataStore dataStore = getDS(monitor); // load ds
        if (dataStore == null) {
            return null; // could not connect no info for you
        }
        rLock.lock();
        try {
            return new WFSServiceInfo(this, ds);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * @see org.locationtech.udig.catalog.IService#getConnectionParams()
     */
    @Override
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

    WFSDataStore getDS(IProgressMonitor monitor) throws IOException {
        if (ds == null) {
            if (monitor == null)
                monitor = new NullProgressMonitor();
            monitor.beginTask(Messages.WFSServiceImpl_task_name, 3);
            dsLock.lock();
            monitor.worked(1);
            try {
                if (ds == null) {
                    if (dsf == null) {
                        dsf = new WFSDataStoreFactory();
                    }
                    monitor.worked(1);
                    if (dsf.canProcess(params)) {
                        monitor.worked(1);
                        try {
                            // TODO Review : explicitly ask for WFS 1.0
                            URL url = (URL) params.get(WFSDataStoreFactory.URL.key);
                            url = WFSDataStoreFactory.createGetCapabilitiesRequest(url);
                            params = new HashMap<>(params);
                            params.put(WFSDataStoreFactory.URL.key, url);
                            ds = dsf.createDataStore(params);
                            monitor.worked(1);
                        } catch (IOException e) {
                            msg = e;
                            throw e;
                        }
                    }
                }
            } finally {
                dsLock.unlock();
                monitor.done();
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
        return identifier;
    }
}
