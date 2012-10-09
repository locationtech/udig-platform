/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.catalog.internal.wfs.ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wfs.WFSGeoResourceImpl;
import net.refractions.udig.catalog.internal.wfs.WFSServiceExtension;
import net.refractions.udig.catalog.internal.wfs.WFSServiceImpl;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;

import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;

public class WFSConnectionFactory extends UDIGConnectionFactory {

	public boolean canProcess( Object data ) {        
        if( data instanceof IResolve ){
            IResolve resolve = (IResolve) data;
            return resolve.canResolve( WFSDataStore.class );
        }
        return toCapabilitiesURL(data) != null;
    }

	public Map<String, Serializable> createConnectionParameters(Object data) {
		if( data==null )
            return null;
        if( data instanceof WFSServiceImpl ){
            IService wfs = (IService) data;
            return wfs.getConnectionParams();
        }
        URL url = toCapabilitiesURL( data );
        if( url != null ) {
            // well we have a url - lets try it!            
            List<IResolve> list = CatalogPlugin.getDefault().getLocalCatalog().find( url, null );
            for( IResolve resolve : list ){
                if( resolve instanceof WFSServiceImpl) {
                    // got a hit!
                    IService wfs = (IService) resolve;
                    return wfs.getConnectionParams();
                }
                else if (resolve instanceof WFSGeoResourceImpl ){
                    WFSGeoResourceImpl layer = (WFSGeoResourceImpl) resolve;
                    IService wfs;
                    try {
                        wfs = (IService) layer.parent( null );
                        return wfs.getConnectionParams();
                    } catch (IOException e) {
                        checkedURL( layer.getIdentifier() );
                    }                    
                }
            }
            return createParams( url );            
        }        
        return Collections.emptyMap();
	}

	public URL createConnectionURL(Object context) {
		return null;
	}

	  /**
     * Convert "data" to a wfs capabilities url
     * <p>
     * Candidates for conversion are:
     * <ul>
     * <li>URL - from browser DnD
     * <li>URL#layer - from browser DnD
     * <li>WFSService - from catalog DnD
     * <li>IService - from search DnD
     * </ul>
     * </p>
     * <p>
     * No external processing should be required here, it is enough to guess and let
     * the ServiceFactory try a real connect.
     * </p>
     * @param data IService, URL, or something else
     * @return URL considered a possibility for a WFS Capabilities, or null
     */
    URL toCapabilitiesURL( Object data ) {
        if( data instanceof IResolve ){
            return toCapabilitiesURL( (IResolve) data );
        }
        else if( data instanceof URL ){
            return toCapabilitiesURL( (URL) data );
        }
//        else if( CatalogPlugin.locateURL(data) != null ){
//            return toCapabilitiesURL( CatalogPlugin.locateURL(data) );
//        }
        else if (ID.cast(data) != null){
        	return ID.cast(data).toURL();
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
        if( resolve instanceof WFSServiceImpl ){
            return toCapabilitiesURL( (WFSServiceImpl) resolve );
        }
        return toCapabilitiesURL( resolve.getIdentifier() );        
    }
    protected URL toCapabilitiesURL( WFSServiceImpl wfs ){
        return wfs.getIdentifier();                
    }
    protected URL toCapabilitiesURL( URL url ){
        if (url == null) return null;

        String path = url.getPath();
        String query = url.getQuery();
        String protocol = (url.getProtocol() != null ) ? url.getProtocol().toLowerCase() 
        		: null;
        
        if( !"http".equals(protocol) && !"https".equals(protocol)){ //$NON-NLS-1$ //$NON-NLS-2$ 
            return null;

        }
        if (query != null && query.toLowerCase().indexOf("service=wfs") != -1) { //$NON-NLS-1$
            return checkedURL( url );
        }
        if (path != null && path.toLowerCase().indexOf("geoserver/wfs") != -1) { //$NON-NLS-1$
            return checkedURL( url );
        }
        if (url.toExternalForm().toLowerCase().indexOf("WFS") != -1) { //$NON-NLS-1$
            return checkedURL( url );
        }
        return null;
    }
    /** Check that any trailing #layer is removed from the url */
    private static final URL checkedURL( URL url ){
        String check = url.toExternalForm();
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
    
    /** 'Create' params given the provided url, no magic occurs */
    protected Map<String,Serializable> createParams( URL url ){
        WFSServiceExtension factory = new WFSServiceExtension();
        Map<String,Serializable> params = factory.createParams( url );
        if( params != null) return params;
        
        Map<String,Serializable> params2 = new HashMap<String,Serializable>();
        params2.put(WFSDataStoreFactory.URL.key,url);
        
        return params2;
    }
}
