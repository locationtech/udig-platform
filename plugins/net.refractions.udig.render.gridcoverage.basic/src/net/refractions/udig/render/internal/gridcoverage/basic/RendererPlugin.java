/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.render.internal.gridcoverage.basic;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * Plugin class for BasicGridCoverage
 * @author jones
 * @since 0.6.0
 */
public class RendererPlugin extends Plugin {

    private static RendererPlugin plugin;

    /**
     * Construct <code>RendererPlugin</code>.
     *
     */
    public RendererPlugin() {
        super();
        plugin=this;
    }

    public static Plugin getDefault(){
        return plugin;
    }

    public static void log(Exception e){
        getDefault().getLog().log(new Status(IStatus.ERROR, "net.refractions.udig.render.gridcoverage.basic", //$NON-NLS-1$
                0, e.getLocalizedMessage(), e));
    }
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
}
