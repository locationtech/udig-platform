/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.memory;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension;
import org.locationtech.udig.catalog.memory.internal.MemoryServiceImpl;

import org.geotools.data.DataUtilities;
import org.geotools.data.memory.MemoryDataStore;

public class MemoryServiceExtensionImpl implements ServiceExtension {
    /** service creation key **/
    public static final String KEY = "scratch"; //$NON-NLS-1$

    /** special memory service url **/
    public static final URL URL;
    static {
        URL tmp;
       try {
        tmp = new URL("http://localhost/scratch"); //$NON-NLS-1$
       }
       catch (MalformedURLException e) {
           tmp=null;
            e.printStackTrace();
       }
       URL=tmp;
    }

    // contains a map of id->MemoryService implementation.
    public static final Map<URL, MemoryServiceImpl> impl=Collections.synchronizedMap(new HashMap<URL, MemoryServiceImpl>());

    /**
     * This param indicates a SimpleFeatureType that needs to be created in the DataStore.
     * <p>
     * Since MemoryData store is volatile there needs to be a way to specify what FeatureTypes
     * the DataStore has using the parameters.
     * </p>
     */
    public static final String MEMBER_PARAM="MEMBER_PARAM_KEY"; //$NON-NLS-1$

    private MemoryDSFactory factory;

    public MemoryServiceExtensionImpl() {
        super();
    }

    /**
     * This permits the use of a custom sub-class of an ActiveMemoryDataStore.
     * If this is null, this extension will use the default implementation.
     * @param factory providing custom datastore to be used or null
     */
	public MemoryServiceExtensionImpl( MemoryDSFactory factory ) {
        super();
        this.factory = factory;
    }

    public IService createService( URL id2, Map<String, Serializable> params ) {
        URL id = id2;
        if (params.containsKey(KEY)) {
            if (id == null) {
                id = (URL) params.get(KEY);
            }
            if (!impl.containsKey(id))
                impl.put(id, new MemoryServiceImpl(id, this.factory));
            MemoryServiceImpl service = impl.get(id);
            MemoryDataStore store = null;

            try {
                store = service.resolve(MemoryDataStore.class, null);
            } catch (IOException e) {
                // won't happen
                throw (RuntimeException) new RuntimeException(e.getLocalizedMessage())
                        .initCause(e);
            }
            for( Map.Entry<String, Serializable> entry : params.entrySet() ) {
                if (entry.getKey().equals(MEMBER_PARAM)) {
                    String[] members = ((String) entry.getValue()).split("_MEMBER_"); //$NON-NLS-1$
                    for( String member : members ) {

                        String[] spec = (member.split("_SPLIT_")); //$NON-NLS-1$
                        try {
                            store.getSchema(spec[0]);
                        } catch (Exception e) {
                            // schema does not exist create it
                            try {
                                store.createSchema(DataUtilities.createType(spec[0], spec[1]));
                            } catch (Exception e2) {
                                CatalogPlugin.log("Error creating type in datastore", e2); //$NON-NLS-1$
                            }
                        }
                    }
                }
            }

            return service;
        }
        return null;
    }


	public Map<String, Serializable> createParams(URL url) {
		 if( url != null && url.toExternalForm().startsWith( URL.toExternalForm())){
            Map<String,Serializable> map = new HashMap<String,Serializable>();
            map.put( KEY, url );
            return map;
        }
        return null;
	}

}
