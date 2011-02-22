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

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.DataStore;
import org.geotools.data.arcsde.ArcSDEDataStoreFactory;

/**
 * Connect to ArcSDE.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class ArcServiceImpl extends IService {

    private URL url = null;
    private Map<String, Serializable> params = null;
    /**
     * Construct <code>PostGISServiceImpl</code>.
     *
     * @param arg1
     * @param arg2
     */
    public ArcServiceImpl( URL arg1, Map<String, Serializable> arg2 ) {
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
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(DataStore.class)) {
            return adaptee.cast(getDS(monitor));
        }
        return super.resolve( adaptee, monitor);
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null
                && (adaptee.isAssignableFrom(IServiceInfo.class)
                        || adaptee.isAssignableFrom(List.class) || adaptee
                        .isAssignableFrom(DataStore.class));
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<ArcGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        if (members == null) {
            synchronized (getDS(monitor)) {
                if (members == null) {
                    getDS(null); // load ds
                    members = new LinkedList<ArcGeoResource>();
                    String[] typenames = ds.getTypeNames();
                    if (typenames != null)
                        for( int i = 0; i < typenames.length; i++ ) {
                            members.add(new ArcGeoResource(this, typenames[i]));
                        }
                }
            }
        }
        return members;
    }
    private volatile List<ArcGeoResource> members = null;

    /*
     * @see net.refractions.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        getDS(monitor); // load ds
        if (info == null && ds != null) {
            synchronized (ds) {
                if (info == null) {
                    info = new IServiceArcSDEInfo(ds);
                }
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
    DataStore getDS(IProgressMonitor monitor) throws IOException {
        if (ds == null) {
            synchronized (ArcSDEDataStoreFactory.class) {
                // please copy a better example from WFS
                if (ds == null) {
                    ArcSDEDataStoreFactory dsf = new ArcSDEDataStoreFactory();
                    if (dsf.canProcess(params)) {
                        try {
                            ds = dsf.createDataStore(params);
                        } catch (IOException e) {
                            msg = e;
                            throw e;
                        }
                    }
                }
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

    private class IServiceArcSDEInfo extends IServiceInfo {

        IServiceArcSDEInfo( DataStore resource ) {
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
            keywords[0] = "postgis"; //$NON-NLS-1$

            try {
                schema = new URI("jdbc://arcsde/gml"); //$NON-NLS-1$
            } catch (URISyntaxException e) {
                ArcsdePlugin.log(null, e);
            }
        }

        public String getDescription() {
            return getIdentifier().toString();
        }

        public URL getSource() {
            return getIdentifier();
        }

        public String getTitle() {
            return "ARCSDE " + getIdentifier().getHost(); //$NON-NLS-1$
        }

        /*
         * @see net.refractions.udig.catalog.IServiceInfo#getIcon()
         */
        public ImageDescriptor getIcon() {
            return AbstractUIPlugin.imageDescriptorFromPlugin(ArcsdePlugin.ID,
                    "icons/obj16/arcsde_obj.gif"); //$NON-NLS-1$
        }
    }
}
