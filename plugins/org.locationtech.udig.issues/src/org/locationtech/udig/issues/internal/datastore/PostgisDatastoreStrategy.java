/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal.datastore;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.locationtech.udig.catalog.PostgisServiceExtension2;
import org.locationtech.udig.issues.IListStrategy;

import org.geotools.data.DataStore;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;

/**
 * Strategy for obtaining a postgis datastore.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class PostgisDatastoreStrategy extends AbstractDatastoreStrategy implements IListStrategy {
    
    private static final String ID = "org.locationtech.udig.issues.issuesList"; //$NON-NLS-1$
    private DataStore datastore;
   

    protected synchronized DataStore getDataStore() throws IOException {
        if( datastore!=null && tested ){
            return datastore;
        }
        featureStore=null;
        datastore=null;
        PostgisServiceExtension2 ext=new PostgisServiceExtension2();
        Map<String, Serializable> params = ext.createParams(PostgisServiceExtension2.DIALECT.toURL(url));
        PostgisNGDataStoreFactory factory = new PostgisNGDataStoreFactory();
        
        datastore=factory.createDataStore(params);
        return datastore;
    }

    public String getExtensionID() {
        return ID;
    }

}
