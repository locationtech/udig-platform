/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package eu.udig.catalog.csw;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {
	public static final String ID = "eu.udig.catalog.csw"; //$NON-NLS-1$
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
