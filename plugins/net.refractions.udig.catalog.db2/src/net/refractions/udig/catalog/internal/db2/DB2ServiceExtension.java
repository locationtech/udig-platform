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

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.refractions.udig.catalog.AbstractDataStoreServiceExtension;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.db2.DB2Plugin;
import net.refractions.udig.catalog.db2.internal.Messages;

import org.eclipse.core.runtime.Platform;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataAccessFinder;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.db2.DB2NGDataStoreFactory;
import static org.geotools.data.db2.DB2NGDataStoreFactory.*;
/**
 * DB2 service extension implementation.
 * 
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 * @since 1.0.1
 */
public class DB2ServiceExtension extends AbstractDataStoreServiceExtension implements ServiceExtension {
    private static DB2NGDataStoreFactory factory = null;
    private static boolean avaialble = true;
    
    /**
     * Factory describing connection parameters.
     * @return factory describing DB2 connection parameters
     */
    public synchronized static DB2NGDataStoreFactory getFactory() {
        if (avaialble && factory == null ) {
        	// factory = new DB2NGDataStoreFactory(); // this was a bad idea
        	Iterator<DataAccessFactory> available = DataAccessFinder.getAvailableDataStores();
        	while( available.hasNext() ){
        		DataAccessFactory access = available.next();
        		if( access instanceof DB2NGDataStoreFactory){
        			factory = (DB2NGDataStoreFactory) access;
        			break;
        		}
        	}
        	if( factory == null ){
        		avaialble = false; // not available! oh no!        		
        	}
        }
        return factory;
    }

    public IService createService( URL id, Map<String, Serializable> params ) {
        
        // We expect the port value (key '3') to be a String but some of the extensions (ArcServiceExtension)
        // change this from a String to an Integer which causes us to fail.
        // In order to cope with this, we make a local copy of the parameters and force the port
        // value to be a String.
        /*
        Map<String, Serializable> paramsLocal = new HashMap<String, Serializable>();      
        Set<Entry<String, Serializable>> entries = params.entrySet();
        Iterator<Entry<String, Serializable>> it = entries.iterator();
        while (it.hasNext()) {
            Entry<String, Serializable> entry = it.next();
            paramsLocal.put(entry.getKey(), entry.getValue().toString());
        }
        */
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
                DB2Plugin.log("DB2ServiceExtension.canProcess errored out with: "
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
            Object host = DB2NGDataStoreFactory.HOST.lookUp( params );
            Object port = DB2NGDataStoreFactory.PORT.lookUp( params );
            Object db = DB2NGDataStoreFactory.DATABASE.lookUp( params );
            
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
        params.put(DBTYPE.key, (Serializable)DBTYPE.sample); // dbtype //$NON-NLS-1$
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
