/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.catalog.jgrass;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import eu.udig.catalog.jgrass.activeregion.ActiveRegionMapGraphic;

/**
 * <p>
 * The activator class controls the plug-in life cycle
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class JGrassPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "eu.udig.catalog.jgrass";

    // The shared instance
    private static JGrassPlugin plugin;

    /**
     * The constructor
     */
    public JGrassPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
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
    public static JGrassPlugin getDefault() {
        return plugin;
    }

    /**
     * Logs the Throwable in the plugin's log.
     * <p>
     * This will be a user visable ERROR iff:
     * <ul>
     * <li>t is an Exception we are assuming it is human readable or if a message is provided
     */
    public static void log( String message2, Throwable t ) {
        if (getDefault() == null) {
            t.printStackTrace();
            return;
        }
        String message = message2;
        if (message == null)
            message = ""; //$NON-NLS-1$
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, PLUGIN_ID, IStatus.OK, message, t));
    }
    
    private ILayer activeRegionLayer;
    public ILayer getActiveRegionMapGraphic() {

        /*
         * load the right mapgraphic layer
         */
        try {
            List<IResolve> mapgraphics = CatalogPlugin.getDefault().getLocalCatalog().find(MapGraphicService.SERVICE_URL, null);
            List<IResolve> members = mapgraphics.get(0).members(null);
            for( IResolve resolve : members ) {
                if (resolve.canResolve(ActiveRegionMapGraphic.class)) {
                    IMap activeMap = ApplicationGIS.getActiveMap();
                    List<ILayer> layers = activeMap.getMapLayers();
                    boolean isAlreadyLoaded = false;
                    for( ILayer layer : layers ) {
                        if (layer.hasResource(ActiveRegionMapGraphic.class)) {
                            isAlreadyLoaded = true;
                            activeRegionLayer = layer;
                        }
                    }

                    if (!isAlreadyLoaded) {
                        List< ? extends ILayer> addedLayersToMap = ApplicationGIS.addLayersToMap(activeMap,
                                Collections.singletonList(resolve.resolve(IGeoResource.class, null)), layers.size());
                        for( ILayer l : addedLayersToMap ) {
                            IGeoResource geoResource = l.getGeoResource();
                            if (geoResource.canResolve(ActiveRegionMapGraphic.class)) {
                                activeRegionLayer = l;
                            }
                        }
                    }
                    break;
                }
            }
            return activeRegionLayer;
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }

    }

}
