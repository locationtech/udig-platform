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
package net.refractions.udig.catalog.internal.gml;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.AbstractDataStoreServiceExtension;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.gml.internal.Messages;

import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFactorySpi.Param;
import org.geotools.data.gml.GMLDataStoreFactory;

/**
 * GML Service Extension implementation.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class GMLServiceExtension extends AbstractDataStoreServiceExtension implements ServiceExtension {

    private static Param URLP;
    private static GMLDataStoreFactory gmlDSFactory;

    public static GMLDataStoreFactory getGMLDSFactory(){
        if(gmlDSFactory == null)
            gmlDSFactory = new GMLDataStoreFactory();
        return gmlDSFactory;
    }

    /**
     * Returns the GMLDataStoreFactory URL Param
     *
     * @return
     */
    public static Param getURLP() {
        if(URLP == null) {
            URLP = getGMLDSFactory().getParametersInfo()[0];
        }
        return URLP;
    }

    public IService createService( URL id, Map<String,Serializable> params ) {
    	if(params.containsKey(getURLP().key) ){
            URL url = null;
            try {
            if(params.get(getURLP().key) instanceof String){
                url = new URL((String)params.get(getURLP().key));
                params.put(getURLP().key,url);
            }else{
                    url = (URL)getURLP().parse(params.get(getURLP().key).toString());
                    params.put(getURLP().key,url);
            }
            } catch (Throwable e1) {
                GmlPlugin.log("", e1); //$NON-NLS-1$
                return null;
            }

            // shapefile ...
            try{
            if( !canProcess((URL) params.get(getURLP().key)))
                return null;
            }catch (Exception e) {
                return null;
            }

                if(id == null){
                    return new GMLServiceImpl(url,params);
                }
                return new GMLServiceImpl(id,params);
        }
        return null;
    }

    public Map<String,Serializable> createParams( URL url ) {
        if(getGMLDSFactory().canProcess(url)){
            // shapefile
            HashMap<String,Serializable> params = new HashMap<String,Serializable>();
            params.put(GMLDataStoreFactory.URLP.key,url);
            params.put(GMLDataStoreFactory.TIMEOUT.key, 20000);
            return params;
        }
        return null;
    }

    private boolean canProcess( URL id ) {
        if(id == null) {
            return false;
        }

        return id.toExternalForm().toUpperCase().endsWith(".GML"); //$NON-NLS-1$
    }

    @Override
    protected String doOtherChecks( Map<String, Serializable> params ) {
        return null;
    }

    @Override
    protected DataStoreFactorySpi getDataStoreFactory() {
        return getGMLDSFactory();
    }

    public String reasonForFailure( URL url ) {
        if (!canProcess(url)) {
            return Messages.GMLServiceExtension_notGMLExt;
        }
        return reasonForFailure(createParams(url));
    }

}
