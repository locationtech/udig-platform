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
package net.refractions.udig.catalog.internal.arcsde;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.util.GeotoolsResourceInfoAdapter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.ResourceInfo;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p>
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
class ArcSDEVectorGeoResource extends IGeoResource {
    String typename = null;

    /**
     * Construct <code>PostGISGeoResource</code>.
     * 
     * @param parent
     * @param typename
     */
    public ArcSDEVectorGeoResource( ArcServiceImpl service, String typename ) {
        this.service = service;
        this.typename = typename;
    }

    public URL getIdentifier() {
        try {
            return new URL(service.getIdentifier().toString() + "#" + typename); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            return service.getIdentifier();
        }
    }

    /*
     * @see net.refractions.udig.catalog.IGeoResource#getStatus()
     */
    public Status getStatus() {
        return service.getStatus();
    }

    /*
     * @see net.refractions.udig.catalog.IGeoResource#getStatusMessage()
     */
    public Throwable getMessage() {
        return service.getMessage();
    }

    /*
     * Required adaptions: <ul> <li>IGeoResourceInfo.class <li>IService.class </ul>
     * @see net.refractions.udig.catalog.IResolve#resolve(java.lang.Class,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    @SuppressWarnings("unchecked")
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;
        if (adaptee.isAssignableFrom(IService.class))
            return adaptee.cast(service(monitor));
        if (adaptee.isAssignableFrom(IGeoResource.class))
            return adaptee.cast(this);
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast(createInfo(monitor));
        if (adaptee.isAssignableFrom(SimpleFeatureStore.class)) {
            DataStore dataStore = getDataStore(monitor);
            SimpleFeatureSource fs;
            fs = dataStore.getFeatureSource(typename);

            if (fs instanceof SimpleFeatureStore) {
                return adaptee.cast(fs);
            }
            if (adaptee.isAssignableFrom(SimpleFeatureSource.class)) {
                return adaptee.cast(fs);
            }
        }
        return super.resolve(adaptee, monitor);
    }

    public DataStore getDataStore( IProgressMonitor monitor ) throws IOException {
        ArcServiceImpl service = service(monitor);
        ArcSDEVectorService vectorService = service.getVectorService();
        DataStore dataStore = vectorService.getDataStore(monitor);
        return dataStore;
    }

    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null) {
            return false;
        }
        return adaptee.isAssignableFrom(IGeoResourceInfo.class)
                || adaptee.isAssignableFrom(SimpleFeatureStore.class)
                || adaptee.isAssignableFrom(SimpleFeatureSource.class) || super.canResolve(adaptee);
    }
    @Override
    protected IGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        DataStore dataStore = getDataStore(monitor);
        FeatureSource<SimpleFeatureType, SimpleFeature> fs = dataStore.getFeatureSource(typename);
        ResourceInfo gtinfo = fs.getInfo();
        GeotoolsResourceInfoAdapter vectorInfo = new GeotoolsResourceInfoAdapter(gtinfo);

        // IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
        // ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
        // .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
        return vectorInfo;
    }

    public ArcServiceImpl service( IProgressMonitor monitor ) throws IOException {
        return (ArcServiceImpl) super.service(monitor);
    }
}