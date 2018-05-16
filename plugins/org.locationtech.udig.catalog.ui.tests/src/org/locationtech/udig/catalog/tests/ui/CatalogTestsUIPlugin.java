/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class CatalogTestsUIPlugin extends AbstractUIPlugin {

    public static final String WMSTestCapabilitiesURL = "https://demo.geo-solutions.it/geoserver/ows?service=wms&version=1.1.1&request=GetCapabilities"; //$NON-NLS-1$

    public static final String WFSTestCapabilitiesURL = "https://demo.geo-solutions.it/geoserver/ows?service=wfs&version=1.0.0&request=GetCapabilities"; //$NON-NLS-1$
	
	//The shared instance.
	private static CatalogTestsUIPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public CatalogTestsUIPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static CatalogTestsUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("foo", path); //$NON-NLS-1$
	}
}
