/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.geotools.process;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension;

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
