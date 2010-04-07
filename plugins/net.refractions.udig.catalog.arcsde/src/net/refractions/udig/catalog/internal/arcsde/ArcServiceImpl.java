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
package net.refractions.udig.catalog.internal.arcsde;

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
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;
import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.arcsde.ArcSDEDataStoreFactory;
import org.geotools.arcsde.data.ArcSDEDataStore;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataStore;

/**
 * Connect to ArcSDE.
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class ArcServiceImpl extends IService {

    private URL url = null;

    private Map<String, Serializable> params = null;

    private Throwable msg = null;

    private volatile DataStore ds = null;

    private static final Lock dsLock = new UDIGDisplaySafeLock();

    private volatile List<ArcGeoResource> members = null;

    /**
     * Construct <code>PostGISServiceImpl</code>.
     * 
     * @param arg1
     * @param arg2
     */
    public ArcServiceImpl(URL arg1, Map<String, Serializable> arg2) {
        url = arg1;
        params = arg2;
    }

    /**
     * Required adaptions:
     * <ul>
     * <li>IServiceInfo.class
     * <li>List.class <IGeoResource>
     * </ul>
     * 
     * @see net.refractions.udig.catalog.IResolve#resolve(Class, IProgressMonitor)
     */
    public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(DataStore.class)) {
            return adaptee.cast(getDS(monitor));
        }
        return super.resolve(adaptee, monitor);
    }

    /**
     * @see net.refractions.udig.catalog.IResolve#canResolve(Class)
     */
    public <T> boolean canResolve(Class<T> adaptee) {
        return adaptee != null
                && (adaptee.isAssignableFrom(IServiceInfo.class)
                        || adaptee.isAssignableFrom(List.class) || adaptee
                        .isAssignableFrom(DataStore.class)) || super.canResolve(adaptee);
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<ArcGeoResource> resources(IProgressMonitor monitor) throws IOException {
        if (members == null) {
            synchronized (getDS(monitor)) {
                if (members == null) {
                    getDS(null); // load ds
                    members = new LinkedList<ArcGeoResource>();
                    String[] typenames = ds.getTypeNames();
                    if (typenames != null)
                        for (int i = 0; i < typenames.length; i++) {
                            members.add(new ArcGeoResource(this, typenames[i]));
                        }
                }
            }
        }
        return members;
    }

    @Override
    public IServiceArcSDEInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (IServiceArcSDEInfo) super.getInfo(monitor);
    }
    /*
     * @see net.refractions.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IServiceArcSDEInfo createInfo(IProgressMonitor monitor) throws IOException {
        getDS(monitor); // load ds
        if (ds == null) {
            return null; // no information if we cannot connect
        }
        synchronized (ds) {
            return new IServiceArcSDEInfo(ds);
            
        }
    }

    /*
     * @see net.refractions.udig.catalog.IService#getConnectionParams()
     */
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

    /**
     * This method will lazily connect to ArcSDE making use of the connection parameters.
     * 
     * @param monitor
     * @return
     * @throws IOException
     */
    DataStore getDS(IProgressMonitor monitor) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        if (ds == null) {
            synchronized (ArcSDEDataStoreFactory.class) {
                // please copy a better example from WFS
                if (ds == null) {
                    try {
                        ds = connect(monitor);
                    } catch (IOException e) {
                        msg = e;
                        throw e;
                    }
                }
            }
            IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
            ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
                    .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
        }
        return ds;
    }

    private ArcSDEDataStore connect(IProgressMonitor monitor) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        ArcSDEDataStoreFactory dsf = new ArcSDEDataStoreFactory();
        if (!dsf.canProcess(params)) {
            msg = new DataSourceException("Cannot connect to ArcSDE");
            return null;
        }
        return (ArcSDEDataStore) dsf.createDataStore(params);
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

    private class IServiceArcSDEInfo extends IServiceInfo {

        IServiceArcSDEInfo(DataStore resource) {
            super();
            String[] tns = null;
            try {
                tns = resource.getTypeNames();
            } catch (IOException e) {
                ArcsdePlugin.log(null, e);
                tns = new String[0];
            }
            keywords = new String[tns.length + 1];
            System.arraycopy(tns, 0, keywords, 1, tns.length);
            keywords[0] = "ArcSDE"; //$NON-NLS-1$

            try {
                schema = new URI("arcsde://geotools/gml"); //$NON-NLS-1$
            } catch (URISyntaxException e) {
                ArcsdePlugin.log(null, e);
            }
            icon = AbstractUIPlugin.imageDescriptorFromPlugin(ArcsdePlugin.ID,
                    "icons/obj16/arcsde_obj.gif"); //$NON-NLS-1$
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
            return "ARCSDE " + getIdentifier().getHost(); //$NON-NLS-1$
        }

    }
}
