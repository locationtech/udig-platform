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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.refractions.udig.catalog.AbstractDataStoreServiceExtension;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.db2.internal.Messages;

import org.geotools.data.db2.DB2DataStoreFactory;

/**
 * DB2 service extension implementation
 *
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 * @since 1.0.1
 */
public class DB2ServiceExtension extends AbstractDataStoreServiceExtension implements ServiceExtension {
    private static DB2DataStoreFactory factory = null;

    public static DB2DataStoreFactory getFactory() {
        if (factory == null) {
            factory = new DB2DataStoreFactory();
        }
        return factory;
    }

    public IService createService( URL id2, Map<String, Serializable> params ) {
        URL id=id2;
        if( !getFactory().isAvailable() )
            return null;
        // We expect the port value (key '3') to be a String but some of the extensions (ArcServiceExtension)
        // change this from a String to an Integer which causes us to fail.
        // In order to cope with this, we make a local copy of the parameters and force the port
        // value to be a String.
        Map<String, Serializable> paramsLocal = new HashMap<String, Serializable>();
        Set<Entry<String, Serializable>> entries = params.entrySet();
        Iterator<Entry<String, Serializable>> it = entries.iterator();
        while (it.hasNext()) {
            Entry<String, Serializable> entry = it.next();
            paramsLocal.put(entry.getKey(), entry.getValue().toString());
        }
        if (!getFactory().canProcess(paramsLocal))
            return null;
        if (id == null) {
            id = paramsToUrl(paramsLocal);
        }
        if (id == null) {
            return null;    // should we actually throw an exception?
        }
        return new DB2Service(id, params);
    }
    /**
     * Returns the database parameter values as a pseudo-URL.
     * @param params
     * @return a pseudo-URL value
     */
    protected URL paramsToUrl(Map<String, Serializable> params) {
        URL dbUrl = null;
        String host = (String) params.get(getFactory().getParametersInfo()[1].key);
        String port = (String) params.get(getFactory().getParametersInfo()[2].key);
        String db = (String) params.get(getFactory().getParametersInfo()[3].key);
        try {
            dbUrl = new URL("http://" + host + ".db2.jdbc:" + port + "/" + db); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } catch (MalformedURLException e) {
            // log this?
            e.printStackTrace();
        }
        return dbUrl;
    }

    public Map<String,Serializable> createParams( URL url ) {
        if (!isDB2URL(url)) {
            return null;
        }

        ParamInfo info = parseParamInfo(url);

        Map<String,Serializable> params = new HashMap<String,Serializable>();
        params.put(getFactory().getParametersInfo()[0].key,"db2"); // dbtype //$NON-NLS-1$
        params.put(getFactory().getParametersInfo()[1].key,info.host); // host
        params.put(getFactory().getParametersInfo()[2].key,info.the_port); // port
        params.put(getFactory().getParametersInfo()[3].key,info.the_database); // database
        params.put(getFactory().getParametersInfo()[4].key,info.username); // user
        params.put(getFactory().getParametersInfo()[5].key,info.password); // pass
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
}
