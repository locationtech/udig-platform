/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2005, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.db2;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.locationtech.udig.catalog.AbstractDataStoreServiceExtension;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension;
import org.locationtech.udig.catalog.db2.DB2Plugin;
import org.locationtech.udig.catalog.db2.internal.Messages;

import org.eclipse.core.runtime.Platform;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataAccessFinder;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.db2.DB2NGJNDIDataStoreFactory;
import org.geotools.data.db2.DB2NGDataStoreFactory;
import static org.geotools.data.db2.DB2NGDataStoreFactory.*;
/**
 * DB2 service extension implementation.
 * 
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 * @author David Adler, Adtech Geospatial,dadler@adtechgeospatial.com
 * @since 1.0.1
 */
public class DB2ServiceExtension extends AbstractDataStoreServiceExtension implements ServiceExtension {
    private static DB2NGDataStoreFactory factory = null;
    private static boolean available = true;
    
    /**
     * Factory describing connection parameters.
     * @return factory describing DB2 connection parameters
     */
    // We want to use a DB2NGDataStoreFactory but this is not in the list of DataStores
    // DB2NGJNDIDataStoreFactory is returned so if we find this,
    // create a DB2NGDataStoreFactory
    public synchronized static DB2NGDataStoreFactory getFactory() {
        if (available && factory == null ) {
        	Iterator<DataAccessFactory> dataStores = DataAccessFinder.getAvailableDataStores();
        	while( dataStores.hasNext() ){
        		DataAccessFactory access = dataStores.next();
        		if( access instanceof DB2NGJNDIDataStoreFactory){
 //       			factory = (DB2NGJNDIDataStoreFactory) access;
        		        factory = new DB2NGDataStoreFactory();
        			break;
        		}
        	}
        	if( factory == null ){
        		available = false; // not available! oh no!        		
        	}
        }
        return factory;
    }

    public IService createService( URL id, Map<String, Serializable> params ) {
        
        try {
            if( getFactory() == null || !getFactory().isAvailable() ){
                return null; // factory not available
            }
            if (!getFactory().canProcess(params)) {
                return null; // the factory cannot use these parameters
            }
        } catch (Exception unexpected) {
            if (Platform.inDevelopmentMode()) {
                // this should never happen
                DB2Plugin.log("DB2ServiceExtension.canProcess failed out with: " //$NON-NLS-1$
                        + unexpected, unexpected);
            }
            return null; // the factory cannot really use these parameters
        }
        
        // generate an id if needed (this is only required the first time)
        if (id == null) {
            id = paramsToUrl(params);
        }
        if (id == null) {
            return null;    // should we actually throw an exception?
        }       
        return new DB2Service(id, params);
    }

    /**
     * Returns the database parameter values as a pseudo-URL.
     * <p>
     * This appears to be used to create an ID.
     * </p>
     * 
     * @param params
     * @return a pseudo-URL value
     */
    protected URL paramsToUrl(Map<String, Serializable> params) {
        URL dbUrl = null;        
        try {
            Object host = DB2NGJNDIDataStoreFactory.HOST.lookUp( params );
            Object port = DB2NGJNDIDataStoreFactory.PORT.lookUp( params );
            Object db = DB2NGJNDIDataStoreFactory.DATABASE.lookUp( params );
            
            dbUrl = new URL("http://" + host + ".db2.jdbc:" + port + "/" + db); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } catch (Exception e) {
            if( Platform.inDevelopmentMode()){
                e.printStackTrace();
            }
        }
        return dbUrl;
    }

    public Map<String,Serializable> createParams( URL url ) {
        if (!isDB2URL(url)) {
            return null;
        }    
        ParamInfo info = parseParamInfo(url);
        
        Map<String,Serializable> params = new HashMap<String,Serializable>();
        params.put(DBTYPE.key, (Serializable)DBTYPE.sample); // dbtype 
        params.put(HOST.key,info.host); // host
        params.put(PORT.key,info.the_port); // port
        params.put(DATABASE.key,info.the_database); // database
        params.put(USER.key,info.username); // user
        params.put(PASSWD.key,info.password); // pass
        
        return params;
    }
    /** A couple quick checks on the url 
     * @param url 
     * @return true if this is a DB2 URL
     * */ 
    private static final boolean isDB2URL( URL url ){
        if (url == null )
            return false;
        return url.getProtocol().toLowerCase().equals("db2") || url.getProtocol().toLowerCase().equals("db2.jdbc") || //$NON-NLS-1$ //$NON-NLS-2$ 
        url.getProtocol().toLowerCase().equals("jdbc.db2"); //$NON-NLS-1$
    }

    public String reasonForFailure( URL url ) {
        if(url==null)
            return Messages.DB2ServiceExtension_nullURL;
        if (!isDB2URL(url))
            return Messages.DB2ServiceExtension_notDB2URL;
        return reasonForFailure(createParams(url));
    }

    @Override
    protected DataStoreFactorySpi getDataStoreFactory() {
        return getFactory();
    }
}
