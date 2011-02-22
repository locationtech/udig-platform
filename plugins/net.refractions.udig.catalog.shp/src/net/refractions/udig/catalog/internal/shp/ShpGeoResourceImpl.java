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
package net.refractions.udig.catalog.internal.shp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStore;
import org.geotools.feature.FeatureType;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryFinder;


/**
 * Connect to a shapefile.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class ShpGeoResourceImpl extends IGeoResource {
    ShpServiceImpl parent;
    String typename = null;
    private URL identifier;

    private ShpGeoResourceImpl(){/*not for use*/}

    /**
     * Construct <code>ShpGeoResourceImpl</code>.
     *
     * @param parent
     * @param typename
     */
    public ShpGeoResourceImpl(ShpServiceImpl parent, String typename){
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
        if (adaptee.isAssignableFrom(IGeoResource.class))
            return adaptee.cast( this );
        if(adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast( getInfo(monitor) );
        if(adaptee.isAssignableFrom(FeatureStore.class)){
            FeatureSource fs = parent.getDS(monitor).getFeatureSource();
            if(fs instanceof FeatureStore)
                return adaptee.cast( fs );
        if(adaptee.isAssignableFrom(FeatureSource.class))
            return adaptee.cast( parent.getDS(monitor).getFeatureSource() );
        }
        if(adaptee.isAssignableFrom(IndexedShapefileDataStore.class)){
            return adaptee.cast( parent.getDS(monitor) );
        }
        if(adaptee.isAssignableFrom(Style.class)){
            Style style = style(monitor);
            if( style != null ){
                return adaptee.cast( style(monitor));
            }
            // proceed to ask the super class, someone may
            // of written an IResolveAdapaterFactory providing us
            // with a style ...
        }
        return super.resolve(adaptee, monitor);
    }

    private FeatureSource featureSource( IProgressMonitor monitor ) throws IOException {
        return parent.getDS(monitor).getFeatureSource();
    }

    public Style style( IProgressMonitor monitor ) {
        URL url = parent.getIdentifier();
        String shp = url.getFile();

        StyleFactory styleFactory = StyleFactoryFinder.createStyleFactory();

        // strip off the extension and check for sld
        String sld = shp.substring(0, shp.length() - 4) + ".sld"; //$NON-NLS-1$
        File f = new File(sld);
        if (!f.exists()) {
            // try upper case
            sld = shp.substring(0, shp.length() - 4) + ".SLD"; //$NON-NLS-1$
            f = new File(sld);
        }

        if (f.exists()) {
            // parse it up
            SLDParser parser = new SLDParser(styleFactory);
            try {
                parser.setInput(f);
            } catch (FileNotFoundException e) {
                return null; // well that is unexpected since f.exists()
            }
            Style[] styles = parser.readXML();

            FeatureSource source;
            try {
                source = featureSource(null);
            } catch (IOException e) {
                return null; // does not look like there is anything in the shapefile
            }
            FeatureType featureType = source.getSchema();
            // put the first one on
            if (styles != null && styles.length > 0) {
                Style style = SLDs.matchingStyle(styles, featureType);
                if (style == null) {
                    style = styles[0];
                }
                return style;
            }
        }
        return null; // well nothing worked out; make your own style
    }

    @Override
    public IService service( IProgressMonitor monitor ) throws IOException {
        return parent;
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if(adaptee == null)
            return false;
        return  adaptee.isAssignableFrom(IGeoResourceInfo.class) ||
                adaptee.isAssignableFrom(FeatureStore.class) ||
                adaptee.isAssignableFrom(FeatureSource.class) ||
                adaptee.isAssignableFrom(IService.class)||
                adaptee.isAssignableFrom(ShapefileDataStore.class) ||
                adaptee.isAssignableFrom(Style.class) ||
                super.canResolve(adaptee);
    }
    private volatile IGeoResourceInfo info;
    public IGeoResourceInfo getInfo(IProgressMonitor monitor) throws IOException{
            parent.rLock.lock();
            try{
                if(info == null || getStatus()==Status.BROKEN){
                    info = new ShpGeoResourceInfo(this);
                }
            }finally{
                parent.rLock.unlock();
            }
        return info;
    }
}
