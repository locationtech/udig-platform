/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.toolview;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.geotools.util.URLs;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.mapgraphic.internal.MapGraphicService;
import org.locationtech.udig.project.ui.internal.DefaultMapViewPart;

/**
 * The main view port.  Adds a shapefile to the View and configures the view with the tools and context menu 
 * (when selection tool is active)
 * @version 1.3.0
 */
public class View extends DefaultMapViewPart {
	public static final String ID = "X.view";


	@Override
	protected void createResources(List<IGeoResource> resources, IProgressMonitor monitor) throws IOException {
		addAlertsMapgraphic(monitor, resources);

		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dialog.setFilterExtensions(new String[]{"*.shp"});
		String path = dialog.open();
		File file = new File( path );
		URL url = URLs.fileToUrl(file);
		
		addShpService(url,resources,monitor);
	}
	private void addAlertsMapgraphic(IProgressMonitor monitor,
			List<IGeoResource> resources) throws IOException {
		IService service = CatalogPlugin.getDefault().getLocalCatalog().acquire(MapGraphicService.SERVICE_URL,monitor);
		String desiredIdString = MapGraphicService.SERVICE_URL+"#"+ShowAlertsMapGraphic.EXTENSION_ID;
		for (IGeoResource resource : service.resources(null)) {
			String idString = resource.getID().toString();
			if(idString.equals(desiredIdString)) {
				resources.add(resource);
				return;
			}
		}
		throw new IllegalStateException("Unable to find " + desiredIdString + " mapgraphic");
	}
	
	private void addShpService(URL url,List<IGeoResource> resources, IProgressMonitor monitor) throws IOException {
		IService service = CatalogPlugin.getDefault().getLocalCatalog().acquire(url,monitor);
		
		resources.addAll(service.resources(monitor));
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
