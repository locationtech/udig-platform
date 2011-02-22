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
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.db2.DB2Plugin;
import net.refractions.udig.ui.ErrorManager;
import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.DataStore;
import org.geotools.data.db2.DB2DataStore;
import org.geotools.data.db2.DB2DataStoreFactory;

/**
 * Service handle for the DB2 Universal Database.
 *
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 */
public class DB2Service extends IService {

    /** underlying datastore * */
    private volatile DB2DataStore ds;

    /** info object * */
    private volatile DB2ServiceInfo info;

    /** members (tables) * */
    private volatile List<DB2GeoResource> members;

    /** service url * */
    private URL url;

    /** connection parameters * */
    private Map<String, Serializable> params;

    /** any thrown exception * */
    Throwable msg;

    protected Lock rLock = new UDIGDisplaySafeLock();

    private Lock dsInstantiationLock = new UDIGDisplaySafeLock();

    public DB2Service( URL id, Map<String, Serializable> params2 ) {
        url = id;
        this.params = params2;
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null && (
                adaptee.isAssignableFrom(DataStore.class)
                || super.canResolve(adaptee));
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified" ); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(DataStore.class)){
            return adaptee.cast(getDataStore(monitor)); // use the monitor!
        }
        return super.resolve(adaptee, monitor);
    }
    public void dispose( IProgressMonitor monitor ) {
        if (members == null)
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

    @Override
    public List<DB2GeoResource> resources( IProgressMonitor monitor ) throws IOException {

        if (members == null) {
            DB2DataStore ds = getDataStore( monitor );
            if (ds == null)
                return null;

            rLock.lock();
            try {
                if (members == null) {
                    members = new ArrayList<DB2GeoResource>();

                    String[] names = ds.getTypeNames();
                    if (names == null)
                        return members;

                    for( int i = 0; i < names.length; i++ ) {
                        members.add(new DB2GeoResource(this, names[i]));
                    }
                }
            } finally {
                rLock.unlock();
            }
        }
        else {
            monitor.done();
        }
        return members;
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return this.params;
    }

    public Status getStatus() {
        return msg != null ? Status.BROKEN : ds == null ? Status.NOTCONNECTED : Status.CONNECTED;
    }

    public Throwable getMessage() {
        return msg;
    }

    public URL getIdentifier() {
        return url;
    }

    @Override
    public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (this.info == null) {
            DB2DataStore ds = getDataStore( monitor);
            if (ds == null)
                return null;

            rLock.lock();
            try {
                if (info == null) {
                    info = new DB2ServiceInfo( null );
                }
            } finally {
                rLock.unlock();
            }
        }
        else {
            monitor.done();
        }
        return info;
    }
    DB2DataStore getDataStore(IProgressMonitor monitor) throws IOException {
        if( monitor == null ) monitor = new NullProgressMonitor();

        if (this.ds == null) {
            dsInstantiationLock.lock();
            try {
                if (ds == null) {
                    DB2DataStoreFactory dsf = new DB2DataStoreFactory();
                    try {
                        // We expect the port value (key '3') to be a String but some of the
                        // extensions (ArcServiceExtension)
                        // change this from a String to an Integer which causes us to fail.
                        // In order to cope with this, we make a local copy of the parameters and
                        // force the port
                        // value to be a String.
                        // Maybe we should change DB2DataStoreFactory.canProcess to accept either
                        // Integer or
                        // String as valid for port.
                        Map<String, Serializable> paramsLocal = new HashMap<String, Serializable>();
                        Set keys = this.params.keySet();
                        Iterator it = keys.iterator();
                        while( it.hasNext() ) {
                            String key = (String) it.next();
                            String value = this.params.get(key).toString();
                            paramsLocal.put(key, value);
                        }

                        if (dsf.canProcess(paramsLocal)) {
                            this.ds = (DB2DataStore) dsf.createDataStore(paramsLocal);
                        }
                    } catch (IOException e) {
                        msg = e;
                        throw e;
                    }
                }
            } finally {
                dsInstantiationLock.unlock();
            }
        }

        return this.ds;
    }

    class DB2ServiceInfo extends IServiceInfo {

        public DB2ServiceInfo(IProgressMonitor monitor) {
            super();

            // make the type names part of the keyword set
            String[] tns = null;
            try {
                tns = getDataStore( monitor ).getTypeNames();
            } catch (IOException e) {
                CatalogPlugin.log(e.getLocalizedMessage(), e);
                tns = new String[0];
            }
            if (tns == null)
                tns = new String[]{};

            keywords = new String[tns.length + 1];
            System.arraycopy(tns, 0, keywords, 1, tns.length);
            keywords[0] = "db2"; //$NON-NLS-1$

            try {
                schema = new URI("jdbc://db2/gml"); //$NON-NLS-1$
            } catch (URISyntaxException e) {
                CatalogPlugin.log(e.getLocalizedMessage(), e);
            }
        }

        public String getDescription() {
            return getIdentifier().toString();
        }

        public URL getSource() {
            return getIdentifier();
        }

        public String getTitle() {
            return "DB2 " + getIdentifier().getHost(); //$NON-NLS-1$
        }
        public ImageDescriptor getIcon() {
            return AbstractUIPlugin.imageDescriptorFromPlugin(DB2Plugin.ID,
                    "icons/obj16/db2_16.gif"); //$NON-NLS-1$
        }
    }
}
