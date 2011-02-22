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
package net.refractions.udig.catalog.internal.oracle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.oracle.internal.Messages;
import net.refractions.udig.ui.graphics.Glyph;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p>
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class OracleGeoResource extends IGeoResource {
    OracleServiceImpl parent;
    String typename = null;

    private OracleGeoResource() {/*not for use*/
    }
    /**
     * Construct <code>OracleGeoResource</code>.
     *
     * @param parent
     * @param typename
     */
    public OracleGeoResource( OracleServiceImpl parent, String typename ) {
        this.parent = parent;
        this.typename = typename;
    }

    public URL getIdentifier() {
        try {
            return new URL(parent.getIdentifier().toString() + "#" + typename); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            return parent.getIdentifier();
        }
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
     * Required adaptions:
     * <ul>
     * <li>IGeoResourceInfo.class
     * <li>IService.class
     * </ul>
     * @see net.refractions.udig.catalog.IResolve#resolve(java.lang.Class, org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;
//        if (adaptee.isAssignableFrom(IService.class))
//            return adaptee.cast(parent);
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast(getInfo(monitor));
        if (adaptee.isAssignableFrom(IGeoResource.class))
            return adaptee.cast(this);
        if (adaptee.isAssignableFrom(FeatureStore.class)) {
            FeatureSource fs = parent.getDS(monitor).getFeatureSource(typename);
            if (fs instanceof FeatureStore)
                return adaptee.cast(fs);
            if (adaptee.isAssignableFrom(FeatureSource.class))
                return adaptee.cast(parent.getDS(monitor).getFeatureSource(typename));
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
                .isAssignableFrom(IService.class))
                || super.canResolve(adaptee);
    }
    private volatile IGeoResourceInfo info;
    public IGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null && getStatus() != Status.BROKEN) {
            parent.rLock.lock();
            try {
                if (info == null) {
                    info = new IGeoResourceShpInfo();
                }
            } finally {
                parent.rLock.unlock();
            }
        }
        return info;
    }

    class IGeoResourceShpInfo extends IGeoResourceInfo {

        private FeatureType ft = null;
        IGeoResourceShpInfo() throws IOException {
            ft = parent.getDS(null).getSchema(typename);

            try {
                FeatureSource source = parent.getDS(null).getFeatureSource(typename);
                bounds = (ReferencedEnvelope) source.getBounds();
                if (bounds == null) {
                    CoordinateReferenceSystem crs = null;
                    GeometryAttributeType defaultGeometry = source.getSchema().getDefaultGeometry();
                    if (defaultGeometry != null) {
                        crs = defaultGeometry.getCoordinateSystem();
                    }
                    bounds = new ReferencedEnvelope(new Envelope(), crs);
                    FeatureIterator iter=source.getFeatures().features();
                    try{
                        while(iter.hasNext() ) {
                            Feature element = iter.next();
                            if( bounds.isNull() )
                                bounds.init(element.getBounds());
                            else
                                bounds.expandToInclude(element.getBounds());
                        }
                    }finally{
                        iter.close();
                    }
                }
                //                        CoordinateReferenceSystem geomcrs = source.getSchema().getDefaultGeometry().getCoordinateSystem();

                //                        if(geomcrs!=null && !geomcrs.equals(CRS.decode("EPSG:4269"))){
                //                            bounds = JTS.transform(bounds,CRS.transform(geomcrs,CRS.decode("EPSG:4269")));
                //                        }else{
                //                            if(geomcrs == null)
                //                                System.err.println("CRS unknown for Shp");
                //                        }
            } catch (Exception e) {
                CatalogPlugin
                        .getDefault()
                        .getLog()
                        .log(
                                new org.eclipse.core.runtime.Status(
                                        IStatus.WARNING,
                                        "net.refractions.udig.catalog", 0, Messages.OracleGeoResource_error_layerBounds, e)); //$NON-NLS-1$
                bounds = new ReferencedEnvelope(new Envelope(), null);
            }

            icon=Glyph.icon(ft);
            keywords = new String[]{"postgis", //$NON-NLS-1$
                    ft.getTypeName(), ft.getNamespace().toString()};
        }

        public CoordinateReferenceSystem getCRS() {
            GeometryAttributeType defGeom = ft.getDefaultGeometry();
            if (defGeom == null)
                return null;
            return defGeom.getCoordinateSystem();
        }

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
