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
package net.refractions.udig.catalog.internal.wfs;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.AbstractDataStoreServiceExtension;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.internal.wfs.UDIGWFSDataStoreFactory.UDIGWFSDataStore;
import net.refractions.udig.catalog.wfs.internal.Messages;

import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.wfs.WFSDataStoreFactory;

/**
 * Service extension for WFS Services
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class WFSServiceExtension extends AbstractDataStoreServiceExtension implements ServiceExtension {

    private static WFSDataStoreFactory wfsDSFactory;
    public static WFSDataStoreFactory getWFSDSFactory(){
        if(wfsDSFactory == null)
            wfsDSFactory = new UDIGWFSDataStoreFactory();
        return wfsDSFactory;
    }

    public IService createService( URL id, Map<String,Serializable> params ) {
        if(params == null || !params.containsKey(WFSDataStoreFactory.URL.key))
            return null;

        if( !params.containsKey(WFSDataStoreFactory.LENIENT.key) )
            params.put(WFSDataStoreFactory.LENIENT.key, true);
        if( !params.containsKey(WFSDataStoreFactory.TRY_GZIP.key) )
            params.put(WFSDataStoreFactory.TRY_GZIP.key, true);
        if(id == null){
            URL base = (URL)params.get(WFSDataStoreFactory.URL.key);
            base = base == null?null:UDIGWFSDataStore.createGetCapabilitiesRequest(base);
            return new WFSServiceImpl(base,params);
        }
        return new WFSServiceImpl(id,params);
    }

    public Map<String,Serializable> createParams( URL url ) {
        if (!isWFS(url)) {
            return null;
        }

        // wfs check
        Map<String,Serializable> params = new HashMap<String,Serializable>();
        params.put(WFSDataStoreFactory.URL.key,url);
        params.put(WFSDataStoreFactory.BUFFER_SIZE.key, 100);
        params.put(WFSDataStoreFactory.LENIENT.key, true);
        params.put(WFSDataStoreFactory.TRY_GZIP.key, true);

        // don't check ... it blocks
        // (XXX: but we are using that to figure out if the service will work?)
        return params;
    }

    /** A couple quick checks on the url */
    private static final boolean isWFS( URL url ){
        return processURL(url)==null;
    }

    @Override
    protected String doOtherChecks( Map<String, Serializable> params ) {
        return null;
    }

    @Override
    protected DataStoreFactorySpi getDataStoreFactory() {
        return getWFSDSFactory();
    }

    public String reasonForFailure( URL url ) {
        String result=processURL(url);
        if( result!=null )
            return result;
        return reasonForFailure(createParams(url));
    }

    private static String processURL( URL url ) {
        String PATH = url.getPath();
        String QUERY = url.getQuery();
        String PROTOCOL = url.getProtocol();

        if( PROTOCOL.indexOf("http") == -1 ){ //$NON-NLS-1$
            return Messages.WFSServiceExtension_protocol+"'"+PROTOCOL+"'";  //$NON-NLS-1$//$NON-NLS-2$
        }
        if( QUERY != null && QUERY.toUpperCase().indexOf( "SERVICE=" ) != -1){ //$NON-NLS-1$
            int indexOf = QUERY.toUpperCase().indexOf( "SERVICE=" ); //$NON-NLS-1$
            // we have a service! it better be wfs
            if( QUERY.toUpperCase().indexOf( "SERVICE=WFS") == -1 ){ //$NON-NLS-1$
                int endOfExp = QUERY.indexOf('&', indexOf);
                if( endOfExp == -1 )
                	endOfExp=QUERY.length();
                if( endOfExp>indexOf+8)
                	return Messages.WFSServiceExtension_badService+QUERY.substring(indexOf+8, endOfExp );
                else{
                	return Messages.WFSServiceExtension_badService+""; //$NON-NLS-1$
                }
            }
        }
        if( PATH != null && PATH.toUpperCase().indexOf( "GEOSERVER/WFS" ) != -1){ //$NON-NLS-1$
            return null;
        }
        return null; // try it anyway
    }

}
