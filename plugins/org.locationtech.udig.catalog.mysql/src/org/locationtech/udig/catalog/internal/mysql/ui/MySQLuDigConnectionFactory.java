/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.mysql.ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.MySQLGeoResource;
import org.locationtech.udig.catalog.MySQLServiceExtension;
import org.locationtech.udig.catalog.MySQLServiceImpl;
import org.locationtech.udig.catalog.internal.mysql.MySQLPlugin;
import org.locationtech.udig.catalog.ui.UDIGConnectionFactory;
import static org.geotools.data.mysql.MySQLDataStoreFactory.*;
/**
 * The Factory class used to build connections for mysql
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySQLuDigConnectionFactory extends UDIGConnectionFactory {

	@Override
    public boolean canProcess(Object context) {
		 return toCapabilitiesURL(context) != null;
	}

	@Override
    public Map<String, Serializable> createConnectionParameters(Object context) {
		if( context instanceof MySQLServiceImpl ){
            MySQLServiceImpl mysql = (MySQLServiceImpl) context;
            return mysql.getConnectionParams();
        }
        URL url = toCapabilitiesURL( context );
        if( url == null ){
            // so we are not sure it is a mysql url
            // lets guess
            url = CatalogPlugin.locateURL(context);
        }
        if( url != null && MySQLServiceExtension.isMySQL(url)) {  
            // well we have a url - lets try it!            
            List<IResolve> list = CatalogPlugin.getDefault().getLocalCatalog().find( url, null );
            for( IResolve resolve : list ){
                if( resolve instanceof MySQLServiceImpl) {
                    // got a hit!
                    MySQLServiceImpl mysqlService = (MySQLServiceImpl) context;
                    return mysqlService.getConnectionParams();
                }
                else if (resolve instanceof MySQLGeoResource ){
                    MySQLGeoResource layer = (MySQLGeoResource) resolve;
                    MySQLServiceImpl mysql;
                    try {
                        mysql = (MySQLServiceImpl) layer.parent( null );
                        return mysql.getConnectionParams();
                    } catch (IOException e) {
                        toCapabilitiesURL( layer.getIdentifier() );
                    }                    
                }
            }
            return createParams( url );            
        }        
        return null;
	}

	@SuppressWarnings("unchecked")
    @Override
    public URL createConnectionURL(Object context) {
        if( context instanceof URL ){
            return (URL) context;
        }
        if( context instanceof Map){
            Map params=(Map) context;
            
            try {
                return MySQLServiceExtension.toURL(params);
            } catch (MalformedURLException e) {
                return null;
            } 
            
        }
        if( context instanceof String ){
            return toCapabilitiesURL((String)context);
        }
		return null;
	}

	/**
     * Convert "data" to a MySQL url
     * <p>
     * Candidates for conversion are:
     * <ul>
     * <li>URL - from browser DnD
     * <li>MySQLServiceImpl - from catalog DnD
     * <li>IService - from search DnD
     * </ul>
     * </p>
     * <p>
     * No external processing should be required here, it is enough to guess and let
     * the ServiceFactory try a real connect.
     * </p>
     * @param data IService, URL, or something else
     * @return URL considered a possibility for a MySQL connection, or null
     */
    protected URL toCapabilitiesURL( Object data ) {
        if( data instanceof IResolve ){
            return toCapabilitiesURL( (IResolve) data );
        }
        else if( data instanceof URL ){
            return toCapabilitiesURL( (URL) data );
        }
        else if( data instanceof String ){
            return toCapabilitiesURL( (String) data );
        }
        else if( CatalogPlugin.locateURL(data) != null ){
            return toCapabilitiesURL( CatalogPlugin.locateURL(data) );
        }
        else {
            return null; // no idea what this should be
        }
    }
    protected URL toCapabilitiesURL( IResolve resolve ){
        if( resolve instanceof IService ){
            return toCapabilitiesURL( (IService) resolve );
        }
        return toCapabilitiesURL( resolve.getIdentifier() );        
    }
    protected URL toCapabilitiesURL( IService resolve ){
        if( resolve instanceof MySQLServiceImpl ){
            return toCapabilitiesURL( (MySQLServiceImpl) resolve );
        }
        return toCapabilitiesURL( resolve.getIdentifier() );        
    }
    /** No further QA checks needed - we know this one works */
    protected URL toCapabilitiesURL( MySQLServiceImpl postgis ){
        return postgis.getIdentifier();                
    }
    /** Quick sanity check to see if url is a MySQL url */
    protected URL toCapabilitiesURL( URL url ){
        if (url == null) return null;

        String protocol = url.getProtocol() != null ? url.getProtocol().toLowerCase()
                : null;

        if (!"http".equals(protocol) //$NON-NLS-1$
                && !"https".equals(protocol)) { //$NON-NLS-1$ 
            return null;
        }
        if (url.toExternalForm().indexOf("mysql.jdbc") != -1) { //$NON-NLS-1$
            return url;
        }
        return null;
    }
    /** Quick sanity check to see if url is a MySQL url String */
    protected URL toCapabilitiesURL( String string ){
        if (string == null) return null;

        if( !string.contains("mysql.jdbc") && !string.contains("jdbc.mysql") && !string.contains("mysql") ) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return null;
        }
        //jdbc.mysql://username:password@host:port/database
        int startindex = string.indexOf("//") + 2; //$NON-NLS-1$
        int usernameEnd= string.indexOf(":", startindex); //$NON-NLS-1$
        int passwordEnd= string.indexOf("@", usernameEnd); //$NON-NLS-1$
        int hostEnd = string.indexOf(":", passwordEnd); //$NON-NLS-1$
        int portEnd = string.indexOf("/", hostEnd); //$NON-NLS-1$
        int databaseEnd = string.indexOf("/", portEnd+1); //$NON-NLS-1$
        
        //int databaseEnd = string.indexOf(" ", databaseStart);
        String the_host = string.substring(passwordEnd+1, hostEnd);
        String the_username=string.substring(startindex, usernameEnd);
        String the_password=string.substring(usernameEnd+1, passwordEnd);
        String the_port;
        String the_database;

        
        if( portEnd < 1 ) {
            the_port = string.substring(hostEnd + 1);
            the_database = ""; //$NON-NLS-1$
        } else {
            the_port = string.substring(hostEnd + 1, portEnd);
            the_database = string.substring( portEnd+1, databaseEnd );
        }
        Integer intPort;
        try{
            intPort = Integer.valueOf(the_port);
        } catch (NumberFormatException e)
        {
            intPort = Integer.valueOf(3306);
        }

        
        //URL(String protocol, String host, int port, String file)
        URL url = null;
        try {
            url = MySQLServiceExtension.toURL( the_username, the_password, the_host, intPort, the_database);      
        } catch (MalformedURLException e) {
            MySQLPlugin.log("bad url", e); //$NON-NLS-1$
        }
        return url;
    }
    
    /** 'Create' params given the provided url, no magic occurs */
    @SuppressWarnings("unchecked")
    protected Map<String,Serializable> createParams( URL url ){
        MySQLServiceExtension serviceFactory = new MySQLServiceExtension();
        Map params = serviceFactory.createParams( url );
        if( params != null) return params;
        
        Map<String,Serializable> params2 = new HashMap<String,Serializable>();
        

        params2.put( DBTYPE.key, (Serializable) DBTYPE.sample); //$NON-NLS-1$
        params2.put( HOST.key, url.getHost());
        
        params2.put(PORT.key, Integer.valueOf( url.getPort() != -1 ? url.getPort() : 3306 ));


        String the_database = url.getPath() == null ? "" : url.getPath(); //$NON-NLS-1$
        params2.put( DATABASE.key,the_database); // database
        String userInfo = url.getUserInfo() == null ? "" : url.getUserInfo(); //$NON-NLS-1$
        params2.put( USER.key,userInfo); // user
        params2.put( PASSWD.key,""); // pass //$NON-NLS-1$
        
        return params2;
    }
    
   
}
