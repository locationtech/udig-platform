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
package net.refractions.udig.catalog.internal.worldimage;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Provides lifecycle management for the WorldImage plugin.
 * @author mleslie
 * @since 0.6.0
 */
public class WorldImagePlugin extends AbstractUIPlugin {
    private static WorldImagePlugin plugin;
    private ResourceBundle resourceBundle;

    /**
     * Construct <code>WorldImagePlugin</code>.
     *
     */
    public WorldImagePlugin() {
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

    /**
     * Retrieves the default instance of this class.
     *
     * @return Default instance of WorldImagePlugin.
     */
    public static WorldImagePlugin getDefault() {
        return plugin;
    }

    /**
     * Retrieves the string value of the requested resource.
     *
     * @param key
     * @return Value of the desired resource string.
     */
    public static String getResourceString(String key) {
        ResourceBundle bundle = WorldImagePlugin.getDefault().getResourceBundle();
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
                        "net.refractions.udig.catalog.worldimage.WorldImagePluginResources"); //$NON-NLS-1$
            }
        } catch(MissingResourceException ex) {
            this.resourceBundle = null;
        }
        return this.resourceBundle;
    }
}
