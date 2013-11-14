/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.arcgrid;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ArcGridPlugin extends AbstractUIPlugin {
	private static ArcGridPlugin plugin;
	private ResourceBundle resourceBundle;
	
	public ArcGridPlugin() {
		super();
		plugin = this;
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
	
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		this.resourceBundle = null;
		super.stop(context);
	}
	
	public static ArcGridPlugin getDefault() {
		return plugin;
	}
	
	public static String getResourceString(String key) {
		ResourceBundle bundle = ArcGridPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}
	
	public ResourceBundle getResourceBundle() {
		try {
			if (this.resourceBundle == null)
				this.resourceBundle = ResourceBundle.getBundle(
                        "org.locationtech.udig.arcgrid.internal.arcgrid.ArcGridPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
		return this.resourceBundle;
	}
}
