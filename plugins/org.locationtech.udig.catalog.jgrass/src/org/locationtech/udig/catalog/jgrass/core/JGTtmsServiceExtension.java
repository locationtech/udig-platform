/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.core;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension;
import org.locationtech.udig.catalog.URLUtils;

/**
 * <p>
 * Creates a service extention for the JGrasstools TMS service.
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.3.2
 */
public class JGTtmsServiceExtension implements ServiceExtension {

    /**
     * the JGrasstools TMS service key
     */
    public static final String KEY = "org.locationtech.udig.catalog.jgrasstoolstms.urlKey"; //$NON-NLS-1$

    public JGTtmsServiceExtension() {
        super();
    }

    /**
     * @param url the url points to the actual service itself. 
     * @return a parameter map containing the necessary info or null if the url is not for this
     *         service
     */
    public Map<String, Serializable> createParams( URL url ) {
        Map<String, Serializable> params;
        try {
            params = null;
            File propertiesFile = URLUtils.urlToFile(url);
            if (propertiesFile == null || !propertiesFile.exists())
                return null;
            String path = propertiesFile.getAbsolutePath();
            if (path.endsWith(".mapurl")) {
                url = propertiesFile.toURI().toURL();
                params = new HashMap<String, Serializable>();
                params.put(KEY, url);
                return params;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * create the JGrasstools TMS service
     */
    public IService createService( URL id, Map<String, Serializable> params ) {
        // good defensive programming
        if (params == null)
            return null;

        // check for the properties service key
        if (params.containsKey(KEY)) {
            // found it, create the service handle
            return new JGTtmsService(params);
        }

        // key not found
        return null;
    }
}
