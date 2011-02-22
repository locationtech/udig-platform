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
package net.refractions.udig.catalog.internal.mysql;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.mysql.internal.Messages;
import net.refractions.udig.core.internal.CorePlugin;
import net.refractions.udig.ui.graphics.Glyph;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.geotools.data.DataSourceException;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.resources.CRSUtilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Provides GeoResouces for MySQL based features.
 *
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
        this.parent = parent;
        this.typename = typename;
        try {
            identifier=new URL(null, parent.getIdentifier().toString() + "#" + typename, CorePlugin.RELAXED_HANDLER); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            identifier= parent.getIdentifier();
        }
    }

    public URL getIdentifier() {
        return identifier;
    }

    /*
     * @see net.refractions.udig.catalog.IGeoResource#getStatus()
     */
    public Status getStatus() {
        if( status!=null )
            return status;
        return parent.getStatus();
    }

    /*
     * @see net.refractions.udig.catalog.IGeoResource#getStatusMessage()
     */
    public Throwable getMessage() {
        if( message!=null )
            return message;
        return parent.getMessage();
    }

    /*
     * Required adaptations: <ul> <li>IGeoResourceInfo.class <li>IService.class </ul>
     *
     * @see net.refractions.udig.catalog.IResolve#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast(getInfo(monitor));
        if (adaptee.isAssignableFrom(IGeoResource.class))
            return adaptee.cast(this);
        if (adaptee.isAssignableFrom(FeatureStore.class)) {
            FeatureSource fs = parent.getDS().getFeatureSource(typename);
            if (fs instanceof FeatureStore)
                return adaptee.cast(fs);
            if (adaptee.isAssignableFrom(FeatureSource.class))
                return adaptee.cast(parent.getDS().getFeatureSource(typename));
        }
        if (adaptee.isAssignableFrom(Connection.class)){
        	return parent.resolve(adaptee, monitor);
        }

        return super.resolve(adaptee, monitor);
    }
    public IService service( IProgressMonitor monitor ) throws IOException {
        return parent;
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;
        return (adaptee.isAssignableFrom(IGeoResourceInfo.class)
                || adaptee.isAssignableFrom(FeatureStore.class)
                || adaptee.isAssignableFrom(FeatureSource.class) || adaptee
                .isAssignableFrom(IService.class))||adaptee
                .isAssignableFrom(Connection.class) ||
                super.canResolve(adaptee);
    }
    private volatile IGeoResourceInfo info;

    public IGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null && getStatus() != Status.BROKEN) {
            parent.rLock.lock();
            try{
                if (info == null) {
                    info = new MySQLResourceInfo();
                }
            }finally{
                parent.rLock.unlock();
            }
        }
        return info;
    }

    class MySQLResourceInfo extends IGeoResourceInfo {

        private FeatureType ft = null;

        MySQLResourceInfo() throws IOException {

            try {
                ft = parent.getDS().getSchema(typename);
            } catch (DataSourceException e) {
                if( e.getMessage().contains("permission") ){ //$NON-NLS-1$
                    status=Status.RESTRICTED_ACCESS;
                }else{
                    status=Status.BROKEN;
                }
                message=e;
                MySQLPlugin.log("Unable to retrieve FeatureType schema for type '"+typename+"'.", e); //$NON-NLS-1$ //$NON-NLS-2$
                keywords = new String[]{"postgis", //$NON-NLS-1$
                        typename};
                return;
            }

            keywords = new String[]{"postgis", //$NON-NLS-1$
                    typename, ft.getNamespace().toString()};

            icon=Glyph.icon(ft);

        }

        @Override
        public synchronized ReferencedEnvelope getBounds() {
            if (bounds==null ){

                try {
                    FeatureSource source = parent.getDS().getFeatureSource(typename);
                    Envelope temp = parent.getDS().getEnvelope(typename);
                    System.out.println(typename);
                    GeometryAttributeType defGeom = source.getSchema().getDefaultGeometry();
                    if (defGeom == null) { //non-geom table!
                        bounds = new ReferencedEnvelope(new Envelope(), null);
                        CatalogPlugin.log("FeatureType '" + typename + "' does not have a geometry", null); //$NON-NLS-1$ //$NON-NLS-2$
                        return bounds;
                    }
                    CoordinateReferenceSystem crs = defGeom.getCoordinateSystem();
                    if( temp instanceof ReferencedEnvelope){
                        bounds = (ReferencedEnvelope) temp;
                    }else{
                        if( temp!=null ){
                            if( crs==null ){
                                crs=null;
                            }
                            bounds=new ReferencedEnvelope(temp, crs);
                        }
                    }
                    if (bounds == null) {

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
                            // whether to do a full table scan
                            // bounds = new ReferencedEnvelope(new Envelope(),crs);
                            // as a last resort do the full scan
                            bounds = new ReferencedEnvelope(new Envelope(), crs);
                            FeatureIterator iter = source.getFeatures().features();
                            try {
                                while( iter.hasNext() ) {
                                    Feature element = iter.next();
                                    if (bounds.isNull())
                                        bounds.init(element.getBounds());
                                    else
                                        bounds.expandToInclude(element.getBounds());
                                }
                            }finally{
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
                                            "net.refractions.udig.catalog", 0, Messages.MySQLGeoResource_error_layer_bounds, e));   //$NON-NLS-1$
                    bounds = new ReferencedEnvelope(new Envelope(), null);
                }

            }
            return super.getBounds();
        }

        public CoordinateReferenceSystem getCRS() {
            if( status==Status.BROKEN || status==Status.RESTRICTED_ACCESS )
                return DefaultGeographicCRS.WGS84;

            GeometryAttributeType defGeom = ft.getDefaultGeometry();
            if (defGeom == null)
                return null;
            return defGeom.getCoordinateSystem();
        }

        public String getName() {
            return typename;
        }

        public URI getSchema() {
            if( status==Status.BROKEN || status==Status.RESTRICTED_ACCESS )
                return null;
            return ft.getNamespace();
        }

        public String getTitle() {
            return typename;
        }
    }

}
