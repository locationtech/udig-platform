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
package net.refractions.udig.catalog.internal.wms;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension2;
import net.refractions.udig.catalog.wms.internal.Messages;

/**
 * A service extension for creating WMS Services
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class WMSServiceExtension implements ServiceExtension2 {

    public IService createService( URL id, Map<String, Serializable> params ) {
        if (params == null)
            return null;

        if ((!params.containsKey(WMSServiceImpl.WMS_URL_KEY) && id == null)
                && !params.containsKey(WMSServiceImpl.WMS_WMS_KEY)) {
            return null; // nope we don't have a WMS_URL_KEY
        }

        URL extractedId = extractId(params);
        if (extractedId != null) {
            if (id != null)
                return new WMSServiceImpl(id, params);
            else
                return new WMSServiceImpl(extractedId, params);
        }

        return null;
    }
    private URL extractId( Map<String, Serializable> params ) {
        if (params.containsKey(WMSServiceImpl.WMS_URL_KEY)) {
            URL base = null; // base url for service

            if (params.get(WMSServiceImpl.WMS_URL_KEY) instanceof URL) {
                base = (URL) params.get(WMSServiceImpl.WMS_URL_KEY); // use provided url for base
            } else {
                try {
                    base = new URL((String) params.get(WMSServiceImpl.WMS_URL_KEY)); // upcoverting
                                                                                        // string to
                                                                                        // url for
                                                                                        // base
                } catch (MalformedURLException e1) {
                    // log this?
                    e1.printStackTrace();
                    return null;
                }
                params.remove(params.get(WMSServiceImpl.WMS_URL_KEY));
                params.put(WMSServiceImpl.WMS_URL_KEY, base);
            }
            // params now has a valid url

            return base;
        }
        return null;
    }

    public Map<String, Serializable> createParams( URL url ) {
        if (!isWMS(url)) {
            return null;
        }

        // wms check
        Map<String, Serializable> params2 = new HashMap<String, Serializable>();
        params2.put(WMSServiceImpl.WMS_URL_KEY, url);

        return params2;
    }

    public static boolean isWMS( URL url ) {
        return processURL(url) == null;
    }

    public String reasonForFailure( Map<String, Serializable> params ) {
        URL id = extractId(params);
        if (id == null)
            return Messages.WMSServiceExtension_needsKey + WMSServiceImpl.WMS_URL_KEY
                    + Messages.WMSServiceExtension_nullValue;
        return reasonForFailure(id);
    }

    public String reasonForFailure( URL url ) {
        return processURL(url);
    }

    private static String processURL( URL url ) {
        if (url == null) {
            return Messages.WMSServiceExtension_nullURL;
        }

        String PATH = url.getPath();
        String QUERY = url.getQuery();
        String PROTOCOL = url.getProtocol();
        if (PROTOCOL==null || PROTOCOL.indexOf("http") == -1) { //$NON-NLS-1$ supports 'https' too.
            return Messages.WMSServiceExtension_protocol + "'"+PROTOCOL+"'"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if( QUERY != null && QUERY.toUpperCase().indexOf( "SERVICE=" ) != -1){ //$NON-NLS-1$
            int indexOf = QUERY.toUpperCase().indexOf( "SERVICE=" ); //$NON-NLS-1$
            // we have a service! it better be wfs
            if( QUERY.toUpperCase().indexOf( "SERVICE=WMS") == -1 ){ //$NON-NLS-1$
                int endOfExp = QUERY.indexOf('&', indexOf);
                if( endOfExp == -1 )
                	endOfExp=QUERY.length();
                if( endOfExp>indexOf+8)
                	return Messages.WMSServiceExtension_badService+QUERY.substring(indexOf+8, endOfExp );
                else{
                	return Messages.WMSServiceExtension_badService+""; //$NON-NLS-1$
                }
            }
        } else if (PATH != null && PATH.toUpperCase().indexOf("GEOSERVER/WMS") != -1) { //$NON-NLS-1$
            return null;
        }
        return null; // try it anyway
    }
}
