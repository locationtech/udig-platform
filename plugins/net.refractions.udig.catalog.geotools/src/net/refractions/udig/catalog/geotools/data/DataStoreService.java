package net.refractions.udig.catalog.geotools.data;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataAccess;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.ServiceInfo;
import org.opengis.feature.type.Name;

public class DataStoreService extends IService {
    private ID id;
    private Map<String, Serializable> connectionParams;
    private DataAccess<?,?> dataStore;
    private DataAccessFactory factory;
    private IOException message;
    private List<FeatureSourceGeoResource> resources;
    
    public DataStoreService( ID id, DataAccessFactory factory, Map<String, Serializable> params ) {
        this.id = id;
        connectionParams = params;
        this.factory = factory;
    }

    public synchronized DataAccess<?,?> toDataAccess() throws IOException {
        if( dataStore == null ){
            // connect!
            try {
                dataStore = factory.createDataStore( connectionParams );
            } catch (IOException e) {
                message = e;
                throw e;
            }
        }
        return dataStore;
    }
    
    @Override
    protected DataStoreServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        DataAccess<?,?> access = toDataAccess();
        ServiceInfo gtInfo = access.getInfo();
        return new DataStoreServiceInfo( gtInfo );
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return connectionParams;
    }

    @Override
    public synchronized List<FeatureSourceGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        if( resources == null ){
            DataAccess<?,?> access = toDataAccess();
            resources = new ArrayList<FeatureSourceGeoResource>();
            for( Name name : access.getNames() ){
                FeatureSourceGeoResource geoResource = new FeatureSourceGeoResource( this, name );
                resources.add( geoResource );
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
        if( dataStore != null ){
            return Status.CONNECTED;
        }
        if( message != null ){
            return Status.BROKEN;
        }
        else {
            return Status.NOTCONNECTED;
        }
    }

}
