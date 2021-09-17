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
package org.locationtech.udig.catalog.internal.mysql;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.jdbc.JDBCDataStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySQLPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.locationtech.udig.catalog.mysql"; //$NON-NLS-1$

    // The shared instance
    private static MySQLPlugin plugin;

    private ResourceBundle resourceBundle;

    private static final Set<JDBCDataStore> dsList = new CopyOnWriteArraySet<JDBCDataStore>();

    /**
     * The constructor
     */
    public MySQLPlugin() {
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        resourceBundle = null;
        /*
        //close datastore connections
        try {
            Iterator<MySQLDataStore> it = dsList.iterator();
            while (it.hasNext()) {
                MySQLDataStore ds = it.next();
                ds.getConnectionPool().close();
            }
        } catch (Exception e) {
            log("failed to close MySQL ConnectionPool", e); //$NON-NLS-1$
        }
        */
        super.stop(context);
    }

    /**
     * Returns the plugin's resource bundle,
     * @return x
     */
    public ResourceBundle getResourceBundle() {
        try {
            if (resourceBundle == null)
                resourceBundle = ResourceBundle.getBundle("org.locationtech.udig.catalog.internal.mysql.MySQLPluginResources"); //$NON-NLS-1$
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
        return resourceBundle;
    }


    /**
     *
     * Logs the Throwable in the plugin's log.
     * <p>
     * This will be a user visible ERROR iff:
     * <ul>
     * <li>t is an Exception we are assuming it is human readable or if a message is provided
     * </ul>
     * </p>
     * @param message
     * @param t
     */
    public static void log( String message, Throwable t ) {
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, PLUGIN_ID, IStatus.OK, message, t));
    }

    public static void addDataStore(JDBCDataStore ds) {
        dsList.add(ds);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static MySQLPlugin getDefault() {
        return plugin;
    }

}
