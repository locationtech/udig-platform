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
package org.locationtech.udig.catalog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;

import org.locationtech.udig.catalog.internal.mysql.MySQLPlugin;
import org.locationtech.udig.catalog.mysql.internal.Messages;
import org.locationtech.udig.core.internal.CorePlugin;
import org.locationtech.udig.ui.graphics.Glyph;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.geotools.data.DataSourceException;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

/**
 * Provides GeoResouces for MySQL based features.
 * <p>
 * This copies the postgisGeoResouce and is used to identify MySql features.
 * </p>
 * 
 * @author David Zwiers, Refractions Research
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySQLGeoResource extends IGeoResource {
    MySQLServiceImpl parent;
    String typename = null;
    private volatile Status status;
    private volatile Throwable message;
    private URL identifier;

    /**
     * Construct <code>MySQLGeoResource</code>.
     * 
     * @param parent
     * @param typename
     */
    public MySQLGeoResource( MySQLServiceImpl parent, String typename ) {
        this.service = parent;
        this.parent = parent;
        this.typename = typename;
        try {
            identifier = new URL(null,
                    parent.getIdentifier().toString() + "#" + typename, CorePlugin.RELAXED_HANDLER); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            identifier = parent.getIdentifier();
        }
    }

    public URL getIdentifier() {
        return identifier;
    }

    /*
     * @see org.locationtech.udig.catalog.IGeoResource#getStatus()
     */
    public Status getStatus() {
        if (status != null)
            return status;
        return parent.getStatus();
    }

    /*
     * @see org.locationtech.udig.catalog.IGeoResource#getStatusMessage()
     */
    public Throwable getMessage() {
        if (message != null)
            return message;
        return parent.getMessage();
    }

    /*
     * Required adaptations: <ul> <li>IGeoResourceInfo.class <li>IService.class </ul>
     * @see org.locationtech.udig.catalog.IResolve#resolve(java.lang.Class,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast(createInfo(monitor));
        if (adaptee.isAssignableFrom(IGeoResource.class))
            return adaptee.cast(this);
        if (adaptee.isAssignableFrom(SimpleFeatureStore.class)) {
            SimpleFeatureSource fs = parent.getDS().getFeatureSource( typename);
            if (fs instanceof SimpleFeatureStore){
                return adaptee.cast(fs);
            }
            if (adaptee.isAssignableFrom(SimpleFeatureSource.class)){
                return adaptee.cast(parent.getDS().getFeatureSource(typename));
            }
        }
        if (adaptee.isAssignableFrom(Connection.class)) {
            return parent.resolve(adaptee, monitor);
        }

        return super.resolve(adaptee, monitor);
    }
    /*
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;
        return (adaptee.isAssignableFrom(IGeoResourceInfo.class)
                || adaptee.isAssignableFrom(SimpleFeatureStore.class)
                || adaptee.isAssignableFrom(SimpleFeatureSource.class) || adaptee
                .isAssignableFrom(IService.class))
                || adaptee.isAssignableFrom(Connection.class) || super.canResolve(adaptee);
    }
    @Override
    public MySQLResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (MySQLResourceInfo) super.getInfo(monitor);
    }
    protected MySQLResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        if (getStatus() == Status.BROKEN) {
            return null; // not connected
        }
        parent.rLock.lock();
        try {
            return new MySQLResourceInfo();

        } finally {
            parent.rLock.unlock();
        }

    }

    class MySQLResourceInfo extends IGeoResourceInfo {

        private SimpleFeatureType ft = null;

        MySQLResourceInfo() throws IOException {

            try {
                ft = parent.getDS().getSchema(typename);
            } catch (DataSourceException e) {
                if (e.getMessage().contains("permission")) { //$NON-NLS-1$
                    status = Status.RESTRICTED_ACCESS;
                } else {
                    status = Status.BROKEN;
                }
                message = e;
                MySQLPlugin.log(
                        "Unable to retrieve FeatureType schema for type '" + typename + "'.", e); //$NON-NLS-1$ //$NON-NLS-2$
                keywords = new String[]{"mysql", //$NON-NLS-1$
                        typename};
                return;
            }

            keywords = new String[]{"mysql", //$NON-NLS-1$
                    typename, ft.getName().getNamespaceURI()};

            icon = Glyph.icon(ft);

        }

        @Override
        public synchronized ReferencedEnvelope getBounds() {
            if (bounds == null) {

                try {
                    FeatureSource<SimpleFeatureType, SimpleFeature> source = parent.getDS()
                            .getFeatureSource(typename);

                    bounds = source.getBounds();
                    CoordinateReferenceSystem crs = getCRS();

                    if (bounds == null) {

                        // try getting an envelope out of the crs
                        org.opengis.geometry.Envelope envelope = CRS.getEnvelope(crs);

                        if (envelope != null) {
                            bounds = new ReferencedEnvelope(envelope.getLowerCorner()
                                    .getOrdinate(0), envelope.getUpperCorner().getOrdinate(0),
                                    envelope.getLowerCorner().getOrdinate(1), envelope
                                            .getUpperCorner().getOrdinate(1), crs);
                        } else {
                            // TODO: perhaps access a preference which indicates
                            // whether to do a full table scan
                            // bounds = new ReferencedEnvelope(new Envelope(),crs);
                            // as a last resort do the full scan
                            bounds = new ReferencedEnvelope(new Envelope(), crs);
                            FeatureIterator<SimpleFeature> iter = source.getFeatures().features();
                            try {
                                while( iter.hasNext() ) {
                                    SimpleFeature element = iter.next();
                                    if (bounds.isNull())
                                        bounds.init(element.getBounds());
                                    else
                                        bounds.include(element.getBounds());
                                }
                            } finally {
                                iter.close();
                            }
                        }
                    }
                } catch (DataSourceException e) {
                    MySQLPlugin.log("Exception while generating MySQLGeoResource.", e); //$NON-NLS-1$
                } catch (Exception e) {
                    CatalogPlugin
                            .getDefault()
                            .getLog()
                            .log(
                                    new org.eclipse.core.runtime.Status(
                                            IStatus.WARNING,
                                            "org.locationtech.udig.catalog", 0, Messages.MySQLGeoResource_error_layer_bounds, e)); //$NON-NLS-1$
                    bounds = new ReferencedEnvelope(new Envelope(), null);
                }

            }
            return super.getBounds();
        }

        public CoordinateReferenceSystem getCRS() {
            if (status == Status.BROKEN || status == Status.RESTRICTED_ACCESS)
                return DefaultGeographicCRS.WGS84;

            return ft.getCoordinateReferenceSystem();
        }

        public String getName() {
            return typename;
        }

        public URI getSchema() {
            if (status == Status.BROKEN || status == Status.RESTRICTED_ACCESS)
                return null;
            try {
                return new URI(ft.getName().getNamespaceURI());
            } catch (URISyntaxException e) {
                return null;
            }
        }

        public String getTitle() {
            return typename;
        }
    }

}
