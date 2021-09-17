/* Image Georeferencing
 *
 * Axios Engineering
 *      http://www.axios.es
 *
 * (C) 2011, Axios Engineering S.L. (Axios)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.image.georeferencing;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @since 1.3.3
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String	PLUGIN_ID	= "org.locationtech.udig.image.georeferencing";	//$NON-NLS-1$

	// The shared instance
	private static Activator	plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
