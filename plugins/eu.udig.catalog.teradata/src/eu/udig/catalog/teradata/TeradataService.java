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
package eu.udig.catalog.teradata;

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

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.service.database.TableDescriptor;
import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.geotools.jdbc.JDBCDataStore;

/**
 * A teradata service that represents the database. Its children are "folders"
 * that each resolve to a TeradataDatastore. Each folder has georesources
 * 
 * @author jesse
 * @since 1.1.0
 */
public class TeradataService extends IService {

	private final URL id;
	private Map<String, Serializable> params;
	private Status status;
	private final List<TeradataGeoResource> members = new ArrayList<TeradataGeoResource>();
	private Lock lock = new UDIGDisplaySafeLock();
	private Throwable message;
	private JDBCDataStore datastore;

	public TeradataService(URL finalID, Map<String, Serializable> map) {
		this.id = finalID;
		this.params = new HashMap<String, Serializable>(map);
		status = Status.NOTCONNECTED;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		// clean up connection
		dispose(new NullProgressMonitor());
	}

	@Override
	public Map<String, Serializable> getConnectionParams() {
		return params;
	}

	@Override
	public TeradataServiceInfo getInfo(IProgressMonitor monitor)
			throws IOException {
		return (TeradataServiceInfo) super.getInfo(monitor);
	}

	protected TeradataServiceInfo createInfo(IProgressMonitor monitor)
			throws IOException {
		// make sure members are loaded cause they're needed for info
		members(monitor);
		return new TeradataServiceInfo(this);
	}

	@Override
	public List<TeradataGeoResource> resources(IProgressMonitor monitor)
			throws IOException {
		lock.lock();
		try {
			if (status != Status.CONNECTED) {
				Set<TableDescriptor> tables = lookupTablesInDB(SubMonitor.convert(monitor,
						"looking up schemas", 1));
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

		TeradataLookUpSchemaRunnable runnable = new TeradataLookUpSchemaRunnable(
				host, port, user, pass, database);
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
				Activator.log("Error occurred while Georesource "+trimmedName+" it is most likely simply a table that we cannot access or that is not spatially enabled.  Error message is: "+e.getMessage(),null);
			}
		}
	}

	@Override
    public String getTitle() {
        URL id = getIdentifier();
		return ("Teradata " +id.getHost()+ "/" +id.getPath()).replaceAll("//","/"); //$NON-NLS-1$
    }
	
	public URL getIdentifier() {
		return id;
	}

	public Throwable getMessage() {
		return message;
	}

	public Status getStatus() {
		return status;
	}

	@Override
	public void dispose(IProgressMonitor monitor) {
		for (IResolve folder : members) {
			folder.dispose(monitor);
		}
	}

	public synchronized JDBCDataStore getDataStore() throws IOException {
		if (datastore == null) {
			datastore = TeradataServiceExtension.getFactory().createDataStore(params);
		}
		return datastore;
	}

}
