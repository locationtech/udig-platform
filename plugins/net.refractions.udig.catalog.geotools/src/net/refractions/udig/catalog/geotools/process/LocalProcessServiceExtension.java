/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.geotools.process;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;

/**
 * Registers ProcessService in the local catalog.
 * <p>
 * Like MapGraphic these processes represent "local" content and will be registered into the local catalog
 * automatically during startup.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.2.0
 */
public class LocalProcessServiceExtension implements ServiceExtension {
    /**
     * Key used to record the URL indicating a local process service.
     */
    public static final String KEY = "process"; //$NON-NLS-1$
    
    public IService createService( URL id, Map<String, Serializable> params ) {
        if( params.containsKey(KEY)){
            return new LocalProcessService();
        }
        return null;
    }
    
    public Map<String, Serializable> createParams( URL url ) {
        if( url != null && url.toExternalForm().startsWith( LocalProcessService.SERVICE_ID.toString())){
            Map<String,Serializable> map = new HashMap<String,Serializable>();
            map.put( KEY, url );
            return map;   
        }
        return null;
    }

}
