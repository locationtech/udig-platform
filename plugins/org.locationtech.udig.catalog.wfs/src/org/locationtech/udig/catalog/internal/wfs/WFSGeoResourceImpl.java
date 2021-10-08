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
package org.locationtech.udig.catalog.internal.wfs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.wfs.WFSDataStore;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IService;

/**
 * Access a feature type in a wfs.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class WFSGeoResourceImpl extends IGeoResource {
    WFSServiceImpl parent;

    String typename = null;

    private URL identifier;

    @SuppressWarnings("unused")
    private WFSGeoResourceImpl() {/* not for use */
    }

    /**
     * Construct <code>WFSGeoResourceImpl</code>.
     *
     * @param parent
     * @param typename
     */
    public WFSGeoResourceImpl(WFSServiceImpl parent, String typename) {
        this.service = parent;
        this.parent = parent;
        this.typename = typename;
        try {
            identifier = new URL(parent.getIdentifier().toString() + "#" + typename); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            identifier = parent.getIdentifier();
        }
    }

    @Override
    public URL getIdentifier() {
        return identifier;
    }

    /**
     * @see org.locationtech.udig.catalog.IGeoResource#getStatus()
     */
    @Override
    public Status getStatus() {
        return parent.getStatus();
    }

    /**
     * @see org.locationtech.udig.catalog.IGeoResource#getStatusMessage()
     */
    @Override
    public Throwable getMessage() {
        return parent.getMessage();
    }

    /**
     * Required adoptions:
     * <ul>
     * <li>IGeoResourceInfo.class
     * <li>IService.class
     * </ul>
     *
     * @see org.locationtech.udig.catalog.IResolve#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
        if (adaptee == null) {
            return null;
        }
        if (adaptee.isAssignableFrom(WFSDataStore.class)) {
            return parent.resolve(adaptee, monitor);
        }
        if (adaptee.isAssignableFrom(IGeoResource.class)) {
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class)) {
            return adaptee.cast(createInfo(monitor));
        }
        if (adaptee.isAssignableFrom(SimpleFeatureSource.class)) {
            WFSDataStore wfs = parent.getDS(monitor);
            SimpleFeatureSource featureSource = wfs.getFeatureSource(typename);
            return adaptee.cast(featureSource);
        }
        if (adaptee.isAssignableFrom(FeatureStore.class)) {
            WFSDataStore wfs = parent.getDS(monitor);
            SimpleFeatureSource featureSource = wfs.getFeatureSource(typename);
            if (featureSource instanceof FeatureStore) {
                return adaptee.cast(featureSource);
            } else {
                return null; // write access not available
            }
        }
        return super.resolve(adaptee, monitor);
    }

    /**
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    @Override
    public <T> boolean canResolve(Class<T> adaptee) {
        if (adaptee == null) {
            return false;
        }
        if (adaptee.isAssignableFrom(SimpleFeatureSource.class)) {
            return true;
        }
        if (adaptee.isAssignableFrom(SimpleFeatureStore.class)) {
            if (info != null) {
                // if info is known we can check if we are writable
                WFSGeoResourceInfo wfsInfo = (WFSGeoResourceInfo) info;
                return wfsInfo.isWritable();
            }
            if (service.getID().toString().indexOf("1.1.0") != -1) { //$NON-NLS-1$
                return false; // 1.1.0 not writable yet
            } else {
                return true;
            }
        }
        return (adaptee.isAssignableFrom(IGeoResourceInfo.class)
                || adaptee.isAssignableFrom(WFSDataStore.class)
                || adaptee.isAssignableFrom(IService.class)) || super.canResolve(adaptee);
    }

    @Override
    public WFSGeoResourceInfo getInfo(IProgressMonitor monitor) throws IOException {
        return (WFSGeoResourceInfo) super.getInfo(monitor);
    }

    @Override
    protected WFSGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException {
        if (getStatus() == Status.BROKEN) {
            return null; // could not connect
        }
        parent.rLock.lock();
        try {
            return new WFSGeoResourceInfo(this);
        } finally {
            parent.rLock.unlock();
        }
    }
}
