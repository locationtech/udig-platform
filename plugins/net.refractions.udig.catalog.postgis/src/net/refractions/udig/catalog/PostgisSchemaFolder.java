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
package net.refractions.udig.catalog;

import static org.geotools.data.postgis.PostgisNGDataStoreFactory.SCHEMA;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.refractions.udig.catalog.internal.postgis.PostgisPlugin;
import net.refractions.udig.catalog.service.database.TableDescriptor;
import net.refractions.udig.core.internal.CorePlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.jdbc.JDBCDataStore;
/**
 * A Folder that represents a schema in a postgis folder. Its members are the tables and are
 * featurestores. This resolves to a PostgisDataStore
 * 
 * @author jesse
 * @since 1.1.0
 */
public class PostgisSchemaFolder implements IResolveFolder {

    private final String schema;
    private final ArrayList<IResolve> members;
    private final PostgisService2 service;
    private final URL identifier;
    private Exception trace;
	private HashMap<String, Serializable> params;
	private JDBCDataStore datastore;

    public PostgisSchemaFolder( PostgisService2 service, String schema, Collection<TableDescriptor> descriptors) {
        this.trace = new Exception("Creating folder"); //$NON-NLS-1$
        this.service = service;
        this.schema = schema;
        
        try {
            URL identifier2 = service.getIdentifier();
            identifier = new URL(identifier2, identifier2.toExternalForm() + "#" + schema, CorePlugin.RELAXED_HANDLER); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The service URL must not contain a #", e); //$NON-NLS-1$
        }
        
        HashMap<String, Serializable> params = new HashMap<String, Serializable>(service
                .getConnectionParams());
        params.put(SCHEMA.key, schema);
        this.params = params;
        members = new ArrayList<IResolve>();

        for (TableDescriptor tableDescriptor : descriptors) {
            PostgisGeoResource2 resource2 = new PostgisGeoResource2(service, this, tableDescriptor);
            members.add(resource2);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        if( trace!=null ){
            // uh oh not disposed!
            PostgisPlugin.log("PostgisSchemaFolder was not disposed!", trace); //$NON-NLS-1$
            dispose(new NullProgressMonitor());
        }
        super.finalize();
    }

    public ImageDescriptor getIcon( IProgressMonitor monitor ) {
        return null;
    }

    public String getTitle() {
        return schema;
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        boolean isResolveFolder = adaptee.isAssignableFrom(IResolveFolder.class);
        boolean isIService = adaptee.isAssignableFrom(IService.class);
        boolean isConnection = adaptee.isAssignableFrom(Connection.class);
        boolean isDataStore = adaptee.isAssignableFrom(JDBCDataStore.class);
        IResolveManager resolveManager = CatalogPlugin.getDefault().getResolveManager();
        return isResolveFolder || isIService || isConnection || isDataStore
                || resolveManager.canResolve(this, adaptee);
    }

    public void dispose( IProgressMonitor monitor ) {
        trace = null;
        if(datastore!=null) {
        	datastore.dispose();
        }
    }

    public URL getIdentifier() {
        return identifier;
    }
    public ID getID() {
        return new ID( getIdentifier() );        
    }
    public Throwable getMessage() {
        return null;
    }

    public Status getStatus() {
        return Status.CONNECTED;
    }

    public List<IResolve> members( IProgressMonitor monitor ) throws IOException {
        return Collections.unmodifiableList(members);
    }

    public IResolve parent( IProgressMonitor monitor ) throws IOException {
        return service;
    }

    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;
        if (adaptee.isAssignableFrom(IResolveFolder.class))
            return adaptee.cast(this);
        if (adaptee.isAssignableFrom(IService.class))
            return adaptee.cast(service);
        if (adaptee.isAssignableFrom(JDBCDataStore.class)) {
                return adaptee.cast(getDataStore());
        }
        if (adaptee.isAssignableFrom(Connection.class)){
            return service.resolve(adaptee, monitor);
        }
        
        return CatalogPlugin.getDefault().getResolveManager().resolve(this, adaptee, monitor);
    }

    public JDBCDataStore getDataStore() throws IOException {
    	if(datastore == null) {
            datastore = PostgisServiceExtension2.getFactory().createDataStore(params);
    	}
        return datastore;
    }

    public String getSchemaName() {
        return schema;
    }

    public IService getService( IProgressMonitor monitor ) {
        return service;
    }

}