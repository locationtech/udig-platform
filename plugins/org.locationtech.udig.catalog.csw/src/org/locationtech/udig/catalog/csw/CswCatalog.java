/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.csw;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.locationtech.udig.catalog.ICatalogInfo;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.ISearch;

import org.eclipse.core.runtime.IProgressMonitor;

import org.locationtech.jts.geom.Envelope;

public class CswCatalog extends ISearch {

	private URL url;
	private Throwable error = null;
	
	public CswCatalog(URL url) {
		this.url = url;
	}

	@Override
	public <T> boolean canResolve(Class<T> adaptee) {
		return adaptee!=null && (adaptee.isAssignableFrom(ICatalogInfo.class) || adaptee.isAssignableFrom(List.class));
	}

	@Override
	public Status getStatus() {
		return error == null?Status.CONNECTED:Status.BROKEN;
	}

	@Override
	public Throwable getMessage() {
		return error;
	}

	@Override
	public URL getIdentifier() {
		return url;
	}

	@Override
	public ID getID() {
		return new ID(url);
	}

	@Override
	public String getTitle() {
		return url.getHost();
	}

	@Override
	public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
        if(adaptee == null)
            return null;
        if(adaptee.isAssignableFrom(ICatalogInfo.class)){
            return adaptee.cast(getInfo(monitor));
        }
        if(adaptee.isAssignableFrom(List.class)){
            return adaptee.cast(members(monitor));
        }
        return null;
	}

	@Override
	public List<IResolve> find(ID resourceId, IProgressMonitor monitor) {
		return new LinkedList<IResolve>();
	}

	@Override
	public <T extends IResolve> T getById(Class<T> type, ID id,
			IProgressMonitor monitor) {
		return null;
	}

	@Override
	public List<IResolve> search(String pattern, Envelope bbox,
			IProgressMonitor monitor) throws IOException {
	    return Collections.emptyList();
	    /*
		OgcFilter filter = new PropertyIsEqualTo("Type", "dataset");
		PropertyIsEqualTo textSearch = new PropertyIsEqualTo("AnyText", pattern);
		if(!pattern.trim().isEmpty()) {
			filter = new And(filter, textSearch);
		}
		if(bbox != null) {
			BBoxFilter bboxFilter = new BBoxFilter(bbox.getMinX(), bbox.getMinY(), bbox.getMaxX(), bbox.getMaxY());
			filter = new And(filter, bboxFilter);
		}
		int maxRecords = 10;
		GetRecordsParams params = new GetRecordsParams(filter,maxRecords);
		GetDublinCoreRecords request = new GetDublinCoreRecords(params);
		Context context = new Context(url.toExternalForm());
		List<DublinCore> results = request.execute(context).java();
		List<IResolve> resolves = new ArrayList<IResolve>();
		for (DublinCore dublinCore : results) {
			if(dublinCore.type() == dublinCore.WMS_TYPE() && !dublinCore.onlineResource().isEmpty()) {
				URL url = new URL(dublinCore.onlineResource().get());
				OGCLayer layer = new OGCLayer(dublinCore.title(), dublinCore.title(), 
						dublinCore.description(), 
						url, 
						dublinCore.type(), 
						url);
				resolves.add(CswResource.getResource(layer));
			}
		}
		return resolves;
		*/
	}

}
