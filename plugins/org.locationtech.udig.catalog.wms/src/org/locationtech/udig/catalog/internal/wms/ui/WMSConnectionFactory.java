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
package org.locationtech.udig.catalog.internal.wms.ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.wms.WMSGeoResourceImpl;
import org.locationtech.udig.catalog.internal.wms.WMSServiceExtension;
import org.locationtech.udig.catalog.internal.wms.WMSServiceImpl;
import org.locationtech.udig.catalog.ui.UDIGConnectionFactory;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.ows.wms.WebMapServer;

public class WMSConnectionFactory extends UDIGConnectionFactory {

	public boolean canProcess(Object context) {
		if( context instanceof IResolve ){
           IResolve resolve = (IResolve) context;
           return resolve.canResolve( WebMapServer.class );
       }
       return toCapabilitiesURL(context) != null;        
	}
	
	public Map<String, Serializable> createConnectionParameters(Object context) {
		  if( context instanceof IResolve  ){
	            Map params = createParams( (IResolve) context );
	            if( !params.isEmpty() ) return params;            
	        } 
	        URL url = toCapabilitiesURL( context );
	        if( url != null ) {
	            // well we have a url - lets try it!            
	            List<IResolve> list = CatalogPlugin.getDefault().getLocalCatalog().find( url, null );
	            for( IResolve resolve : list ){
	                Map params = createParams( resolve );
	                if( !params.isEmpty() ) return params; // we got the goods!
	            }
	            return createParams( url );            
	        }        
	        return Collections.EMPTY_MAP;
	}

	static public Map<String,Serializable> createParams( IResolve handle ){
        if( handle instanceof WMSServiceImpl) {
            // got a hit!
            WMSServiceImpl wms = (WMSServiceImpl) handle;
            return wms.getConnectionParams();
        }
        else if (handle instanceof WMSGeoResourceImpl ){
            WMSGeoResourceImpl layer = (WMSGeoResourceImpl) handle;
            WMSServiceImpl wms;
            try {
                wms = layer.service( new NullProgressMonitor());
                return wms.getConnectionParams();
            } catch (IOException e) {
                checkedURL( layer.getIdentifier() );
            }                    
        }
        else if( handle.canResolve( WebMapServer.class )){
            // must be some kind of handle from a search!
            return createParams( handle.getIdentifier() );
        }
        return Collections.EMPTY_MAP;
    }
	
	/** 'Create' params given the provided url, no magic occurs */
    static public Map<String,Serializable> createParams( URL url ){
        WMSServiceExtension factory = new WMSServiceExtension();
        Map params = factory.createParams( url );
        if( params != null) return params;
        
        Map<String,Serializable> params2 = new HashMap<String,Serializable>();
        params2.put(WMSServiceImpl.WMS_URL_KEY,url);
        return params2;
    }

    
	 /**
     * Convert "data" to a wms capabilities url
     * <p>
     * Candidates for conversion are:
     * <ul>
     * <li>URL - from browser DnD
     * <li>URL#layer - from browser DnD
     * <li>WMSService - from catalog DnD
     * <li>WMSGeoResource - from catalog DnD
     * <li>IService - from search DnD
     * </ul>
     * </p>
     * <p>
     * No external processing should be required here, it is enough to guess and let
     * the ServiceFactory try a real connect.
     * </p>
     * @param data IService, URL, or something else
     * @return URL considered a possibility for a WMS Capabilities, or null
     */
    static URL toCapabilitiesURL( Object data ) {
        if( data instanceof IResolve ){
            return toCapabilitiesURL( (IResolve) data );
        }
        else if( data instanceof URL ){
            return toCapabilitiesURL( (URL) data );
        }
//        else if( CatalogPlugin.locateURL(data) != null ){
//            return toCapabilitiesURL( CatalogPlugin.locateURL(data) );
//        }
        else if (ID.cast(data) != null ){
        	return toCapabilitiesURL(ID.cast(data).toURL());
        }
        else {
            return null; // no idea what this should be
        }
    }

    static URL toCapabilitiesURL( IResolve resolve ){
        if( resolve instanceof IService ){
            return toCapabilitiesURL( (IService) resolve );
        }
        return toCapabilitiesURL( resolve.getIdentifier() );        
    }

    static URL toCapabilitiesURL( IService resolve ){
        if( resolve instanceof WMSServiceImpl ){
            return toCapabilitiesURL( (WMSServiceImpl) resolve );
        }
        return toCapabilitiesURL( resolve.getIdentifier() );        
    }

    /** No further QA checks needed - we know this one works */
    static URL toCapabilitiesURL( WMSServiceImpl wms ){
        return wms.getIdentifier();                
    }

    /** Quick sanity check to see if url is a WMS url */
    static URL toCapabilitiesURL( URL url ){
        if (url == null) return null;
    
        String path = url.getPath() == null ? null : url.getPath().toLowerCase();
        String query = url.getQuery() == null ? null : url.getQuery().toLowerCase();
        String protocol = url.getProtocol() == null ? null : url.getProtocol().toLowerCase();
    
        if (!"http".equals(protocol) //$NON-NLS-1$
                && !"https".equals(protocol)) { //$NON-NLS-1$ 
            return null;
        }
        if (query != null && query.indexOf("service=wms") != -1) { //$NON-NLS-1$
            return checkedURL( url );
        }else if( query != null && query.indexOf("service=") == -1 && query.indexOf("request=getcapabilities") != -1){ //$NON-NLS-1$ //$NON-NLS-2$
            try {
                return new URL( url.toString()+"&SERVICE=WMS"); //$NON-NLS-1$
            } catch (MalformedURLException e) {
                return null;
            }
        }
        
        if (path != null && path.toUpperCase().indexOf("GEOSERVER/WMS") != -1 ) { //$NON-NLS-1$
            return checkedURL( url );
        }
        if (url.toExternalForm().indexOf("WMS") != -1) { //$NON-NLS-1$
            return checkedURL( url );
        }
        return null;
    }
    
    /** Check that any trailing #layer is removed from the url */
    static public URL checkedURL( URL url ){
        String check = url.toExternalForm();
        int tiled = check.toUpperCase().indexOf("TILED=TRUE");
        if( tiled != -1 ){
            return null; // we do not support tiled WMS here
        }
        int hash = check.indexOf('#');
        if ( hash == -1 ){
            return url;            
        }
        try {
            return new URL( check.substring(0, hash ));
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
	public URL createConnectionURL(Object context) {
	    if( context instanceof URL ){
	        return (URL) context;
	    }
		return null;
	}

}
