/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.geotools.data;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IResolve.Status;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataAccess;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.ServiceInfo;
import org.opengis.feature.type.Name;

/**
 * This is a generic DataStore service offering no extended functionality besides
 * the ability to resolve to a DataStore containing FeatureSource.
 * 
 * @author Jody Garnett
 */
public class DataStoreService extends IService {
	
	/** Key used to mark generic datastore service entry */
	public static String GENERIC = "generic";
	
    private ID id;
    private Map<String, Serializable> connectionParams;
    private DataStore dataStore;
    private DataStoreFactorySpi factory;
    private IOException message;
    private List<FeatureSourceGeoResource> resources;

    public DataStoreService( ID id, DataStoreFactorySpi factory, Map<String, Serializable> params ) {
        this.id = id;
        connectionParams = params;
        this.factory = factory;
    }

    public synchronized DataStore toDataAccess() throws IOException {
        if (dataStore == null) {
            // connect!
            try {
                dataStore = factory.createDataStore(connectionParams);
            } catch (IOException e) {
                message = e;
                throw e;
            }
        }
        return dataStore;
    }

    @Override
    protected DataStoreServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        DataAccess< ? , ? > access = toDataAccess();
        ServiceInfo gtInfo = access.getInfo();
        return new DataStoreServiceInfo(factory,connectionParams,gtInfo);
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return connectionParams;
    }

    @Override
    public synchronized List<FeatureSourceGeoResource> resources( IProgressMonitor monitor )
            throws IOException {
        if (resources == null) {
            DataAccess< ? , ? > access = toDataAccess();
            resources = new ArrayList<FeatureSourceGeoResource>();
            for( Name name : access.getNames() ) {
                FeatureSourceGeoResource geoResource = new FeatureSourceGeoResource(this, name);
                resources.add(geoResource);
            }
        }
        return resources;
    }

    public URL getIdentifier() {
        return id.toURL();
    }

    public ID getID() {
        return id;
    }

    public Throwable getMessage() {
        return message;
    }

    public Status getStatus() {
        if( dataStore == null ){
            return super.getStatus();
        }
        return Status.CONNECTED;
    }

    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee == null || DataAccess.class.isAssignableFrom(adaptee)
                || super.canResolve(adaptee);
    }
    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        
        if( DataAccess.class.isAssignableFrom( adaptee )){
            return adaptee.cast( toDataAccess() );
        }
        return super.resolve(adaptee, monitor);
    }

    @Override
    public void dispose(IProgressMonitor monitor) {
        super.dispose(monitor); // clean up members
        if( dataStore != null ){
            dataStore.dispose();
            dataStore = null;
        }
        if( resources != null ){
            resources = null;
        }
    }
}
