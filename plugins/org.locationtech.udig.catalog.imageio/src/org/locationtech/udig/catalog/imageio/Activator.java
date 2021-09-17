/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.imageio;

import it.geosolutions.imageio.gdalframework.GDALUtilities;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Provides lifecycle management for the ImageIOPlugin plugin.
 *
 * @author mleslie
 * @author Daniele Romagnoli, GeoSolutions
 * @author Jody Garnett
 * @author Simone Giannecchini, GeoSolutions
 *
 * @since 0.6.0
 */
public class Activator extends AbstractUIPlugin {
    private static Activator plugin;
    public static final String ID = "org.locationtech.udig.catalog.imageio"; //$NON-NLS-1$
    private ResourceBundle resourceBundle;

    /**
     * Construct <code>ImageIOPlugin</code>.
     */
    public Activator() {
        super();
        plugin = this;
    }

    public void start( BundleContext context ) throws Exception {
        super.start(context);
        if (!GDALUtilities.isGDALAvailable()) {
            StringBuilder b = new StringBuilder();
            b.append("\tLD_LIBRARY_PATH=" + absoluteFile("LD_LIBRARY_PATH") + "\n");
            b.append("\tDYLD_LIBRARY_PATH=" + absoluteFile("DYLD_LIBRARY_PATH") + "\n");
            b.append("\tGDAL_DATA=" + absoluteFile("GDAL_DATA") + "\n");
            b.append("\tjava.libary.path=" + System.getProperty("java.library.path") + "\n");

            // we perform a check here to check if gdal is actually around
            // if we fail then the plugin contributions would smoothly
            // not be applied.
            Platform.getLog(context.getBundle()).log(
                    new Status(IStatus.WARNING, Activator.ID,
                            "GDAL Not Available ... some image formats disabled: \nExpected environment variable resolve to:\n"
                                    + b));
        }
    }

	private String absoluteFile(String prop) {
		String getenv = System.getenv(prop);
		if(getenv!=null){
			return new java.io.File(getenv).getAbsolutePath();
		} else {
			return null;
		}
	}

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        this.resourceBundle = null;
        super.stop(context);
    }

    /**
     * Retrieves the default instance of this class.
     *
     * @return Default instance of ImageIOPlugin.
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Retrieves the string value of the requested resource.
     *
     * @param key
     * @return Value of the desired resource string.
     */
    public static String getResourceString(String key) {
        ResourceBundle bundle = Activator.getDefault().getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException ex) {
            return key;
        }
    }

    /**
     * Retrieves the default resource bundle for this plugin.
     *
     * @return ResourceBundle
     */
    public ResourceBundle getResourceBundle() {
        try {
            if(this.resourceBundle == null) {
                this.resourceBundle = ResourceBundle.getBundle(
                        "org.locationtech.udig.catalog.imageio.MrSIDPluginResources"); //$NON-NLS-1$
            }
        } catch(MissingResourceException ex) {
            this.resourceBundle = null;
        }
        return this.resourceBundle;
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log( String message, Throwable e ) {
        if (message == null){
            message = ""; //$NON-NLS-1$
        }
        getDefault().getLog().log(new Status(IStatus.INFO, ID, 0, message, e));
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * You can turn that on using -debug on the command line,
     * you should get more specific with a .options file
     * but life is short right now.
     */
    private static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null){
                System.out.println(message+"\n"); //$NON-NLS-1$
            }
            if (e != null){
                e.printStackTrace(System.out);
            }
        }
    }
}
