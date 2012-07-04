/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2011, Refractions Research Inc.
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
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.IResolve.Status;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;
import net.refractions.udig.catalog.shp.internal.Messages;
import net.refractions.udig.ui.ErrorManager;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureReader;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Connect to a shapefile
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 * @version 1.2
 */
public class ShpServiceImpl extends IService {
    //private URL url;
    private ID id;
    private Map<String, Serializable> params = null;

    private Throwable msg = null;
    /**
     * Volatile cache of dataStore if created.
     */
    volatile ShapefileDataStore ds = null;
    protected final Lock rLock = new UDIGDisplaySafeLock();
    private final static Lock dsInstantiationLock = new UDIGDisplaySafeLock();

    /**
     * Construct <code>ShpServiceImpl</code>.
     * 
     * @param arg1
     * @param arg2
     */
    public ShpServiceImpl( URL url, Map<String, Serializable> params ) {
        //this.url = url;
        if( url == null ){
            throw new NullPointerException("ShpService requres a URL");
        }
        try {
            id = new ID(url);
        } catch (Throwable t) {
            throw new IllegalArgumentException("Unable to create ID from:"+url,t);
        }
        this.params = params;
        Serializable memorymapped = params.get("memory mapped buffer"); //$NON-NLS-1$
        if (memorymapped == null) {
            memorymapped = false;
            try {
                File file = URLUtils.urlToFile(url);
                final int maxsize = 1024 * 2048;
                if (file.length() > maxsize) {
                    memorymapped = false;
                }
            } catch (Exception e) {
                memorymapped = false;
            }
        }
        if (!params.containsKey(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key)) {
            params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, ShpPlugin.getDefault().isUseSpatialIndex());
        }
        if (!params.containsKey(ShapefileDataStoreFactory.DBFCHARSET.key)) {
            params.put(ShapefileDataStoreFactory.DBFCHARSET.key, ShpPlugin.getDefault().defaultCharset());
        }
    }

    /*
     * Required adaptions: <ul> <li>IServiceInfo.class <li>List.class <IGeoResource> </ul>
     * @see net.refractions.udig.catalog.IService#resolve(java.lang.Class,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {

        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(ShapefileDataStore.class))
            return adaptee.cast(getDS(monitor));
        if (adaptee.isAssignableFrom(File.class))
            return adaptee.cast(toFile());
        return super.resolve(adaptee, monitor);
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;
        return adaptee.isAssignableFrom(ShapefileDataStore.class) || //
                adaptee.isAssignableFrom(File.class) || //
                super.canResolve(adaptee);
    }

    public void dispose( IProgressMonitor monitor ) {
        super.dispose(monitor);
        if (members != null){
            members = null;
        }
        if( ds != null ){
            ds.dispose();
            ds = null;
        }
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<ShpGeoResourceImpl> resources( IProgressMonitor monitor ) throws IOException {

        if (members == null) {
            getDS(monitor); // slap it to load datastore
            rLock.lock();
            try {
                if (members == null) {
                    members = new LinkedList<ShpGeoResourceImpl>();
                    String[] typenames = ds.getTypeNames();
                    if (typenames != null)
                        for( int i = 0; i < typenames.length; i++ ) {
                            ShpGeoResourceImpl shpGeoResource = new ShpGeoResourceImpl(this, typenames[i]);
                            members.add(shpGeoResource);
                        }
                }
            } finally {
                rLock.unlock();
            }
        }
        return members;
    }

    private volatile List<ShpGeoResourceImpl> members = null;

    @Override
    public ShpServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (ShpServiceInfo) super.getInfo(monitor);
    }

    /*
     * @see net.refractions.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        ShapefileDataStore dataStore = getDS(monitor); // load ds
        if (dataStore == null) {
            return null; // could not connect
        }
        rLock.lock();
        try {
            return new ShpServiceInfo(this);
        } finally {
            rLock.unlock();
        }
    }

    /*
     * @see net.refractions.udig.catalog.IService#getConnectionParams()
     */
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

    ShapefileDataStore getDS( IProgressMonitor monitor ) throws IOException {
        if (ds == null) {
            dsInstantiationLock.lock();
            try {
                if (ds == null) {
                    ShapefileDataStoreFactory dsf = new ShapefileDataStoreFactory();
                    if (dsf.canProcess(params)) {

                        try {
                            ds = (ShapefileDataStore) dsf.createDataStore(params);
                            openIndexGenerationDialog(ds);
                            // hit it lightly to make sure it exists.
                            ds.getFeatureSource();

                        } catch (IOException e) {
                            msg = e;
                            try {
                                params.remove(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key);
                                ds = (ShapefileDataStore) dsf.createDataStore(params);
                                // hit it lightly to make sure it exists.
                                ds.getFeatureSource();

                            } catch (Exception e2) {
                                msg = e2;
                                throw (IOException) new IOException().initCause(e2);
                            }
                        }
                    }
                }
            } finally {
                dsInstantiationLock.unlock();
            }
            IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
            ResolveChangeEvent event = new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta);
            fire(event);
        }
        return ds;
    }

    private void fire( ResolveChangeEvent event ) {
        ICatalog catalog = parent(new NullProgressMonitor());
        if (catalog instanceof CatalogImpl) {
            ((CatalogImpl) catalog).fire(event);
        }
    }

    private void openIndexGenerationDialog( final ShapefileDataStore ds ) {
        rLock.lock();
        try {
            if (ds instanceof IndexedShapefileDataStore) {
                IndexedShapefileDataStore ids = (IndexedShapefileDataStore) ds;
                if (ids.isIndexed())
                    return;
                String name = getIdentifier().getFile();
                int lastIndexOf = name.lastIndexOf(File.separator);
                if (lastIndexOf > 0)
                    name = name.substring(lastIndexOf + 1);
                final String finalName = name;
                IRunnableWithProgress runnable = new IRunnableWithProgress(){

                    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                        monitor.beginTask(Messages.ShpPreferencePage_createindex + " " + finalName, IProgressMonitor.UNKNOWN); //$NON-NLS-1$
                        index(ds, ds.getTypeNames()[0]);
                        monitor.done();
                    }

                };
                if (PlatformUI.getWorkbench().isClosing() && false) {
                    try {
                        runnable.run(new NullProgressMonitor());
                    } catch (InvocationTargetException e) {
                        ShpPlugin.log("", e); //$NON-NLS-1$
                    } catch (InterruptedException e) {
                        ShpPlugin.log("", e); //$NON-NLS-1$
                    }
                } else {
                    PlatformGIS.runInProgressDialog(Messages.ShpServiceImpl_indexing + " " + finalName, true, runnable, false); //$NON-NLS-1$
                }
            }
        } finally {
            rLock.unlock();
        }

    }

    private void index( final ShapefileDataStore ds, final String typename ) {
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = null;
        try {
            // smack Datastore to generate indices
            reader = ds.getFeatureReader(new DefaultQuery(typename, Filter.INCLUDE, new String[0]), Transaction.AUTO_COMMIT);
        } catch (Exception e) {
            ShpPlugin.log("", e); //$NON-NLS-1$
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    ShpPlugin.log("", e); //$NON-NLS-1$
                }
        }
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        if( ds == null ){
            return super.getStatus();
        }
        return Status.CONNECTED;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return msg;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return id.toURL();
    }
    @Override
    public ID getID() {
        return id;
    }
    /**
     * The File as indicated in the connection parameters, may be null if we are representing a web
     * resource.
     * 
     * @return file as indicated in the connection parameters, may be null if we are reprsenting a
     *         web resource
     */
    public File toFile() {
        Map<String, Serializable> parametersMap = getConnectionParams();
        URL url = (URL) parametersMap.get(ShapefileDataStoreFactory.URLP.key);
        return URLUtils.urlToFile(url);
    }
}