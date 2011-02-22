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
package net.refractions.udig.catalog;


import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.internal.postgis.PostGISServiceImpl;
import net.refractions.udig.catalog.internal.postgis.PostgisPlugin;
import net.refractions.udig.catalog.postgis.internal.Messages;
import net.refractions.udig.core.internal.CorePlugin;

import org.geotools.data.postgis.PostgisDataStoreFactory;

/**
 * PostGis ServiceExtension
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class PostGISServiceExtension extends AbstractDataStoreServiceExtension implements ServiceExtension2 {

    /**
     * @param id
     * @param params
     * @return x
     */
    public IService createService( URL id, Map<String, Serializable> params ) {
        if (params != null && params.containsKey(PostgisDataStoreFactory.PORT.key)
                && params.get(PostgisDataStoreFactory.PORT.key) instanceof String) {
            String val = (String) params.get(PostgisDataStoreFactory.PORT.key);
            params.remove(val);
            params.put(PostgisDataStoreFactory.PORT.key, new Integer(val));
        }
        if (!getFactory().canProcess(params))
            return null;
        if (id == null) {
            try {
                URL toURL = toURL(params, true);
                return new PostGISServiceImpl(toURL, params);
            } catch (MalformedURLException e) {
                PostgisPlugin.log("Unable to construct proper service URL.", e); //$NON-NLS-1$
                return null;
            }
        }
        return new PostGISServiceImpl(id, params);
    }

    public static URL toURL( Map<String, Serializable> params, boolean withPass ) throws MalformedURLException {
        String the_host = (String) params.get(PostgisDataStoreFactory.HOST.key);
        Integer intPort = (Integer) params.get(PostgisDataStoreFactory.PORT.key);
        String the_schema= (String) params.get(PostgisDataStoreFactory.SCHEMA.key);
        String the_database = (String) params.get(PostgisDataStoreFactory.DATABASE.key);
        String the_username = (String) params.get(PostgisDataStoreFactory.USER.key); // bug used to be 3 DataBase

        String the_password =  "";
        if( withPass ) the_password = (String) params.get(PostgisDataStoreFactory.PASSWD.key);

        URL toURL = toURL(the_username, the_password, the_host, intPort, the_database, the_schema);
        return toURL;
    }

    /**
     * This is a guess ...
     *
     * @see net.refractions.udig.catalog.ServiceExtension#createParams(java.net.URL)
     * @param url
     * @return x
     */
    public Map<String, Serializable> createParams( URL url ) {
        if (!isPostGIS(url)) {
            return null;
        }

        ParamInfo info=parseParamInfo(url);

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(PostgisDataStoreFactory.DBTYPE.key, "postgis"); // dbtype //$NON-NLS-1$
        params.put(PostgisDataStoreFactory.USER.key, info.username); // user
        params.put(PostgisDataStoreFactory.PASSWD.key, info.password); // pass
        params.put(PostgisDataStoreFactory.HOST.key, info.host); // host
        params.put(PostgisDataStoreFactory.DATABASE.key, info.the_database); // database
        params.put(PostgisDataStoreFactory.PORT.key, info.the_port); // port
        params.put(PostgisDataStoreFactory.SCHEMA.key, info.the_schema); // database
       return params;
    }

    private static PostgisDataStoreFactory factory = null;
    /**
     * @return x
     */
    public static PostgisDataStoreFactory getFactory() {
        if (factory == null) {
            factory = new PostgisDataStoreFactory();
        }
        return factory;
    }

    /** A couple quick checks on the url */
    public static final boolean isPostGIS( URL url ) {
        if( url==null )
            return false;
        return url.getProtocol().toLowerCase().equals("postgis") || url.getProtocol().toLowerCase().equals("postgis.jdbc") || //$NON-NLS-1$ //$NON-NLS-2$
        url.getProtocol().toLowerCase().equals("jdbc.postgis"); //$NON-NLS-1$
    }

    public static URL toURL( String username, String password, String host, String database, String schema) throws MalformedURLException {
    	return toURL(username, password, host, 5506, database, schema);
    }

    public static URL toURL( String username, String password, String host, Integer port, String database, String schema) throws MalformedURLException {
    	return toURL(username, password, host, port.toString(), database, schema);
    }

    public static URL toURL( String username, String password, String host, String port, String database, String schema) throws MalformedURLException {
        String the_spec = "postgis.jdbc://" + username //$NON-NLS-1$
                + ":" + password + "@" + host //$NON-NLS-1$ //$NON-NLS-2$
                + ":" + port + "/" + database //$NON-NLS-1$ //$NON-NLS-2$
                + "/" + schema; //$NON-NLS-1$
        return toURL(the_spec);
    }

    public static URL toURL( String the_spec ) throws MalformedURLException {
        return new URL(null, the_spec, CorePlugin.RELAXED_HANDLER);
    }

    public String reasonForFailure( URL url ) {
        if( ! isPostGIS(url) )
            return Messages.PostGISServiceExtension_badURL;
        return reasonForFailure(createParams(url));
    }

}
