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
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;
import net.refractions.udig.ui.graphics.Glyph;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.ows.FeatureSetDescription;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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

    private WFSGeoResourceImpl(){/*not for use*/}
    /**
     * Construct <code>WFSGeoResourceImpl</code>.
     *
     * @param parent
     * @param typename
     */
    public WFSGeoResourceImpl(WFSServiceImpl parent, String typename){
        this.parent = parent; this.typename = typename;
        try {
            identifier= new URL(parent.getIdentifier().toString()+"#"+typename); //$NON-NLS-1$
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
        if(adaptee == null)
            return null;
//        if(adaptee.isAssignableFrom(IService.class))
//            return adaptee.cast( parent );
        if(adaptee.isAssignableFrom(WFSDataStore.class))
            return parent.resolve( adaptee, monitor );
        if (adaptee.isAssignableFrom(IGeoResource.class))
            return adaptee.cast( this );
        if(adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast( getInfo(monitor));
        if(adaptee.isAssignableFrom(FeatureStore.class)){
            FeatureSource fs = parent.getDS(monitor).getFeatureSource(typename);
            if(fs instanceof FeatureStore)
                return adaptee.cast( fs);
        if(adaptee.isAssignableFrom(FeatureSource.class))
            return adaptee.cast( parent.getDS(monitor).getFeatureSource(typename));
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
        if(adaptee == null)
            return false;
        return (adaptee.isAssignableFrom(IGeoResourceInfo.class) ||
                adaptee.isAssignableFrom(FeatureStore.class) ||
                adaptee.isAssignableFrom(FeatureSource.class) ||
                adaptee.isAssignableFrom(WFSDataStore.class) ||
                adaptee.isAssignableFrom(IService.class))||
                super.canResolve(adaptee);
    }
    private volatile IGeoResourceInfo info;
    public IGeoResourceInfo getInfo(IProgressMonitor monitor) throws IOException{
        if(info == null && getStatus()!=Status.BROKEN){
            parent.rLock.lock();
            try{
                if(info == null){
                    info = new IGeoResourceWFSInfo();
                }
            }finally{
                parent.rLock.unlock();
            }
        }
        return info;
    }

    class IGeoResourceWFSInfo extends IGeoResourceInfo {

        CoordinateReferenceSystem crs = null;
        IGeoResourceWFSInfo() throws IOException{
            FeatureType ft = parent.getDS(null).getSchema(typename);

            List<FeatureSetDescription> fts = parent.getDS(null).getCapabilities().getFeatureTypes();
            FeatureSetDescription fsd = null;
            if(fts!=null){
                Iterator<FeatureSetDescription> i = fts.iterator();
                while(i.hasNext() && fsd == null){
                    FeatureSetDescription t = i.next();
                    if(t!=null && typename.equals(t.getName()))
                        fsd = t;
                }
            }

            if( fsd==null ){
                bounds = new ReferencedEnvelope(-180,180,-90,90,DefaultGeographicCRS.WGS84);
            }else{
                bounds = new ReferencedEnvelope(fsd.getLatLongBoundingBox(),DefaultGeographicCRS.WGS84);
                description = fsd.getAbstract();
                title = fsd.getTitle();
            }

            GeometryAttributeType defaultGeom=ft.getDefaultGeometry();
            if( defaultGeom==null ){
               crs=null;
            }else{
                crs = defaultGeom.getCoordinateSystem();
            }

            name = typename;
            schema = ft.getNamespace();

            keywords = new String[]{
                "wfs", //$NON-NLS-1$
                typename,
                ft.getNamespace().toString()
            };

            icon=Glyph.icon(ft);
        }

        /*
         * @see net.refractions.udig.catalog.IGeoResourceInfo#getCRS()
         */
        public CoordinateReferenceSystem getCRS() {
            if(crs != null)
                return crs;
            return super.getCRS();
        }
    }
}
