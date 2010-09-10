package net.refractions.udig.catalog.internal.wmt.ui.wizard;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wmt.wmtsource.ww.LayerSet;
import net.refractions.udig.catalog.internal.wmt.ww.WWGeoResource;
import net.refractions.udig.catalog.internal.wmt.ww.WWService;
import net.refractions.udig.catalog.internal.wmt.ww.WWServiceExtension;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Based on WMSConnectionFactory
 * 
 * @see net.refractions.udig.catalog.internal.wms.ui.WMSConnectionFactory
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WWConnectionFactory extends UDIGConnectionFactory {

	public boolean canProcess(Object context) {
		if(context instanceof IResolve){
           IResolve resolve = (IResolve) context;
           return resolve.canResolve(LayerSet.class);
       }
       return toWWConfigURL(context) != null;        
	}
	
	public Map<String, Serializable> createConnectionParameters( Object context ) {
        if (context instanceof IResolve) {
            Map<String, Serializable> params = createParams((IResolve) context);
            if (!params.isEmpty())
                return params;
        }
        URL url = toWWConfigURL(context);
        if (url == null) {
            // so we are not sure it is a WW Config File url
            // lets guess
            url = CatalogPlugin.locateURL(context);
        }
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
        if(handle instanceof WWService) {
            // got a hit!
            WWService service = (WWService) handle;
            return service.getConnectionParams();
        }
        else if (handle instanceof WWGeoResource ){
            WWGeoResource geoResource = (WWGeoResource) handle;
            WWService service;
            try {
                service = (WWService) geoResource.service(new NullProgressMonitor());
                return service.getConnectionParams();
            } catch (IOException e) {
                checkedURL(geoResource.getIdentifier());
            }                    
        }
        else if(handle.canResolve(LayerSet.class)){
            // must be some kind of handle from a search!
            return createParams(handle.getIdentifier());
        }
        
        return Collections.emptyMap();
    }
	
	/** 'Create' params given the provided url, no magic occurs */
    static public Map<String,Serializable> createParams(URL url){
        WWServiceExtension factory = new WWServiceExtension();
        Map<String,Serializable> params = factory.createParams(url);
        if( params != null) return params;
        
        Map<String,Serializable> params2 = new HashMap<String,Serializable>();
        params2.put(WWService.WW_URL_KEY, url);
        
        return params2;
    }

    
	 /**
     * Convert "data" to a WW Config UUrl
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
    static URL toWWConfigURL( Object data ) {
        if( data instanceof IResolve ){
            return toWWConfigURL( (IResolve) data );
        }
        else if( data instanceof URL ){
            return toWWConfigURL( (URL) data );
        }
        else if( CatalogPlugin.locateURL(data) != null ){
            return toWWConfigURL( CatalogPlugin.locateURL(data) );
        }
        else {
            return null; // no idea what this should be
        }
    }

    static URL toWWConfigURL( IResolve resolve ){
        if( resolve instanceof IService ){
            return toWWConfigURL( (IService) resolve );
        }
        return toWWConfigURL( resolve.getIdentifier() );        
    }

    static URL toWWConfigURL(IService resolve){
        if(resolve instanceof WWService){
            return toWWConfigURL((WWService) resolve);
        }
        return toWWConfigURL(resolve.getIdentifier());        
    }

    /** No further QA checks needed - we know this one works */
    static URL toWWConfigURL(WWService service){
        return service.getIdentifier();                
    }

    /** Quick sanity check to see if url is a WMS url */
    static URL toWWConfigURL(URL url){
        if (url == null) return null;
    
        String PROTOCOL = url.getProtocol();
        String PATH = url.getPath();
        if (PROTOCOL==null || PROTOCOL.isEmpty()) {
            return null;
        }
        
        if (PATH == null || PATH.isEmpty() || !PATH.toLowerCase().contains(".xml")) { //$NON-NLS-1$
            return null;
        }
        
        return checkedURL(url);
    }
    
    /** Check that any trailing #layer is removed from the url */
    static public URL checkedURL(URL url){
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
	    if(context instanceof URL){
	        return (URL) context;
	    }
		return null;
	}

}
