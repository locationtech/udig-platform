package org.locationtech.udig.style.advanced.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class AdvancedStylePlugin implements BundleActivator {

	public static final String PLUGIN_ID = "org.locationtech.udig.style.advanced.core"; // //$NON-NLS-1$
	
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		AdvancedStylePlugin.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		AdvancedStylePlugin.context = null;
	}

}
