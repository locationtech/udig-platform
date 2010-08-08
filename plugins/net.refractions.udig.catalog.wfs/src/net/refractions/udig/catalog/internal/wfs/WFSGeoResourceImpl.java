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
package net.refractions.udig.catalog.internal.wfs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.core.jts.ReferencedEnvelopeCache;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;

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

    private WFSGeoResourceImpl() {/* not for use */
    }
    /**
     * Construct <code>WFSGeoResourceImpl</code>.
     * 
     * @param parent
     * @param typename
     */
    public WFSGeoResourceImpl( WFSServiceImpl parent, String typename ) {
        this.service = parent;
        this.parent = parent;
        this.typename = typename;
        try {
            identifier = new URL(parent.getIdentifier().toString() + "#" + typename); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            identifier = parent.getIdentifier();
        }
    }

    public URL getIdentifier() {
        return identifier;
    }

    /*
     * @see net.refractions.udig.catalog.IGeoResource#getStatus()
     */
    public Status getStatus() {
        return parent.getStatus();
    }

    /*
     * @see net.refractions.udig.catalog.IGeoResource#getStatusMessage()
     */
    public Throwable getMessage() {
        return parent.getMessage();
    }

    /*
     * Required adaptions: <ul> <li>IGeoResourceInfo.class <li>IService.class </ul>
     * @see net.refractions.udig.catalog.IResolve#resolve(java.lang.Class,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null){
            return null;
        }
        if (adaptee.isAssignableFrom(WFSDataStore.class)){
            return parent.resolve(adaptee, monitor);
        }
        if (adaptee.isAssignableFrom(IGeoResource.class)){
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class)){
            return adaptee.cast(createInfo(monitor));
        }
        if (adaptee.isAssignableFrom(FeatureSource.class)) {
            WFSDataStore wfs = parent.getDS(monitor);
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = wfs.getFeatureSource(typename);
            return adaptee.cast(featureSource);            
        }
        if (adaptee.isAssignableFrom(FeatureStore.class)) {
            WFSDataStore wfs = parent.getDS(monitor);
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = wfs.getFeatureSource(typename);
            if( featureSource instanceof FeatureStore){
                return adaptee.cast(featureSource);
            }
            else {
                return null; // write access not available
            }
        }
        return super.resolve(adaptee, monitor);
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null){
            return false;
        }
        if (adaptee.isAssignableFrom(FeatureSource.class)){
            return true;
        }
        if (adaptee.isAssignableFrom(FeatureStore.class)) {
            if (info != null) {
                // if info is known we can check if we are writable
                WFSGeoResourceInfo wfsInfo = (WFSGeoResourceInfo) info;
                return wfsInfo.isWritable();
            }
            if( service.getID().toString().indexOf("1.1.0") != -1){
                return false; // 1.1.0 not writable yet
            }
            else {
                return true;
            }
        }
        return (adaptee.isAssignableFrom(IGeoResourceInfo.class)
                || adaptee.isAssignableFrom(WFSDataStore.class) || adaptee
                .isAssignableFrom(IService.class))
                || super.canResolve(adaptee);
    }
    @Override
    public WFSGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (WFSGeoResourceInfo) super.getInfo(monitor);
    }
    protected WFSGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
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