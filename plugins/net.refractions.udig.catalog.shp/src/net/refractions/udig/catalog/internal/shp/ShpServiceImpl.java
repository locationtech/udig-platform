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
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.URLUtils;
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
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStore;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStoreFactory;
import org.geotools.filter.Filter;

/**
 * Connect to a shapefile
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class ShpServiceImpl extends IService {


	private URL url = null;

	private Map<String, Serializable> params = null;

	/**
	 * Construct <code>ShpServiceImpl</code>.
	 *
	 * @param arg1
	 * @param arg2
	 */
	public ShpServiceImpl(URL arg1, Map<String, Serializable> arg2) {
		url = arg1;
		params = arg2;
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
        if( !params.containsKey(IndexedShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key)){
            params.put(IndexedShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key,
                    ShpPlugin.getDefault().isUseSpatialIndex());
        }
        if( !params.containsKey(IndexedShapefileDataStoreFactory.CHARSET.key)){
            params.put(IndexedShapefileDataStoreFactory.CHARSET.key,
                    ShpPlugin.getDefault().defaultCharset());
        }
	}

	/*
	 * Required adaptions: <ul> <li>IServiceInfo.class <li>List.class
	 * <IGeoResource> </ul>
	 *
	 * @see net.refractions.udig.catalog.IService#resolve(java.lang.Class,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor)
			throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
		if (adaptee.isAssignableFrom(ShapefileDataStore.class))
			return adaptee.cast(getDS(monitor));
		return super.resolve(adaptee, monitor);
	}

	/*
	 * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
	 */
	public <T> boolean canResolve(Class<T> adaptee) {
		if (adaptee == null)
			return false;
		return adaptee.isAssignableFrom(ShapefileDataStore.class)||
                super.canResolve(adaptee);
	}

    public void dispose( IProgressMonitor monitor ) {
        if( members==null)
            return;

        int steps = (int) ((double) 99 / (double) members.size());
        for( IResolve resolve : members ) {
            try {
                SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, steps);
                resolve.dispose(subProgressMonitor);
                subProgressMonitor.done();
            } catch (Throwable e) {
                ErrorManager.get().displayException(e,
                        "Error disposing members of service: " + getIdentifier(), CatalogPlugin.ID); //$NON-NLS-1$
            }
        }
    }

	/*
	 * @see net.refractions.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List<ShpGeoResourceImpl> resources(IProgressMonitor monitor)
			throws IOException {

		if (members == null) {
            getDS(monitor); // slap it to load datastore
			rLock.lock();
            try{
				if (members == null) {
					members = new LinkedList<ShpGeoResourceImpl>();
					String[] typenames = ds.getTypeNames();
					if (typenames != null)
						for (int i = 0; i < typenames.length; i++) {
							members.add(new ShpGeoResourceImpl(this,
									typenames[i]));
						}
				}
			}finally{
			    rLock.unlock();
            }
		}
		return members;
	}

	private volatile List<ShpGeoResourceImpl> members = null;

	/*
	 * @see net.refractions.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IServiceInfo getInfo(IProgressMonitor monitor) throws IOException {
		getDS(monitor); // load ds
		if (info == null && ds != null) {
			rLock.lock();
            try{
				if (info == null) {
					info = new IServiceShpInfo();
				}
			}finally{
			    rLock.unlock();
            }
		}
		return info;
	}

	private volatile IServiceInfo info = null;

	/*
	 * @see net.refractions.udig.catalog.IService#getConnectionParams()
	 */
	public Map<String, Serializable> getConnectionParams() {
		return params;
	}

	private Throwable msg = null;

	private volatile ShapefileDataStore ds = null;

    protected final Lock rLock=new UDIGDisplaySafeLock();

    private final static Lock dsInstantiationLock=new UDIGDisplaySafeLock();

	ShapefileDataStore getDS(IProgressMonitor monitor) throws IOException {
		if (ds == null) {
			dsInstantiationLock.lock();
            try{
				if (ds == null) {
					IndexedShapefileDataStoreFactory dsf = new IndexedShapefileDataStoreFactory();
					if (dsf.canProcess(params)) {


                        try {
							ds = (ShapefileDataStore) dsf
									.createDataStore(params);
							openIndexGenerationDialog(ds);
                            // hit it lightly to make sure it exists.
                            ds.getFeatureSource();

						} catch (IOException e) {
							msg = e;
							try {
								params
										.remove(IndexedShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key);
								ds = (ShapefileDataStore) dsf
										.createDataStore(params);
                                // hit it lightly to make sure it exists.
                                ds.getFeatureSource();

							} catch (Exception  e2) {
								msg = e2;
								throw (IOException) new IOException().initCause(e2);
							}
                        }
					}
				}
			}finally{
                dsInstantiationLock.unlock();
            }
			IResolveDelta delta = new ResolveDelta(this,
					IResolveDelta.Kind.CHANGED);
			ResolveChangeEvent event = new ResolveChangeEvent(this,
					IResolveChangeEvent.Type.POST_CHANGE, delta);
			fire(event);
		}
		return ds;
	}

	private void fire(ResolveChangeEvent event) {
		ICatalog catalog = parent(new NullProgressMonitor());
		if( catalog instanceof CatalogImpl){
			((CatalogImpl)catalog)
					.fire(event);
		}
	}

	private void openIndexGenerationDialog(final ShapefileDataStore ds) {
        rLock.lock();
        try{
            if (ds instanceof IndexedShapefileDataStore) {
                IndexedShapefileDataStore ids = (IndexedShapefileDataStore) ds;
                if( ids.isIndexed() )
                    return;
                String name = getIdentifier().getFile();
                int lastIndexOf = name.lastIndexOf(File.separator);
                if (lastIndexOf > 0)
                    name = name.substring(lastIndexOf + 1);
                final String finalName = name;
                IRunnableWithProgress runnable = new IRunnableWithProgress(){

                    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                            InterruptedException {
                        monitor
                                .beginTask(
                                        Messages.ShpPreferencePage_createindex + " " + finalName, IProgressMonitor.UNKNOWN); //$NON-NLS-1$
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
                    PlatformGIS.runInProgressDialog(Messages.ShpServiceImpl_indexing
                            + " " + finalName, true, runnable, false); //$NON-NLS-1$
                }
            }
        }finally{
            rLock.unlock();
        }

    }

    private void index( final ShapefileDataStore ds, final String typename ) {
        FeatureReader reader = null;
        try {
            // smack Datastore to generate indices
            reader = ds.getFeatureReader(new DefaultQuery(typename, Filter.NONE,
                    new String[0]), Transaction.AUTO_COMMIT);
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
		return msg != null ? Status.BROKEN : ds == null ? Status.NOTCONNECTED
				: Status.CONNECTED;
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
		return url;
	}

	private class IServiceShpInfo extends IServiceInfo {

		IServiceShpInfo() {
			super();
			keywords = new String[] { ".shp", "Shapefile", //$NON-NLS-1$ //$NON-NLS-2$
					ds.getTypeNames()[0] };

			try {
				schema = new URI("shp://www.opengis.net/gml"); //$NON-NLS-1$
			} catch (URISyntaxException e) {
				ShpPlugin.log(null, e);
				schema = null;
			}
		}

		public String getDescription() {
			return getIdentifier().toString();
		}

		public String getTitle() {
			return getIdentifier().getFile();
		}
	}
}
