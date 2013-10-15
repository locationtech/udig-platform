/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.internal.wmsc;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.ServiceExtension2;
import net.refractions.udig.catalog.wms.internal.Messages;

/**
 * A new service extension for a WMS-C Service as defined by:
 * 
 * http://wiki.osgeo.org/wiki/WMS_Tiling_Client_Recommendation
 * 
 * @author Emily Gouge (Refractions Research, Inc)
 * @since 1.1.0
 */
public class WMSCServiceExtension implements ServiceExtension2 {

    /**
     * Creates a new service
     */
    public WMSCServiceExtension(){
        super();
    }
    
    
    /**
     * Creates a new WMSCService Handle
     */
    public IService createService( URL id, Map<String, Serializable> params ) {
        if (params == null)
            return null;

        if (!params.containsKey(WMSCServiceImpl.WMSC_URL_KEY) && id != null){
            return null;
        }
        
        URL extractedId = extractId(params);
        if (extractedId != null) {
            if (id != null)
                return new WMSCServiceImpl(id, params);
            else
                return new WMSCServiceImpl(extractedId, params);
        }

        return null;
    }
    
    /**
     * Extracts the service URL from the parameters
     *
     * @param params
     * @return
     */
    private URL extractId( Map<String, Serializable> params ) {
        if (params.containsKey(WMSCServiceImpl.WMSC_URL_KEY)) {
            URL base = null; // base url for service

            if (params.get(WMSCServiceImpl.WMSC_URL_KEY) instanceof URL) {
                base = (URL) params.get(WMSCServiceImpl.WMSC_URL_KEY); // use provided url for base
            } else {
                try {
                    base = new URL((String) params.get(WMSCServiceImpl.WMSC_URL_KEY));
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                    return null;
                }
                params.remove(params.get(WMSCServiceImpl.WMSC_URL_KEY));
                params.put(WMSCServiceImpl.WMSC_URL_KEY, base);
            }
            return base;
        }
        return null;
    }

    /**
     * Creates paramaters from a given URL
     */
    public Map<String, Serializable> createParams( URL url ) {
        if (!isWMSC(url)) {
            return null;
        }

        // wms check
        Map<String, Serializable> params2 = new HashMap<String, Serializable>();
        params2.put(WMSCServiceImpl.WMSC_URL_KEY, url);

        return params2;
    }

    /**
     * Determines if a given url is a tiled url
     *
     * @param url
     * @return
     */
    public static boolean isWMSC( URL url ) {
        return processURL(url) == null;
    }

    /**
     * @return a string describing why a given url cannot be converted to a service.
     */
    public String reasonForFailure( URL url ) {
        return processURL(url);
    }

    /*
     * Processes a URL verifying if it is a WMS-C service
     */
    private static String processURL( URL url ) {
        if (url == null) {
            return Messages.WMSServiceExtension_nullURL;
        }

        String QUERY = url.getQuery();
        String PROTOCOL = url.getProtocol();
        if (PROTOCOL==null || PROTOCOL.indexOf("http") == -1) { //$NON-NLS-1$ supports 'https' too.
            return Messages.WMSServiceExtension_protocol + "'"+PROTOCOL+"'"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        int found = QUERY == null ? -1 : QUERY.toUpperCase().indexOf( "TILED=TRUE" );
        if( found != -1){ //$NON-NLS-1$
            return null; // this is a WMSC URL :-)
        }
        return Messages.WMSCServiceExtension_nottiled;
    }


    public String reasonForFailure( Map<String, Serializable> params ) {
        URL url = extractId( params );
        return reasonForFailure( url );
    }

    /* 
     * testing
    public static void main(String args[]) throws MalformedURLException{
        URL url = new URL("http://localhost:8080/geowebcache/service/wms?request=getMap&layers=topp:states&format=image/png&srs=EPSG:4326&bbox=-180,-90,0,-26.71875");
        WMSCServiceExtension.processURL(url);
        WMSCServiceExtension xx = new WMSCServiceExtension();  
        xx.createService(url, xx.createParams(url));
    }
    */
}
