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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {
	public static final String ID = "org.locationtech.udig.catalog.csw"; //$NON-NLS-1$
	private static Activator plugin;

	public static Activator getDefault() {
		return plugin;
	}
	public Activator() {
		plugin = this;
	}
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		super.stop(bundleContext);
	}
	
    public static void log( String message, Throwable t ) {
        String msg = message == null ? "" : message;
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, ID, IStatus.OK, msg, t));
    }


}
