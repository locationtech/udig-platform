/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 20042-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.internal.arcsde;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.IResolve.Status;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.arcsde.data.ArcSDEDataStoreConfig;
import org.geotools.arcsde.session.ArcSDEConnectionConfig;
import org.geotools.data.DataStore;

/**
 * Connect to ArcSDE.
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class ArcServiceImpl extends IService {
    
    /** Identifier used in Catalog */
    private final URL url;
    
    /** Current connection error - if any */
    private Throwable msg;

    /**
     * List vector contents
     */
    private ArcSDEVectorService vectorService;
    /**
     * List raster contents
     */
    private ArcSDERasterService rasterService;

    /** List of child members, provided by {@link #vectorService} and {@link #rasterService}. */
    private volatile List<IGeoResource> members;
    
    /**
     * ArcSDE DataStore details used for connection.
     */
    private final ArcSDEDataStoreConfig dataStoreConfig;

    /**
     * Construct <code>PostGISServiceImpl</code>.
     * 
     * @param url
     * @param params
     */
    public ArcServiceImpl( URL url, Map<String, Serializable> params ) {
        this.url = url;
        this.dataStoreConfig = new ArcSDEDataStoreConfig(params);
        vectorService = new ArcSDEVectorService(this);
        rasterService = new ArcSDERasterService(this);
    }

    /**
     * @see IService#resolve(Class, IProgressMonitor)
     */
    public <T> T resolve( final Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(DataStore.class)) {
            try {
                return adaptee.cast(vectorService.getDataStore(monitor));
            } catch (IOException e) {
                msg = e;
                throw e;
            } catch (Exception e) {
                msg = e;
                throw (IOException) (new IOException(e.getLocalizedMessage()).initCause(e));
            }
        }
        // if (rasterService != null && rasterService.canResolve(adaptee)) {
        // return rasterService.resolve(adaptee, monitor);
        // }
        return super.resolve(adaptee, monitor);
    }

    /**
     * @see net.refractions.udig.catalog.IResolve#canResolve(Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null) {
            return false;
        }
        if (adaptee.isAssignableFrom(DataStore.class)) {
            return true;
        }
        // if (rasterService != null && rasterService.canResolve(adaptee)) {
        // return true;
        // }
        return super.canResolve(adaptee);
    }
    
    /**
     * @see IService#resources(IProgressMonitor)
     */
    @Override
    public List<IGeoResource> resources( final IProgressMonitor monitor ) throws IOException {
        if (members == null) {
            synchronized (this) {
                if (members == null) {
                    members = new ArrayList<IGeoResource>();
                    if (vectorService != null) {
                        members.addAll(vectorService.resources(monitor));
                    }
                    if (rasterService != null) {
                        members.addAll(rasterService.resources(monitor));
                    }

                    IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
                    ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
                            .fire(new ResolveChangeEvent(this,
                                    IResolveChangeEvent.Type.POST_CHANGE, delta));

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
    @Override
    protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null) {
            synchronized (this) {
                if (info == null) {
                    URL identifier = getIdentifier();
                    info = new IServiceArcSDEInfo(identifier, dataStoreConfig.getSessionConfig());
                }
            }
            IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
            ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
                    .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
        }
        return info;
    }

    /**
     * @see IService#getConnectionParams()
     */
    public Map<String, Serializable> getConnectionParams() {
        return dataStoreConfig == null ? null : dataStoreConfig.toMap();
    }

    /**
     * @see IService#getStatus()
     */
    public Status getStatus() {
        if( vectorService == null ){
            return super.getStatus();
        }
        return Status.CONNECTED;
    }

    /**
     * @see IService#getMessage()
     */
    public Throwable getMessage() {
        return msg;
    }

    /**
     * @see IService#getIdentifier()
     */
    public URL getIdentifier() {
        return url;
    }

    public ArcSDEDataStoreConfig getDataStoreConfig() {
        return dataStoreConfig;
    }

    public ArcSDEConnectionConfig getConnectionConfig() {
        return dataStoreConfig.getSessionConfig();
    }

    public ArcSDEVectorService getVectorService() {
        return vectorService;
    }

    private static class IServiceArcSDEInfo extends IServiceInfo {
    
        private final URL identifier;
    
        IServiceArcSDEInfo( final URL identifier, final ArcSDEConnectionConfig connectionConfig ) {
            this.identifier = identifier;
            try {
                schema = new URI("arcsde://geotools/gml"); //$NON-NLS-1$
            } catch (URISyntaxException e) {
                ArcsdePlugin.log(null, e);
            }
            icon = AbstractUIPlugin.imageDescriptorFromPlugin(ArcsdePlugin.ID,
                    "icons/obj16/arcsde_obj.gif"); //$NON-NLS-1$
        }
    
        public String getDescription() {
            return identifier.toString();
        }
    
        public URI getSource() {
            try {
                return identifier.toURI();
            } catch (URISyntaxException e) {
                // This would be bad
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }
    
        public String getTitle() {
            return "ARCSDE " + identifier.getHost(); //$NON-NLS-1$
        }
    
    }
}
