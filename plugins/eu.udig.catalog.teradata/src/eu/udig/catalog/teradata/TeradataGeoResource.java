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
package eu.udig.catalog.teradata;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.service.database.TableDescriptor;
import net.refractions.udig.core.internal.CorePlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.jdbc.JDBCDataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * A GeoResource implementation for {@link TeradataSchemaFolder}. Resolves to a
 * Featurestore
 * 
 * @author Jesse Eichar, Refractions Research
 * @since 1,2
 */
public class TeradataGeoResource extends IGeoResource {
	final String typename;
	private volatile Status status;
	private volatile Throwable message;
	private final URL identifier;
	private Boolean readOnly = null; // we won't know until we try
	private TeradataService parent;
	final TableDescriptor desc;

	public TeradataGeoResource(TeradataService service, TableDescriptor desc) {
		this.service = this.parent = service;
		this.desc = desc;
		this.typename = desc.name;
		try {
			URL identifier2 = service.getIdentifier();
			identifier = new URL(identifier2, identifier2.toExternalForm()
					+ "#" + typename, CorePlugin.RELAXED_HANDLER);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(
					"The service URL must not contain a #", e);
		}
	}

	public URL getIdentifier() {
		return identifier;
	}

	@Override
	public IResolve parent(IProgressMonitor monitor) throws IOException {
		return service;
	}

	/*
	 * @see net.refractions.udig.catalog.IGeoResource#getStatus()
	 */
	public Status getStatus() {
		if (status != null)
			return status;
		return service.getStatus();
	}

	/*
	 * @see net.refractions.udig.catalog.IGeoResource#getStatusMessage()
	 */
	public Throwable getMessage() {
		if (message != null)
			return message;
		return service.getMessage();
	}

	/*
	 * Required adaptions: <ul> <li>IGeoResourceInfo.class <li>IService.class
	 * </ul>
	 * 
	 * @see net.refractions.udig.catalog.IResolve#resolve(java.lang.Class,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor)
			throws IOException {
		if (adaptee == null)
			return null;

		if (adaptee.isAssignableFrom(IGeoResourceInfo.class)) {
			return adaptee.cast(createInfo(monitor));
		}
		if (adaptee.isAssignableFrom(IGeoResource.class)) {
			return adaptee.cast(this);
		}

		JDBCDataStore dataStore = parent.getDataStore();
		if (adaptee.isAssignableFrom(DataStore.class)) {
			return adaptee.cast(dataStore);
		}
		if (adaptee.isAssignableFrom(FeatureStore.class)) {
			FeatureSource<SimpleFeatureType, SimpleFeature> fs = dataStore
					.getFeatureSource(typename);
			if (fs instanceof FeatureStore) {
				readOnly = false;
				return adaptee.cast(fs);
			} else {
				readOnly = true;
			}
			if (adaptee.isAssignableFrom(FeatureSource.class)) {
				return adaptee.cast(dataStore.getFeatureSource(typename));
			}
		}
		if (adaptee.isAssignableFrom(Connection.class)) {
			return service.resolve(adaptee, monitor);
		}

		return super.resolve(adaptee, monitor);
	}

	/*
	 * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
	 */
	public <T> boolean canResolve(Class<T> adaptee) {
		if (adaptee == null) {
			return false;
		}
		boolean isFeatureStore = adaptee.isAssignableFrom(FeatureStore.class)
				&& (readOnly == null || readOnly == false);
		boolean isFeatureSource = adaptee.isAssignableFrom(FeatureSource.class);
		boolean isGeoResource = adaptee
				.isAssignableFrom(IGeoResourceInfo.class);
		boolean isIService = adaptee.isAssignableFrom(IService.class);
		boolean isConnection = adaptee.isAssignableFrom(Connection.class);
		return (isGeoResource || isFeatureStore || isFeatureSource || isIService)
				|| isConnection || super.canResolve(adaptee);
	}

	@Override
	protected IGeoResourceInfo createInfo(IProgressMonitor monitor)
			throws IOException {
		try {
			return new TeradataResourceInfo(this);
		} catch (Exception e) {
			Activator.log("Error creating a TeradataInfo object", e);
			return INFO_UNAVAILABLE;
		}
	}

	/**
	 * returns the schema of the GeoResource
	 * 
	 * @return the schema of the GeoResource
	 * @throws IOException
	 */
	public SimpleFeatureType getSchema() throws IOException {
		return parent.getDataStore().getSchema(typename);
	}

	/**
	 * Sets the status and error message of this resource.
	 * 
	 * @param status
	 *            the new status. Cannot be null.
	 * @param message
	 *            the new message. May be null.
	 */
	public void setStatus(Status status, Throwable message) {
		this.status = status;
		this.message = message;
	}

}