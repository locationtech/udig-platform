package net.refractions.udig.catalog.geotools.data;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;

import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.DataAccessFactory.Param;
import org.geotools.jdbc.JDBCDataStoreFactory;

/**
 * ServiceExtension willing to place any GeoTools DataStore into the udig catalog.
 * 
 * @author Jody Garnett
 * @since 1.2.0
 */
public class DataStoreServiceExtension implements ServiceExtension {

    /**
     * GeoTools does not have an intrinsic concept of a "url" or identifier for each resource. Here
     * is our stratagy:
     * <ul>
     * <li>For any "File" DataStore we will use the file url
     * <li>For any DataBase we will create the jdbc url String as an URL with no protocol.
     * <li>We will special case WebFeatureServer to use the capabilities URL
     * </ul>
     */
    public Map<String, Serializable> createParams( URL url ) {
        Iterator<DataStoreFactorySpi> available = DataStoreFinder.getAvailableDataStores();
        while( available.hasNext() ) {
            DataStoreFactorySpi factory = available.next();

            if (!consider(factory, url)) {
                continue;
            }
            Map<String, Serializable> params = createConnectionParameters(url, factory);
            if (params != null && factory.canProcess(params)) {
                // oh this actually worked!
                return params;
            }
        }
        return null; // could not make use of the provided URL
    }

    /**
     * Use the url to generate some connection parameters that pass factory.canProcess or return
     * null if not possible.
     * 
     * @param url
     * @param factory
     * @return connectionParameters based on the provided url, or null if not possible
     */
    private Map<String, Serializable> createConnectionParameters( URL url,
            DataStoreFactorySpi factory ) {
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        for( Param param : factory.getParametersInfo() ) {
            if (param.required) {
                // sample default value (if available)
                if (param.sample != null) {
                    params.put(param.key, (Serializable) param.sample);
                }

                if (URL.class.isAssignableFrom(param.type)) {
                    params.put(param.key, url);
                } else if (File.class.isAssignableFrom(param.type)
                        && "file".equalsIgnoreCase(url.getProtocol())) {
                    File file = DataUtilities.urlToFile(url);
                    params.put(param.key, file);

                } else if (param.sample != null) {
                    params.put(param.key, (Serializable) param.sample);
                }
                // we cannot make up a value for a required parameters? This won't end well for
                // us...
            } else {
                if (param.sample != null) {
                    // may as well use the provided default value
                    params.put(param.key, (Serializable) param.sample);
                }
            }
        }
        return params;
    }

    /**
     * Check if the provided url has any hope of working with the factory.
     * 
     * @param factory
     * @param url
     * @return true if we could try using the url with the factory
     */
    private boolean consider( DataStoreFactorySpi factory, URL url ) {
        // is this specifically the kind of thing that can take a url?
        if (factory instanceof FileDataStoreFactorySpi) {
            FileDataStoreFactorySpi fileFactory = (FileDataStoreFactorySpi) factory;
            return fileFactory.canProcess(url);
        }
        // Go through the params and see if a URL can even be used?
        // (It should be a required parameter that accepts a URL
        for( Param param : factory.getParametersInfo() ) {
            if (param.required) {
                if (URL.class.isAssignableFrom(param.type)) {
                    return true; // we can use a URL
                }
                if (File.class.isAssignableFrom(param.type)) {
                    return "file".equalsIgnoreCase(url.getProtocol());
                }
            }
        }
        // We could try and reverse engineer a jdbc url
        if (factory instanceof JDBCDataStoreFactory) {
            return "jdbc".equalsIgnoreCase(url.getProtocol());
        }
        return false;
    }
    /**
     * Creates an IService based on the params provided.
     * @param id ID to use if possible
     * @param params ConnectionParameters
     */
    public IService createService( URL id, Map<String, Serializable> params ) {
        Iterator<DataStoreFactorySpi> available = DataStoreFinder.getAvailableDataStores();
        while( available.hasNext() ) {
            DataStoreFactorySpi factory = available.next();
            if( factory.canProcess( params )){
                return new DataStoreService( id, factory, params );
            }
        }
        return null; // could not use
        /*
        try {
            DataStore dataStore = DataStoreFinder.getDataStore( params );
            
        } catch (IOException e) {
            if( Activator.getDefault().isDebugging() ){
                IStatus status = new Status( IStatus.OK,Activator.PLUGIN_ID, "Could not connect to GeoTools Datastore", e );
                Activator.getDefault().getLog().log(status);
            }
        }
        return null;
        */
    }

}
