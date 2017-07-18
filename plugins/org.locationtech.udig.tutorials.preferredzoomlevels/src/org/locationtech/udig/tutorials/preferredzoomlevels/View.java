/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.preferredzoomlevels;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IStatusLineManager;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.internal.DefaultMapViewPart;

/**
 * 
 * @version 1.3.0
 */
public class View extends DefaultMapViewPart {
	@SuppressWarnings("nls")
    public static final String ID = "org.locationtech.udig.tutorials.preferredzoomlevels.view";

	@Override
	protected void createResources(List<IGeoResource> resources, 
			IProgressMonitor monitor) throws IOException {
		
		@SuppressWarnings("nls")
        URL url = FileLocator.resolve(FileLocator.find(Activator.getDefault().getBundle(), new Path("data/face.shp"), new HashMap<String, String>()));
		IService r = CatalogPlugin.getDefault().getLocalCatalog().acquire(url, monitor);
		resources.add(r.resources(monitor).get(0));
	}
	
	@Override
	protected void initMap(Map createdMap) {
		TreeSet<Double> scales = new TreeSet<Double>();
		scales.add(1000.0);
		scales.add(10000.0);
		scales.add(100000.0);
		scales.add(1000000.0);
		scales.add(10000000.0);
		scales.add(100000000.0);
		scales.add(1000000000.0);
		createdMap.getViewportModelInternal().setPreferredScaleDenominators(scales);
		super.initMap(createdMap);
	}

	@Override
	protected boolean acquireToolbar() {
		return true;
	}
	@Override
	public IStatusLineManager getStatusLineManager() {
		return getViewSite().getActionBars().getStatusLineManager();
	}
	
}
