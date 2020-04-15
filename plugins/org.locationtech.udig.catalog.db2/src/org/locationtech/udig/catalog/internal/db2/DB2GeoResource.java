/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2005, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.db2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.ui.graphics.Glyph;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

/**
 * Resource handle for the DB2 Universal Database.
 * 
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 */
public class DB2GeoResource extends IGeoResource {

    /** parent service * */
    DB2Service parent;

    /** feature type (table) name * */
    String name;

    DB2GeoResource( DB2Service parent, String name ) {
        this.service = parent;
        this.parent = parent;
        this.name = name;
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;

        return adaptee.isAssignableFrom(IGeoResourceInfo.class)
                || adaptee.isAssignableFrom(SimpleFeatureStore.class)
                || adaptee.isAssignableFrom(SimpleFeatureSource.class)
                || adaptee.isAssignableFrom(IService.class)
                || super.canResolve(adaptee);
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {

        if (adaptee == null)
            return null;

        if (adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast(createInfo(monitor));

        if (adaptee.isAssignableFrom(SimpleFeatureSource.class)) {
            DataStore ds = parent.getDataStore(monitor);
            if (ds != null) {
                SimpleFeatureSource fs = ds.getFeatureSource(name);
                if (fs != null){
                    return adaptee.cast(fs);
                }
            }
        }

        if (adaptee.isAssignableFrom(SimpleFeatureStore.class)) {
            FeatureSource<SimpleFeatureType, SimpleFeature> fs = resolve(SimpleFeatureSource.class, monitor);
            if (fs != null && fs instanceof SimpleFeatureStore) {
                return adaptee.cast(fs);
            }
        }

        if (adaptee.isAssignableFrom(IService.class)) {
            return adaptee.cast(parent);
        }

        return super.resolve(adaptee, monitor);
    }
    public Status getStatus() {
        return parent.getStatus();
    }

    public Throwable getMessage() {
        return parent.getMessage();
    }
    public String getName() {
        return name;
    }

    @SuppressWarnings("unqualified-field-access")
    public URL getIdentifier() {
        try {
            return new URL(parent.getIdentifier() + "#" + name); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            return parent.getIdentifier();
        }
    }

    @Override
    public DB2GeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (DB2GeoResourceInfo) super.getInfo(monitor);
    }
    @Override
	protected DB2GeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        try {
            parent.rLock.lock();
            return  new DB2GeoResourceInfo(monitor, name);
        } finally {
            parent.rLock.unlock();
        }
    }

    class DB2GeoResourceInfo extends IGeoResourceInfo {

        private SimpleFeatureType ft = null;

        DB2GeoResourceInfo( IProgressMonitor monitor, String nameArg ) throws IOException {
            DataStore ds = parent.getDataStore(monitor);
            if (ds == null)
                return;

            try {
                this.ft = ds.getSchema(nameArg);
            } catch (DataSourceException e) {
                return;
            }

            try {
                FeatureSource<SimpleFeatureType, SimpleFeature> fs = resolve(FeatureSource.class, null);
                if (fs != null) {
                    bounds = (ReferencedEnvelope) fs.getBounds();
                }

                if (bounds == null) {
                    CoordinateReferenceSystem crs = fs.getSchema().getCoordinateReferenceSystem();

                    // try getting an envelope out of the crs
                    org.opengis.geometry.Envelope envelope = CRS.getEnvelope(crs);

                    if (envelope != null) {
                        bounds = new ReferencedEnvelope(new Envelope(envelope.getLowerCorner()
                                .getOrdinate(0), envelope.getLowerCorner().getOrdinate(1), envelope
                                .getUpperCorner().getOrdinate(0), envelope.getUpperCorner()
                                .getOrdinate(1)), crs);
                    } else {
                        // TODO: perhaps access a preference which indicates
                        // wether to do a full table scan
                        // bounds = new ReferencedEnvelope(new Envelope(),crs);
                        // as a last resort do the full scan
                    	FeatureIterator<SimpleFeature> r = fs.getFeatures().features();
                        try{
                        SimpleFeature f = r.next();

                        bounds = new ReferencedEnvelope(new Envelope(), crs);
                        bounds.init(f.getBounds());
                        for( ; r.hasNext(); ) {
                            f = r.next();
                            bounds.include(f.getBounds());
                        }
                        }finally{
                            r.close();
                        }
                    }
                }
            } catch (Exception e) {
                CatalogPlugin.log(e.getLocalizedMessage(), e);
            }

            icon=Glyph.icon(ft);
            keywords = new String[]{"db2", //$NON-NLS-1$
                    ft.getName().getLocalPart(), ft.getName().getNamespaceURI()};
        }

        public CoordinateReferenceSystem getCRS() {
            return ft.getCoordinateReferenceSystem();
        }

        @SuppressWarnings("unqualified-field-access")
        public String getName() {
            return ft.getName().getLocalPart();
        }

        public URI getSchema() {
            try {
				return new URI( ft.getName().getNamespaceURI());
			} catch (URISyntaxException e) {
				return null;
			}
        }

        public String getTitle() {
            return ft.getName().getLocalPart();
        }
    }
}
