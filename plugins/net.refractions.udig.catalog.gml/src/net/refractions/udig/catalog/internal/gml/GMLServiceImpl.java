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
package net.refractions.udig.catalog.internal.gml;

import java.io.IOException;
import java.io.Serializable;
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
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;
import net.refractions.udig.ui.ErrorManager;
import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.data.gml.GMLDataStore;
import org.geotools.data.gml.GMLDataStoreFactory;

/**
 * Connect to a shapefile
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class GMLServiceImpl extends IService {

    static final Lock dsInstantationLock = new UDIGDisplaySafeLock();
    final Lock dsLock = new UDIGDisplaySafeLock();
    private URL url = null;
    private Map<String, Serializable> params = null;
    /**
     * Construct <code>ShpServiceImpl</code>.
     *
     * @param arg1
     * @param arg2
     */
    public GMLServiceImpl( URL arg1, Map<String, Serializable> arg2 ) {
        url = arg1;
        params = arg2;
    }

    /*
     * Required adaptions: <ul> <li>IServiceInfo.class <li>List.class <IGeoResource> </ul>
     *
     * @see net.refractions.udig.catalog.IService#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified" ); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(GMLDataStore.class)) {
            return adaptee.cast(getDS(monitor));
        }
        return super.resolve(adaptee, monitor);
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null
                && (adaptee.isAssignableFrom(GMLDataStore.class) || super.canResolve(adaptee));
    }

    public void dispose( IProgressMonitor monitor ) {
        if (members == null)
            return;

        int steps = (int) ((double) 99 / (double) members.size());
        for( IResolve resolve : members ) {
            try {
                SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, steps);
                resolve.dispose(subProgressMonitor);
                subProgressMonitor.done();
            } catch (Throwable e) {
                ErrorManager.get().displayException(e,
                        "Error disposing members of service: " + getIdentifier(), CatalogPlugin.ID); //$NON-NLS-1$
            }
        }
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<GMLGeoResourceImpl> resources( IProgressMonitor monitor ) throws IOException {

        if (members == null) {
            dsLock.lock();
            try {
                if (members == null) {
                    members = new LinkedList<GMLGeoResourceImpl>();
                    String[] typenames = getDS(monitor).getTypeNames();
                    if (typenames != null)
                        for( int i = 0; i < typenames.length; i++ ) {
                            members.add(new GMLGeoResourceImpl(this, typenames[i]));
                        }
                }
            } finally {
                dsLock.unlock();
            }
        }
        return members;
    }
    private volatile List<GMLGeoResourceImpl> members = null;

    /*
     * @see net.refractions.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        getDS(monitor); // load ds
        if (info == null && ds != null) {
            dsLock.lock();
            try {
                if (info == null) {
                    info = new IServiceShpInfo();
                }
            } finally {
                dsLock.unlock();
            }
            IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
            ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
                    .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
        }
        return info;
    }
    private volatile IServiceInfo info = null;
    /*
     * @see net.refractions.udig.catalog.IService#getConnectionParams()
     */
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }
    private Throwable msg = null;
    private volatile DataStore ds = null;
    DataStore getDS( IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        if (ds == null) {
            dsInstantationLock.lock();
            try {
                if (ds == null) {
                    GMLDataStoreFactory dsf = new GMLDataStoreFactory();
                    if (dsf.canProcess(params)) {
                        try {
                            ds = (DataStore) dsf.createDataStore(params);
                        } catch (IOException e) {
                            msg = e;
                            throw e;
                        }
                    }
                }
            } finally {
                dsInstantationLock.unlock();
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
        return msg != null ? Status.BROKEN : ds == null ? Status.NOTCONNECTED : Status.CONNECTED;
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

    private class IServiceShpInfo extends IServiceInfo {

        IServiceShpInfo() {
            super();
            try {
                keywords = new String[]{".shp", "Shapefile", //$NON-NLS-1$ //$NON-NLS-2$
                        ds.getTypeNames()[0]};
            } catch (IOException ex) {
                keywords = new String[]{".shp", "Shapfile" //$NON-NLS-1$//$NON-NLS-2$
                };
            }

            try {
                schema = new URI("shp://www.opengis.net/gml"); //$NON-NLS-1$
            } catch (URISyntaxException e) {
                GmlPlugin.log(null, e);
                schema = null;
            }
        }

        public String getDescription() {
            return getIdentifier().toString();
        }

        public String getTitle() {
            return getIdentifier().getFile();
        }
    }
}
