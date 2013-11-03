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
package org.locationtech.udig.catalog.internal.worldimage;

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
    
    public static final String PROTOCOL_FILE = "file"; //$NON-NLS-1$
    
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
                        "org.locationtech.udig.catalog.worldimage.WorldImagePluginResources"); //$NON-NLS-1$
            }
        } catch(MissingResourceException ex) {
            this.resourceBundle = null;
        }
        return this.resourceBundle;
    } 
}
