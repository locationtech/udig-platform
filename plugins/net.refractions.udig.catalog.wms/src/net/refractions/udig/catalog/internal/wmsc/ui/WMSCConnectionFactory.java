/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.wmsc.ui;

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
import net.refractions.udig.catalog.internal.wmsc.WMSCGeoResourceImpl;
import net.refractions.udig.catalog.internal.wmsc.WMSCServiceExtension;
import net.refractions.udig.catalog.internal.wmsc.WMSCServiceImpl;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;
import net.refractions.udig.catalog.wmsc.server.TiledWebMapServer;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Connection factory for a wmsc service.
 * 
 * Note the url must have "tiled=true" to be a wmsc service.
 * 
 * 
 * @author Emily Gouge (Refractions Research, Inc.)
 * @since 1.1.0
 */
public class WMSCConnectionFactory extends UDIGConnectionFactory {

    public WMSCConnectionFactory() {
    }

    /**
     * Determines if a particular context can become a WMSC Service
     */
    public boolean canProcess(Object context) {
        if( context instanceof IResolve ){
          IResolve resolve = (IResolve) context;
          return resolve.canResolve( TiledWebMapServer.class );
      }
      return toCapabilitiesURL(context) != null;        
   }
   
    /**
     * Creates connection parameters from a given context
     */
   public Map<String, Serializable> createConnectionParameters( Object context ) {
        if (context instanceof IResolve) {
            Map<String, Serializable> params = createParams((IResolve) context);
            if (!params.isEmpty())
                return params;
        }
        URL url = toCapabilitiesURL(context);
        if (url != null) {
            // well we have a url - lets try it!
            List<IResolve> list = CatalogPlugin.getDefault().getLocalCatalog().find(url, null);
            for( IResolve resolve : list ) {
                Map<String, Serializable> params = createParams(resolve);
                if (!params.isEmpty())
                    return params; // we got the goods!
            }
            return createParams(url);
        }
        return Collections.emptyMap();
    }

   static public Map<String,Serializable> createParams( IResolve handle ){
       if( handle instanceof WMSCServiceImpl) {
           // got a hit!
           WMSCServiceImpl wms = (WMSCServiceImpl) handle;
           return wms.getConnectionParams();
       }
       else if (handle instanceof WMSCGeoResourceImpl ){
           WMSCGeoResourceImpl layer = (WMSCGeoResourceImpl) handle;
           WMSCServiceImpl wms;
           try {
               wms = layer.service( new NullProgressMonitor());
               return wms.getConnectionParams();
           } catch (IOException e) {
               checkedURL( layer.getIdentifier() );
           }                    
       }
       else if( handle.canResolve( TiledWebMapServer.class )){
           // must be some kind of handle from a search!
           return createParams( handle.getIdentifier() );
       }
       return Collections.emptyMap();
   }
   
   /** 'Create' params given the provided url, no magic occurs */
   static public Map<String,Serializable> createParams( URL url ){
       WMSCServiceExtension factory = new WMSCServiceExtension();
       Map<String,Serializable> params = factory.createParams( url );
       if( params != null) return params;
       
       Map<String,Serializable> params2 = new HashMap<String,Serializable>();
       params2.put(WMSCServiceImpl.WMSC_URL_KEY,url);
       return params2;
   }

   
    /**
    * Convert "data" to a wmsc capabilities url.
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
       else if( ID.cast(data) != null ){
           ID id = ID.cast( data );
           if( id.toURL() != null ){
               return toCapabilitiesURL( id.toURL() );
           }
       }
       return null; // no idea what this should be       
   }

   static URL toCapabilitiesURL( IResolve resolve ){
       if( resolve instanceof IService ){
           return toCapabilitiesURL( (IService) resolve );
       }
       return toCapabilitiesURL( resolve.getIdentifier() );        
   }

   static URL toCapabilitiesURL( IService resolve ){
       if( resolve instanceof WMSCServiceImpl ){
           return toCapabilitiesURL( (WMSCServiceImpl) resolve );
       }
       return toCapabilitiesURL( resolve.getIdentifier() );        
   }

   /** No further QA checks needed - we know this one works */
   static URL toCapabilitiesURL( WMSCServiceImpl wms ){
       return wms.getIdentifier();                
   }

   /** 
    * Quick sanity check to see if url is a WMSC url;
    * Must contain "tiled=true"
    *  
    */
   static URL toCapabilitiesURL( URL url ){
       if (url == null) return null;
   
       String query = url.getQuery() == null ? null : url.getQuery().toLowerCase();
       String protocol = url.getProtocol() == null ? null : url.getProtocol().toLowerCase();
   
       if (!"http".equals(protocol) //$NON-NLS-1$
               && !"https".equals(protocol)) { //$NON-NLS-1$ 
           return null;
       }
       
       if( query != null && query.indexOf( "tiled=true" ) != -1){ //$NON-NLS-1$
           return checkedURL(url);
       }
       return null;
       
       
   }
   
   /** Check that any trailing #layer is removed from the url */
   static public URL checkedURL( URL url ){
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
   
   public URL createConnectionURL(Object context) {
       // TODO Auto-generated method stub
       return null;
   }

}
