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
package net.refractions.udig.catalog.internal.oracle;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.AbstractDataStoreServiceExtension;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension2;
import net.refractions.udig.catalog.oracle.internal.Messages;

import org.geotools.data.oracle.OracleDataStoreFactory;

/**
 * Oracle Service Extension implementation.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class OracleServiceExtension extends AbstractDataStoreServiceExtension implements ServiceExtension2 {
    /**
     *
     * @see net.refractions.udig.catalog.ServiceExtension#createService(java.net.URI, java.util.Map)
     * @param id
     * @param params
     * @return
     */
    public IService createService( URL id, Map<String,Serializable> params ) {
        try {
            if (!getFactory().canProcess(params))
                return null;
        } catch (Exception e) {
            return null;
        }
        if(id == null){
            String host = (String)params.get(getFactory().getParametersInfo()[1].key);
            String port = params.get(getFactory().getParametersInfo()[2].key).toString();
            String db = (String)params.get(getFactory().getParametersInfo()[5].key);
            params.put(getFactory().getParametersInfo()[2].key, Integer.valueOf(port));
            try {
                return new OracleServiceImpl(new URL("http://"+host+".oracle.jdbc:"+port+"/"+db),params); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            } catch (MalformedURLException e) {
                // log this?
                OraclePlugin.log( null, e);
                return null;
            }
        }
        return new OracleServiceImpl(id,params);
    }

    /**
     * This is a guess ...
     *
     * @see net.refractions.udig.catalog.ServiceExtension#createParams(java.net.URL)
     * @param url
     * @return
     */
    public Map<String,Serializable> createParams( URL url ) {
        if (!isOracle(url)) {
            return null;
        }
        ParamInfo info=parseParamInfo(url);

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(getFactory().getParametersInfo()[0].key,"oracle"); // dbtype //$NON-NLS-1$
        params.put(getFactory().getParametersInfo()[1].key, info.host); // host
        params.put(getFactory().getParametersInfo()[2].key, info.the_port); // port
        params.put(getFactory().getParametersInfo()[3].key, info.username); // user
        params.put(getFactory().getParametersInfo()[4].key, info.password); // pass
        params.put(getFactory().getParametersInfo()[5].key, info.the_database); // database

        return params;
    }

    private static OracleDataStoreFactory factory = null;
    public static OracleDataStoreFactory getFactory(){
        if(factory == null){
            factory = new OracleDataStoreFactory();
        }
        return factory;
    }

    /** A couple quick checks on the url
     *  This should perhaps do more, but I can't think of a good test that
     *  will tell me without a doubt that the url is an Oracle url.
     */
    private static final boolean isOracle( URL url ){
        if( url==null )
            return false;
        return url.getProtocol().toLowerCase().equals("oracle") || url.getProtocol().toLowerCase().equals("oracle.jdbc") || //$NON-NLS-1$ //$NON-NLS-2$
        url.getProtocol().toLowerCase().equals("jdbc.oracle"); //$NON-NLS-1$
    }



    public String reasonForFailure( URL url ) {
        if (!isOracle(url)) {
            return Messages.OracleServiceExtension_badUrl;
        }
        return reasonForFailure(createParams(url));
    }
}
