/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.memory.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ITransientResolve;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

public class MemoryGeoResourceImpl extends IGeoResource implements ITransientResolve {

    /** parent service * */
    private MemoryServiceImpl parent;

    /** feature type name * */
    String type;

    private volatile Status status;
    private volatile Throwable message;

    public MemoryGeoResourceImpl( String type, MemoryServiceImpl parent ) {
        this.service = parent;
        this.type = type;
        this.parent = parent;
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;
        if (adaptee.isAssignableFrom(ITransientResolve.class)) {
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(IService.class))
            return adaptee.cast(parent);
        if (adaptee.isAssignableFrom(IGeoResource.class))
            return adaptee.cast(this);
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast(createInfo(monitor));
        if (adaptee.isAssignableFrom(SimpleFeatureStore.class))
            return adaptee.cast(parent.getDS().getFeatureSource(type));
        if (adaptee.isAssignableFrom(SimpleFeatureSource.class))
            return adaptee.cast(parent.getDS().getFeatureSource(type));
        if (adaptee.isAssignableFrom(SimpleFeatureType.class))
            return adaptee.cast(parent.getDS().getSchema(type));

        return super.resolve(adaptee, monitor);
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;

        return adaptee.isAssignableFrom(ITransientResolve.class) || adaptee.isAssignableFrom(IService.class)
                || adaptee.isAssignableFrom(IGeoResource.class) || adaptee.isAssignableFrom(IGeoResourceInfo.class)
                || adaptee.isAssignableFrom(SimpleFeatureStore.class) || adaptee.isAssignableFrom(SimpleFeatureSource.class)
                || adaptee.isAssignableFrom(FeatureStore.class) || adaptee.isAssignableFrom(FeatureSource.class) 
                || adaptee.isAssignableFrom(SimpleFeatureType.class) || super.canResolve(adaptee);
    }

    public Status getStatus() {
        if (status == null)
            return parent.getStatus();
        return status;
    }

    public Throwable getMessage() {
        if (message == null)
            return parent.getMessage();
        return message;
    }

    public URL getIdentifier() {
        try {
            return new URL(parent.getIdentifier().toString() + "#" + type); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            return parent.getIdentifier();
        }
    }

    @Override
    public ScratchResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (ScratchResourceInfo) super.getInfo(monitor);
    }
    @Override
    protected ScratchResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        parent.rLock.lock();
        try {
            return new ScratchResourceInfo();
        } finally {
            parent.rLock.unlock();
        }
    }

    class ScratchResourceInfo extends IGeoResourceInfo {
        SimpleFeatureType ft = null;
        FeatureSource<SimpleFeatureType, SimpleFeature> source;

        ScratchResourceInfo() throws IOException {
            try {
                source = parent.getDS().getFeatureSource(type);
                ft = source.getSchema();
            } catch (Exception e) {
                status = Status.BROKEN;
                message = new Exception("Error obtaining the feature type: " + type).initCause(e); //$NON-NLS-1$
                bounds = new ReferencedEnvelope(new Envelope(), getCRS());
            }

            keywords = new String[]{type, ft.getName().getNamespaceURI()};
        }

        public CoordinateReferenceSystem getCRS() {
            GeometryDescriptor defaultGeometry = ft.getGeometryDescriptor();
            if (defaultGeometry != null)
                return defaultGeometry.getCoordinateReferenceSystem();
            return null;
        }

        public String getName() {
            return ft.getName().getLocalPart();
        }

        public URI getSchema() {
            try {
                return new URI(ft.getName().getNamespaceURI());
            } catch (URISyntaxException e) {
                return null;
            }
        }

        public String getTitle() {
            return ft.getName().getLocalPart();
        }

        @Override
        public ReferencedEnvelope getBounds() {
            Envelope bounds;
            try {
                bounds = source.getBounds();
                if (bounds == null)
                    return new ReferencedEnvelope(new Envelope(), DefaultGeographicCRS.WGS84);
                if (bounds instanceof ReferencedEnvelope)
                    return (ReferencedEnvelope) bounds;
                return new ReferencedEnvelope(bounds, getCRS());
            } catch (IOException e) {
                return new ReferencedEnvelope(new Envelope(), DefaultGeographicCRS.WGS84);
            }
        }

    }
}
