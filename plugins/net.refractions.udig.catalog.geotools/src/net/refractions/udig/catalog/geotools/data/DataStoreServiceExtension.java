package net.refractions.udig.catalog.geotools.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceExtension;
import net.refractions.udig.catalog.geotools.Activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataAccessFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.DataAccessFactory.Param;
import org.geotools.jdbc.JDBCDataStoreFactory;

/**
 * ServiceExtension willing to place any GeoTools DataStore into the udig catalog.
 * <p>
 * This class contains many static utility methods for handling GeoTools DataStores (in particular a
 * consistent way of supporting URL based connections which is not provided natively by the
 * DataAccess API).
 * 
 * @author Jody Garnett
 * @since 1.2.0
 */
public class DataStoreServiceExtension extends IServiceExtension {
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
        if( DataStoreConnectionFactory.DO_NOTHING){
            return null;
        }        
        return createDataAcessParameters(url);
    }

    /**
     * Used to turn a URL into something that can be used by a GeoTools DataAccessFactory.
     * 
     * @param url
     * @return Connection parameters, or null if no factory is willing to process the URL
     */
    public static Map<String, Serializable> createDataAcessParameters( URL url ) {
        Iterator<DataAccessFactory> available = DataAccessFinder.getAvailableDataStores();
        while( available.hasNext() ) {
            DataAccessFactory factory = available.next();
            try {
                if (!consider(factory, url)) {
                    continue;
                }
                Map<String, Serializable> params = createConnectionParameters(url, factory);
                if (params != null && factory.canProcess(params)) {
                    // oh this actually worked!
                    return params;
                }
            } catch (Throwable t) {
                if (Activator.getDefault().isDebugging()) {
                    IStatus warning = new Status(IStatus.WARNING, Activator.PLUGIN_ID, factory
                            .getDisplayName()
                            + " unable to process " + url, t);
                    Activator.getDefault().getLog().log(warning);
                }
            }
        }
        return null; // could not make use of the provided URL
    }

    /**
     * Find the DataAcessFactory willing to work with the provided params, or null if none is
     * available.
     * 
     * @param params
     * @return DataAccessFactory for the params, or null if none is available.
     */
    public static DataAccessFactory findDataAcessFactory( Map<String, Serializable> params ) {
        Iterator<DataAccessFactory> available = DataAccessFinder.getAvailableDataStores();
        while( available.hasNext() ) {
            DataAccessFactory factory = available.next();
            if (params != null && factory.canProcess(params)) {
                return factory;
            }
        }
        return null; // could not make use of the provided parameters
    }

    /**
     * Use the url to generate some connection parameters that pass factory.canProcess or return
     * null if not possible.
     * 
     * @param url
     * @param factory
     * @return connectionParameters based on the provided url, or null if not possible
     */
    static Map<String, Serializable> createConnectionParameters( URL url, DataAccessFactory factory ) {
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
    public static boolean consider( DataAccessFactory factory, URL url ) {
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
     * 
     * @param id ID to use if possible
     * @param params ConnectionParameters
     */
    public IService createService( URL providedId, Map<String, Serializable> params ) {
        if( DataStoreConnectionFactory.DO_NOTHING){
            return null;
        }
        Iterator<DataAccessFactory> available = DataAccessFinder.getAvailableDataStores();
        while( available.hasNext() ) {
            DataAccessFactory factory = available.next();
            if (factory.canProcess(params)) {
                ID id = createID(providedId, factory, params);
                return new DataStoreService(id, factory, params);
            }
        }
        return null; // could not use
        /*
         * try { DataStore dataStore = DataStoreFinder.getDataStore( params ); } catch (IOException
         * e) { if( Activator.getDefault().isDebugging() ){ IStatus status = new Status(
         * IStatus.OK,Activator.PLUGIN_ID, "Could not connect to GeoTools Datastore", e );
         * Activator.getDefault().getLog().log(status); } } return null;
         */
    }
    public static Param lookupParam( DataAccessFactory factory, Class< ? > type ) {
        if (type == null)
            return null;
        for( Param param : factory.getParametersInfo() ) {
            if (type.isAssignableFrom(param.type)) {
                return param;
            }
        }
        return null;
    }
    public static Param lookupParam( DataAccessFactory factory, String key ) {
        if (key == null)
            return null;
        for( Param param : factory.getParametersInfo() ) {
            if (key.equalsIgnoreCase(param.key)) {
                return param;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T lookup( DataAccessFactory factory, Map<String, Serializable> params,
            Class<T> type ) {
        Param param = lookupParam(factory, URL.class);
        if (param != null) {
            T value;
            try {
                value = (T) param.lookUp(params); // find the value
                if (value != null) {
                    return value;
                }
            } catch (IOException e) {
                if (Activator.getDefault().isDebugging()) {
                    e.printStackTrace();
                }
            }
        }
        return null; // not found!
    }

    public static ID createID( URL providedId, DataAccessFactory factory,
            Map<String, Serializable> params ) {
        if (providedId != null) {
            // one was already provided!
            return new ID(providedId, factory.getDisplayName());
        }
        URL url = lookup(factory, params, URL.class);
        if (url != null) {
            // this should handle all files and wfs :-)
            return new ID(url, factory.getDisplayName());
        }
        File file = lookup(factory, params, File.class);
        if (file != null) {
            URL fileUrl = DataUtilities.fileToURL(file);
            if (fileUrl != null) {
                return new ID(fileUrl, factory.getDisplayName());
            }
        }
        if (factory instanceof JDBCDataStoreFactory) {
            // dbtype://host:port/schema
            JDBCDataStoreFactory jdbcFactory = (JDBCDataStoreFactory) factory;
            try {
                final Param DBTYPE = lookupParam(factory, JDBCDataStoreFactory.DBTYPE.key);
                String dbType = (String) DBTYPE.lookUp(params);

                final Param HOST = lookupParam(factory, JDBCDataStoreFactory.HOST.key);
                String host = (String) HOST.lookUp(params);

                // needed to look up the actual PORT
                final Param PORT = lookupParam(factory, JDBCDataStoreFactory.PORT.key);
                Integer port = (Integer) PORT.lookUp(params);

                final Param SCHEMA = lookupParam(factory, JDBCDataStoreFactory.SCHEMA.key);
                String schema = (String) SCHEMA.lookUp(params);
                if (schema == null)
                    schema = "";

                ID id = new ID(dbType + "://" + host + ":" + port + "/" + schema, factory
                        .getDisplayName());

                return id;
            } catch (IOException e) {
                if (Activator.getDefault().isDebugging()) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
