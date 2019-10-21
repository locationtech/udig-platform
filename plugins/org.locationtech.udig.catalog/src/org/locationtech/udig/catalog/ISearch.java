/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.locationtech.jts.geom.Envelope;

/**
 * Provide a searchable catalog of spatial data sources.
 * <p>
 * The specific metadata to be searched is arbitrary; we are providing a limited
 * search based on plain text and an extent.
 * 
 * @author Jody Garnett (Lisasoft)
 * 
 * @since 1.2.0
 * @see ICatalog
 * @see IRepository
 */
public abstract class ISearch implements IResolve {
	/**
	 * ISearch does not have a parent so null is returned.
	 * 
	 * @return null as catalogs do not have a parent
	 */
	public IResolve parent(IProgressMonitor monitor) {
		return null;
	}

	/**
	 * Will attempt to morph into the adaptee, and return that object. Required
	 * adaptions:
	 * <ul>
	 * <li>ICatalogInfo.class
	 * </ul>
	 * This operation may block.
	 * <p>
	 * You can use this method to make specific registry access APIs availble;
	 * perhaps based on the WRS or CAT service?
	 * 
	 * @param adaptee
	 * @param monitor
	 *            Used to report progress when connecting
	 * @return instance of adaptee, or null if not available
	 * @see ICatalogInfo
	 * @see IService
	 */
	public abstract <T> T resolve(Class<T> adaptee, IProgressMonitor monitor)
			throws IOException;

	/**
	 * Aquire info on this Catalog.
	 * <p>
	 * This is functionally equivalent to:
	 * <core>resolve(ICatalogInfo.class,monitor)</code>
	 * </p>
	 * 
	 * @see ISearch#resolve(Class, IProgressMonitor)
	 * @return ICatalogInfo resolve(ICatalogInfo.class,IProgressMonitor
	 *         monitor);
	 */
	public ICatalogInfo getInfo(IProgressMonitor monitor) throws IOException {
		return resolve(ICatalogInfo.class, monitor);
	}

	/**
	 * Find resources for this resourceId from the Catalog.
	 * <p>
	 * Please note that a List of resources Services, Resources, and friends are
	 * returned by this method. While the first result returned may be the most
	 * appropriate; you may need to try some of the other values (if for example
	 * the first service is unavailable).
	 * <p>
	 * 
	 * @param resource
	 *            used to match resolves
	 * @param monitor
	 *            used to show the progress of the find.
	 * @return List (possibly empty) of matching Resolves
	 */
	public abstract List<IResolve> find(ID resourceId, IProgressMonitor monitor);

	/**
	 * Check for an exact match with provided id.
	 * 
	 * @param type
	 *            Type of IResolve if known
	 * @param id
	 *            id used for lookup
	 * @param monitor
	 * @return Resolve or null if not found
	 */
	public abstract <T extends IResolve> T getById(Class<T> type, ID id, IProgressMonitor monitor);
    
	/**
	 * Performs a search on this catalog based on the specified inputs.
	 * <p>
	 * The pattern uses the following conventions:
	 * <ul>
	 * <li>
	 * <li> use " " to surround a phase
	 * <li> use + to represent 'AND'
	 * <li> use - to represent 'OR'
	 * <li> use ! to represent 'NOT'
	 * <li> use ( ) to designate scope
	 * </ul>
	 * The bbox provided shall be in Lat - Long, or null if the search is not to
	 * be contained within a specified area.
	 * </p>
	 * 
	 * @param pattern
	 *            Search pattern (see above)
	 * @param bbox
	 *            The bbox in Lat-Long (ESPG 4269), or null
	 * @param monitor
	 *            for progress, or null if monitoring is not desired
	 * @return List matching IResolve
	 */
	public abstract List<IResolve> search(String pattern, Envelope bbox,
			IProgressMonitor monitor) throws IOException;

	/**
	 * Indicate class and id.
	 * 
	 * @return string representing this IResolve
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		String classname = getClass().getName();
		String name = classname.substring(classname.lastIndexOf('.') + 1);
		buf.append(name);
		buf.append("("); //$NON-NLS-1$
		buf.append(getIdentifier());
		buf.append(")"); //$NON-NLS-1$
		return buf.toString();
	}
	/**
	 * An empty list is provided by default; ISerach contents are considered
	 * too vast (and too remote) to iterate through.
	 */
	public List<IResolve> members(IProgressMonitor monitor) throws IOException {
		return Collections.emptyList();
	}
	
	/**
	 * Clean up any resources or connections used to support
	 * the operation of the ISearch.
	 */
    public void dispose(IProgressMonitor monitor) {    
    }
}
