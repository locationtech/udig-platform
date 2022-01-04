/** uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.teradata;

import static org.geotools.data.teradata.TeradataDataStoreFactory.PORT;
import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.geotools.jdbc.JDBCDataStore;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.service.database.TableDescriptor;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;

/**
 * A teradata service that represents the database. Its children are "folders" that each resolve to
 * a TeradataDatastore. Each folder has GeoResources
 *
 * @author jesse
 * @since 1.1.0
 */
public class TeradataService extends IService {

    private final URL id;

    private Map<String, Serializable> params;

    private Status status;

    private final List<TeradataGeoResource> members = new ArrayList<>();

    private Lock lock = new UDIGDisplaySafeLock();

    private Throwable message;

    private JDBCDataStore datastore;

    public TeradataService(URL finalID, Map<String, Serializable> map) {
        this.id = finalID;
        this.params = new HashMap<>(map);
        status = Status.NOTCONNECTED;
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

    @Override
    public TeradataServiceInfo getInfo(IProgressMonitor monitor) throws IOException {
        return (TeradataServiceInfo) super.getInfo(monitor);
    }

    @Override
    protected TeradataServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
        // make sure members are loaded cause they're needed for info
        members(monitor);
        return new TeradataServiceInfo(this);
    }

    @Override
    public List<TeradataGeoResource> resources(IProgressMonitor monitor) throws IOException {
        lock.lock();
        try {
            if (status != Status.CONNECTED) {
                Set<TableDescriptor> tables = lookupTablesInDB(
                        SubMonitor.convert(monitor, "looking up schemas", 1));
                message = null;
                status = Status.CONNECTED;
                if (tables == null) {
                    return Collections.unmodifiableList(members);
                }
                createGeoResources(tables);
            }
            return Collections.unmodifiableList(members);
        } finally {
            lock.unlock();
        }
    }

    private Set<TableDescriptor> lookupTablesInDB(IProgressMonitor monitor) {
        String host = (String) params.get(HOST.key);
        Integer port = (Integer) params.get(PORT.key);
        String database = (String) params.get(DATABASE.key);
        String user = (String) params.get(USER.key);
        String pass = (String) params.get(PASSWD.key);

        TeradataLookUpSchemaRunnable runnable = new TeradataLookUpSchemaRunnable(host, port, user,
                pass, database);
        runnable.run(monitor);

        if (runnable.getError() != null) {
            message = new Exception(runnable.getError());
            status = Status.BROKEN;
            return null;
        }
        Set<TableDescriptor> tables = runnable.getTableDescriptors();
        return tables;
    }

    private void createGeoResources(Set<TableDescriptor> tables) {

        for (TableDescriptor desc : tables) {
            String trimmedName = desc.name.trim();
            if (trimmedName.length() == 0) {
                continue;
            }

            try {
                members.add(new TeradataGeoResource(this, desc));
            } catch (Throwable e) {
                Activator.log("Error occurred while Georesource " + trimmedName
                        + " it is most likely simply a table that we cannot access or that is not spatially enabled.  Error message is: "
                        + e.getMessage(), null);
            }
        }
    }

    @Override
    public String getTitle() {
        URL id = getIdentifier();
        return ("Teradata " + id.getHost() + "/" + id.getPath()).replaceAll("//", "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    @Override
    public URL getIdentifier() {
        return id;
    }

    @Override
    public Throwable getMessage() {
        return message;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void dispose(IProgressMonitor monitor) {
        super.dispose(monitor);
        if (!members.isEmpty()) {
            members.clear();
        }
        if (datastore != null) {
            datastore.dispose();
            datastore = null;
        }
        status = Status.DISPOSED;
    }

    public synchronized JDBCDataStore getDataStore() throws IOException {
        if (datastore == null) {
            datastore = TeradataServiceExtension.getFactory().createDataStore(params);
        }
        return datastore;
    }

}
