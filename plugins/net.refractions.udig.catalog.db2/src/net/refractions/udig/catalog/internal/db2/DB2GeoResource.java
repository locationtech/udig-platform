/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2005, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.db2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.ui.graphics.Glyph;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.resources.CRSUtilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

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

    /** info object * */
    volatile DB2GeoResourceInfo info;

    DB2GeoResource( DB2Service parent, String name ) {
        this.parent = parent;
        this.name = name;
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;

        return adaptee.isAssignableFrom(IGeoResourceInfo.class)
                || adaptee.isAssignableFrom(FeatureStore.class)
                || adaptee.isAssignableFrom(FeatureSource.class)
                || adaptee.isAssignableFrom(IService.class)
                || super.canResolve(adaptee);
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {

        if (adaptee == null)
            return null;

        if (adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast(getInfo(monitor));

        if (adaptee.isAssignableFrom(FeatureSource.class)) {
            DataStore ds = parent.getDataStore(monitor);
            if (ds != null) {
                FeatureSource fs = ds.getFeatureSource(name);
                if (fs != null)
                    return adaptee.cast(fs);
            }
        }

        if (adaptee.isAssignableFrom(FeatureStore.class)) {
            FeatureSource fs = resolve(FeatureSource.class, monitor);
            if (fs != null && fs instanceof FeatureStore) {
                return adaptee.cast(fs);
            }
        }

        if (adaptee.isAssignableFrom(IService.class)) {
            return adaptee.cast(parent);
        }

        return super.resolve(adaptee, monitor);
    }
    public IService service( IProgressMonitor monitor ) throws IOException {
        return parent;
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
    public IGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        parent.rLock.lock();
        try{
            if (info == null) {
                info = new DB2GeoResourceInfo(monitor, name);
            }
        }finally{
            parent.rLock.unlock();
        }

        return info;
    }

    class DB2GeoResourceInfo extends IGeoResourceInfo {

        private FeatureType ft = null;

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
                FeatureSource fs = resolve(FeatureSource.class, null);
                if (fs != null) {
                    bounds = (ReferencedEnvelope) fs.getBounds();
                }

                if (bounds == null) {
                    CoordinateReferenceSystem crs = fs.getSchema().getDefaultGeometry()
                            .getCoordinateSystem();

                    // try getting an envelope out of the crs
                    org.opengis.spatialschema.geometry.Envelope envelope = CRSUtilities
                            .getEnvelope(crs);

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
                        FeatureIterator r = fs.getFeatures().features();
                        try{
                        Feature f = r.next();

                        bounds = new ReferencedEnvelope(new Envelope(), crs);
                        bounds.init(f.getBounds());
                        for( ; r.hasNext(); ) {
                            f = r.next();
                            bounds.expandToInclude(f.getBounds());
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
                    ft.getTypeName(), ft.getNamespace().toString()};
        }

        public CoordinateReferenceSystem getCRS() {
            return ft.getDefaultGeometry().getCoordinateSystem();
        }

        @SuppressWarnings("unqualified-field-access")
        public String getName() {
            return ft.getTypeName();
        }

        public URI getSchema() {
            return ft.getNamespace();
        }

        public String getTitle() {
            return ft.getTypeName();
        }
    }
}
