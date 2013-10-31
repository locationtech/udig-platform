/*
 * (C) HydroloGIS - www.hydrologis.com 
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.kml.core;

import org.locationtech.udig.core.AbstractUdigUIPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 * @author Frank Gasdorf
 */
public class KmlToolPlugin extends AbstractUdigUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.locationtech.udig.catalog.kml"; //$NON-NLS-1$

    // The shared instance
    private static KmlToolPlugin plugin;

    /**
     * The constructor
     */
    public KmlToolPlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static KmlToolPlugin getDefault() {
        return plugin;
    }

    @Override
    public IPath getIconPath() {
        return new Path(DEFAULT_ICON_PATH);
    }

}
