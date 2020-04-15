/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.geotools.data;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataAccessFactory.Param;
import org.geotools.data.DataAccessFinder;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.util.URLs;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceExtension;
import org.locationtech.udig.catalog.geotools.Activator;

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
                    File file = URLs.urlToFile(url);
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
        Iterator<DataStoreFactorySpi> available = DataStoreFinder.getAvailableDataStores();
        while( available.hasNext() ) {
            DataStoreFactorySpi factory = available.next();
            if (factory.canProcess(params)) {
                ID id = createID(providedId, factory, params);
                if( id == null ){
                    // cannot represent this in our catalog as we have
                    // no idea how to create an "id" for it
                    continue;
                }
                return new DataStoreService(id, factory, params);
            }
        }
        return null; // could not use
    }
    

    public static ID createID( URL providedId, DataAccessFactory factory,
            Map<String, Serializable> params ) {

        if (providedId != null) {
            // one was already provided!
            return new ID(providedId, factory.getDisplayName());
        }

        GTFormat format = GTFormat.format(factory);
        return format.toID(factory, params);
    }
}
