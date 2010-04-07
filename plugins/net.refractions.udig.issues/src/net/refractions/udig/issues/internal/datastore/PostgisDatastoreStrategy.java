/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.issues.internal.datastore;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import net.refractions.udig.catalog.PostgisServiceExtension2;
import net.refractions.udig.issues.IListStrategy;

import org.geotools.data.DataStore;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;

/**
 * Strategy for obtaining a postgis datastore.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class PostgisDatastoreStrategy extends AbstractDatastoreStrategy implements IListStrategy {
    
    private static final String ID = "net.refractions.udig.issues.issuesList"; //$NON-NLS-1$
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
