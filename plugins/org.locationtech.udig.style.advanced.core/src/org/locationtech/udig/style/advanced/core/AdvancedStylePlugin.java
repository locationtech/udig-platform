/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class AdvancedStylePlugin implements BundleActivator {

	public static final String PLUGIN_ID = "org.locationtech.udig.style.advanced.core"; // //$NON-NLS-1$

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		AdvancedStylePlugin.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		AdvancedStylePlugin.context = null;
	}

}
