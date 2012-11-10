/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.catalog;


import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.internal.mysql.MySQLPlugin;
import net.refractions.udig.catalog.mysql.internal.Messages;
import net.refractions.udig.core.internal.CorePlugin;

import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataAccessFactory.Param;
import org.geotools.data.mysql.MySQLDataStoreFactory;


/**
 * MySQL ServiceExtension
 * @author David Zwiers, Refractions Research
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySQLServiceExtension extends AbstractDataStoreServiceExtension
    implements ServiceExtension2 {

    /**
     * @param id
     * @param params
     * @return x
     */
    public IService createService( URL id, Map<String, Serializable> params ) {
        if (params != null && params.containsKey(MySQLDataStoreFactory.PORT.key)
                && params.get(MySQLDataStoreFactory.PORT.key) instanceof Integer) {
            String val = params.get(MySQLDataStoreFactory.PORT.key).toString();
            params.remove(val);
            params.put(MySQLDataStoreFactory.PORT.key, val);
        }
        if (!getFactory().canProcess(params))
            return null;
        if (id == null) {
            try {
                URL toURL = toURL(params);
                return new MySQLServiceImpl(toURL, params);
            } catch (MalformedURLException e) {
                MySQLPlugin.log("Unable to construct proper service URL.", e); //$NON-NLS-1$
                return null;
            }
        }
        return new MySQLServiceImpl(id, params);
    }

    public static URL toURL( Map<String, Serializable> params ) throws MalformedURLException {
        String the_host = (String) params.get(MySQLDataStoreFactory.HOST.key);
        String intPort = (String) params.get(MySQLDataStoreFactory.PORT.key);
        String the_database = (String) params.get(MySQLDataStoreFactory.DATABASE.key);
        String the_username = (String) params.get(MySQLDataStoreFactory.USER.key);
        String the_password = (String) params.get(MySQLDataStoreFactory.PASSWD.key);
        
        URL toURL = toURL(the_username, the_password, the_host, intPort, the_database);
        return toURL;
    }

    /**
     * Creates some  Params for mysql based off a url that is passed in
     * 
     * @see net.refractions.udig.catalog.ServiceExtension#createParams(java.net.URL)
     * @param url for the mysql database
     * @return x
     */
    public Map<String, Serializable> createParams( URL url ) {
        if (!isMySQL(url)) {
            return null;
        }

        ParamInfo info=parseParamInfo(url);
        
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(MySQLDataStoreFactory.DBTYPE.key, (Serializable)MySQLDataStoreFactory.DBTYPE.sample);  //$NON-NLS-1$
        params.put(MySQLDataStoreFactory.HOST.key, info.host); 
        params.put(MySQLDataStoreFactory.PORT.key, info.the_port.toString() ); 
        params.put(MySQLDataStoreFactory.DATABASE.key, info.the_database); 
        params.put(MySQLDataStoreFactory.USER.key, info.username); 
        params.put(MySQLDataStoreFactory.PASSWD.key, info.password);
        
        return params;
    }

    private static MySQLDataStoreFactory factory = null;
    /**
     * @return x
     */
    public static MySQLDataStoreFactory getFactory() {
        if (factory == null) {
            factory = new MySQLDataStoreFactory();
        }
        return factory;
    }
    /**
     * Look up Param by key; used to access the correct sample
     * value for DBTYPE.
     *
     * @param key
     * @return
     */
    public static Param getPram( String key ){
        for( Param param : getFactory().getParametersInfo()){
            if( key.equals( param.key )){
                return param;
            }
        }
        return null;
    }

    /** A couple quick checks on the url */
    public static final boolean isMySQL( URL url ) {
        if( url==null )
            return false;
        return url.getProtocol().toLowerCase().equals("mysql") || url.getProtocol().toLowerCase().equals("mysql.jdbc") || //$NON-NLS-1$ //$NON-NLS-2$
        url.getProtocol().toLowerCase().equals("jdbc.mysql"); //$NON-NLS-1$
    }

    public static URL toURL( String username, String password, String host, String database) throws MalformedURLException {
    	return toURL(username, password, host, 3306, database);
    }
    
    public static URL toURL( String username, String password, String host, Integer port, String database) throws MalformedURLException {
    	return toURL(username, password, host, port.toString(), database);
    }
    
    public static URL toURL( String username, String password, String host, String port, String database) throws MalformedURLException {
        String the_spec = "mysql.jdbc://" + username //$NON-NLS-1$
                + ":" + password + "@" + host //$NON-NLS-1$ //$NON-NLS-2$
                + ":" + port + "/" + database //$NON-NLS-1$ //$NON-NLS-2$
                ;
        return toURL(the_spec);
    }

    public static URL toURL( String the_spec ) throws MalformedURLException {
        return new URL(null, the_spec, CorePlugin.RELAXED_HANDLER);
    }

    public String reasonForFailure( URL url ) {
        if( ! isMySQL(url) )
            return Messages.MySQLServiceExtension_badURL; 
        return reasonForFailure(createParams(url));
    }

    @Override
    protected DataStoreFactorySpi getDataStoreFactory() {
        return getFactory();
    }

}
