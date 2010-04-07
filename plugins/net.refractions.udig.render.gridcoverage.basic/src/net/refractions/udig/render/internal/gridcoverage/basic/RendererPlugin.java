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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Plugin class for BasicGridCoverage
 * @author jones
 * @since 0.6.0
 */
public class RendererPlugin extends Plugin {
    public static final String ID = "net.refractions.udig.render.gridcoverage.basic"; //$NON-NLS-1$
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
        getDefault().getLog().log(new Status(IStatus.ERROR, ID, 
                0, e.getLocalizedMessage(), e));
    }
    
    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log( String message, Throwable e) {
        getDefault().getLog().log(new Status(IStatus.INFO, ID, 0, message, e));
    }
    
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
	
	/**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin("net.refractions.udig.render.gridcoverage.basic", path); //$NON-NLS-1$
    }
    
}
