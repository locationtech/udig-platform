

package net.refractions.udig.catalog.internal.postgis.ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.PostGISServiceExtension;
import net.refractions.udig.catalog.internal.postgis.PostGISGeoResource;
import net.refractions.udig.catalog.internal.postgis.PostGISServiceImpl;
import net.refractions.udig.catalog.internal.postgis.PostgisPlugin;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;

import org.geotools.data.postgis.PostgisDataStoreFactory;

public class PostgisUdigConnectionFactory extends UDIGConnectionFactory {

	@Override
    public boolean canProcess(Object context) {
		 return toCapabilitiesURL(context) != null;
	}

	@Override
    public Map<String, Serializable> createConnectionParameters(Object context) {
		if( context instanceof PostGISServiceImpl ){
            PostGISServiceImpl postgis = (PostGISServiceImpl) context;
            return postgis.getConnectionParams();
        }
        URL url = toCapabilitiesURL( context );
        if( url == null ){
            // so we are not sure it is a postgis url
            // lets guess
            url = CatalogPlugin.locateURL(context);
        }
        if( url != null && PostGISServiceExtension.isPostGIS(url)) {
            // well we have a url - lets try it!
            List<IResolve> list = CatalogPlugin.getDefault().getLocalCatalog().find( url, null );
            for( IResolve resolve : list ){
                if( resolve instanceof PostGISServiceImpl) {
                    // got a hit!
                    PostGISServiceImpl postgisService = (PostGISServiceImpl) context;
                    return postgisService.getConnectionParams();
                }
                else if (resolve instanceof PostGISGeoResource ){
                    PostGISGeoResource layer = (PostGISGeoResource) resolve;
                    PostGISServiceImpl postgis;
                    try {
                        postgis = (PostGISServiceImpl) layer.parent( null );
                        return postgis.getConnectionParams();
                    } catch (IOException e) {
                        toCapabilitiesURL( layer.getIdentifier() );
                    }
                }
            }
            return createParams( url );
        }
        return null;
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
    @Override
    public URL createConnectionURL(Object context) {
        if( context instanceof URL ){
            return (URL) context;
        }
        if( context instanceof Map){
            Map params=(Map) context;

            try {
                return PostGISServiceExtension.toURL(params, true);
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
     * Convert "data" to a PostGIS url
     * <p>
     * Candidates for conversion are:
     * <ul>
     * <li>URL - from browser DnD
     * <li>PostGISServiceImpl - from catalog DnD
     * <li>IService - from search DnD
     * </ul>
     * </p>
     * <p>
     * No external processing should be required here, it is enough to guess and let
     * the ServiceFactory try a real connect.
     * </p>
     * @param data IService, URL, or something else
     * @return URL considered a possibility for a PostGIS connection, or null
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
        if( resolve instanceof PostGISServiceImpl ){
            return toCapabilitiesURL( (PostGISServiceImpl) resolve );
        }
        return toCapabilitiesURL( resolve.getIdentifier() );
    }
    /** No further QA checks needed - we know this one works */
    protected URL toCapabilitiesURL( PostGISServiceImpl postgis ){
        return postgis.getIdentifier();
    }
    /** Quick sanity check to see if url is a PostGIS url */
    protected URL toCapabilitiesURL( URL url ){
        if (url == null) return null;

        String protocol = url.getProtocol() != null ? url.getProtocol().toLowerCase()
                : null;

        if (PostGISServiceExtension.isPostGIS(url) ) { //$NON-NLS-1$
            return url;
        }

        if (!"http".equals(protocol) //$NON-NLS-1$
                && !"https".equals(protocol)) { //$NON-NLS-1$
            return null;
        }
        return null;
    }
    /** Quick sanity check to see if url is a PostGIS url String */
    protected URL toCapabilitiesURL( String string ){
        if (string == null) return null;

        if( !string.toLowerCase().contains("postgis.jdbc") && !string.toLowerCase().contains("jdbc.postgis") && !string.toLowerCase().contains("postgis") ) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return null;
        }

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
        String the_schema;
        if( databaseEnd<1 ){
            databaseEnd=string.length();
            the_schema="public"; //$NON-NLS-1$
        }else{
            the_schema=string.substring(databaseEnd+1);
        }
        if( portEnd < 1 ) {
            the_port = string.substring(hostEnd + 1);
            the_database = ""; //$NON-NLS-1$
        } else {
            the_port = string.substring(hostEnd + 1, portEnd);
            the_database = string.substring( portEnd+1, databaseEnd );
        }
        Integer intPort;
        if( !the_port.equalsIgnoreCase("") ) { //$NON-NLS-1$
            intPort = new Integer(the_port);
        } else {
            intPort = new Integer(5432);
        }


        //URL(String protocol, String host, int port, String file)
        URL url = null;
        try {
            url = PostGISServiceExtension.toURL( the_username, the_password, the_host, intPort, the_database, the_schema);

        } catch (MalformedURLException e) {
            // TODO Catch e
            PostgisPlugin.log("bad url", e); //$NON-NLS-1$
        }
        return url;
    }

    /** 'Create' params given the provided url, no magic occurs */
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    protected Map<String,Serializable> createParams( URL url ){
        PostGISServiceExtension serviceFactory = new PostGISServiceExtension();
        Map params = serviceFactory.createParams( url );
        if( params != null) return params;

        Map<String,Serializable> params2 = new HashMap<String,Serializable>();

        params2.put(PostgisDataStoreFactory.DBTYPE.key, "postgis"); //$NON-NLS-1$
        params2.put(PostgisDataStoreFactory.HOST.key, url.getHost());
        String dbport = ((Integer)url.getPort()).toString();
        try {
            params2.put(PostgisDataStoreFactory.PORT.key, new Integer(dbport));
        } catch (NumberFormatException e) {
            params2.put(PostgisDataStoreFactory.PORT.key, new Integer(5432));
        }

        String the_database = url.getPath() == null ? "" : url.getPath(); //$NON-NLS-1$
        params2.put(PostgisDataStoreFactory.DATABASE.key,the_database); // database
        String userInfo = url.getUserInfo() == null ? "" : url.getUserInfo(); //$NON-NLS-1$
        params2.put(PostgisDataStoreFactory.USER.key,userInfo); // user
        params2.put(PostgisDataStoreFactory.PASSWD.key,""); // pass //$NON-NLS-1$

        return params2;
    }


}
