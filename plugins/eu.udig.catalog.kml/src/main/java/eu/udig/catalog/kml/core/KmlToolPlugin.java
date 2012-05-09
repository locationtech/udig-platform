/*
 * (C) HydroloGIS - www.hydrologis.com 
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.catalog.kml.core;

import net.refractions.udig.core.AbstractUdigUIPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @author Frank Gasdorf
 */
public class KmlToolPlugin extends AbstractUdigUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "eu.udig.tools.kml"; //$NON-NLS-1$

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
