/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wfs;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class WfsPlugin extends AbstractUIPlugin {
    // The shared instance.
    private static WfsPlugin plugin = new WfsPlugin();

    private ResourceBundle resourceBundle;

    public static final String ID = "org.locationtech.udig.catalog.internal.wfs"; //$NON-NLS-1$

    /**
     * The constructor.
     */
    private WfsPlugin() {
        super();
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        resourceBundle = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     */
    public static WfsPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not found.
     */
    public static String getResourceString(String key) {
        ResourceBundle bundle = WfsPlugin.getDefault().getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        try {
            if (resourceBundle == null)
                resourceBundle = ResourceBundle
                        .getBundle("org.locationtech.udig.catalog.internal.wfs.WfsPluginResources"); //$NON-NLS-1$
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
        return resourceBundle;
    }

    /**
     * Logs the Throwable in the plugin's log.
     * <p>
     * This will be a user visible ERROR iff:
     * <ul>
     * <li>t is an Exception we are assuming it is human readable or if a message is provided
     * </ul>
     * </p>
     *
     * @param message
     * @param t
     */
    public static void log(String message, Throwable t) {
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, ID, IStatus.OK, message, t));
    }

    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much preferred to do this:
     *
     * <pre>
     * <code>
     * private static final String RENDERING = "org.locationtech.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * }
     * </code>
     * </pre>
     * </p>
     *
     * @param message
     * @param e
     */
    public static void trace(String message, Throwable e) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
    }

    /**
     * Performs the Platform.getDebugOption true check on the provided trace
     * <p>
     * Note: ProjectUIPlugin.getDefault().isDebugging() must also be on.
     * <ul>
     * <li>Trace.RENDER - trace rendering progress
     * </ul>
     * </p>
     *
     * @param trace currently only RENDER is defined
     * @return true if -debug is on for this plugin
     */
    public static boolean isDebugging(final String trace) {
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$
    }
}
