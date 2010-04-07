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
package net.crischan.udig.arcgrid;

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
                        "net.crischan.udig.arcgrid.internal.arcgrid.ArcGridPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
		return this.resourceBundle;
	}
}
