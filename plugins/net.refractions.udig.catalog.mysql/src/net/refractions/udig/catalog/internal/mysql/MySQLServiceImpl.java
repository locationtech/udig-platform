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
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;
import net.refractions.udig.ui.ErrorManager;
import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.mysql.MySQLDataStore;
import org.geotools.data.mysql.MySQLDataStoreFactory;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p>
 *
 * @author David Zwiers, Refractions Research
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySQLServiceImpl extends IService {

    private URL url = null;
    private Map<String, Serializable> params = null;
    protected Lock rLock=new UDIGDisplaySafeLock();

    /**
     * Construct <code>PostGISServiceImpl</code>.
     *
     * @param arg1
     * @param arg2
     */
    public MySQLServiceImpl( URL arg1, Map<String, Serializable> arg2 ) {
        url = arg1;
        params = arg2;
    }

    /*
     * Required adaptations: <ul> <li>IServiceInfo.class <li>List.class <IGeoResource> </ul>
     *
     * @see net.refractions.udig.catalog.IService#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(MySQLDataStore.class))
            return adaptee.cast(getDS());
       /* if (adaptee.isAssignableFrom(Connection.class)){
                Connection connection;
				try {
					connection = getDS().getConnectionPool().getConnection();
				} catch (SQLException e) {
					throw (IOException) new IOException(e.getLocalizedMessage()).initCause(e);
				}
				return adaptee.cast(connection);
        }*/
        return super.resolve(adaptee, monitor);
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null) return false;
        return adaptee.isAssignableFrom(MySQLDataStore.class) ||
        adaptee.isAssignableFrom(Connection.class) ||
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
    // NOTE breaks when using udig as plugins with null pointer exception
    public List<MySQLGeoResource> resources( IProgressMonitor monitor ) throws IOException {

        MySQLDataStore ds=getDS();
        rLock.lock();
        try{
            if (members == null) {
                members = new LinkedList<MySQLGeoResource>();
                String[] typenames = ds.getTypeNames();
                if (typenames != null)
                    for( int i = 0; i < typenames.length; i++ ) {
                        members.add(new MySQLGeoResource(this, typenames[i]));
                    }
            }
        }finally{
            rLock.unlock();
        }
        return members;
    }
    private volatile List<MySQLGeoResource> members = null;

    /*
     * @see net.refractions.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        getDS(); // load DataStore
        if (info == null && ds != null) {
            rLock.lock();
            try{
                if (info == null) {
                    info = new IServiceMySQLInfo(ds);
                }
            }finally{
                rLock.unlock();
            }
            IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
            ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
                    .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
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
    private volatile MySQLDataStore ds = null;
    private Lock dsInstantiationLock=new UDIGDisplaySafeLock();

    MySQLDataStore getDS() throws IOException {
        boolean changed=false;
        dsInstantiationLock.lock();
        MySQLDataStoreFactory dsf = new MySQLDataStoreFactory();
        try{
                if (ds == null) {
                    changed=true;

                    if (dsf.canProcess(params)) {
                        try {
                            ds = (MySQLDataStore) dsf.createDataStore(params);
                        } catch (IOException e) {
                            msg = e;
                            throw e;
                        }
                    }
                }
        }finally{
            dsInstantiationLock.unlock();
        }
        if( changed ){
            IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
            ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
            .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
        }
        if (ds != null) {
            MySQLPlugin.addDataStore(ds);
        }
        return ds;
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        return msg != null ? Status.BROKEN : ds == null ? Status.NOTCONNECTED : Status.CONNECTED;
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

    private class IServiceMySQLInfo extends IServiceInfo {

        IServiceMySQLInfo( MySQLDataStore resource ) {
            super();
            String[] tns = null;
            try {
                tns = resource.getTypeNames();
            } catch (IOException e) {
                MySQLPlugin.log("Unable to read typenames", e); //$NON-NLS-1$
                tns = new String[0];
            }
            keywords = new String[tns.length + 1];

            System.arraycopy(tns, 0, keywords, 1, tns.length);
            keywords[0] = "mysql"; //$NON-NLS-1$

            try {
                schema = new URI("jdbc://mysql/gml"); //$NON-NLS-1$
            } catch (URISyntaxException e) {
                MySQLPlugin.log(null, e);
            }
        }

        public String getDescription() {
            return getIdentifier().toString();
        }

        public URL getSource() {
            return getIdentifier();
        }

        public String getTitle() {
            return "MySQL " + getIdentifier().getHost() + getIdentifier().getFile(); //$NON-NLS-1$
        }
        public ImageDescriptor getIcon() {
            return AbstractUIPlugin.imageDescriptorFromPlugin(MySQLPlugin.PLUGIN_ID,
                    "icons/obj16/mysql_16.png"); //$NON-NLS-1$
        }
    }
}
