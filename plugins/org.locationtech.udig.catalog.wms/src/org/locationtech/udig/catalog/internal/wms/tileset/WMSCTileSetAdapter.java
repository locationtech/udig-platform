/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wms.tileset;

import java.io.IOException;

import javax.management.ServiceNotFoundException;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveAdapterFactory;
import org.locationtech.udig.catalog.internal.wms.WmsPlugin;
import org.locationtech.udig.catalog.wmsc.WMSCTileUtils;
import org.locationtech.udig.catalog.wmsc.server.TileSet;
import org.locationtech.udig.catalog.wmsc.server.TiledWebMapServer;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.ows.AbstractOpenWebService;
import org.geotools.ows.wms.WebMapServer;

/**
 * Allow any WebMapServer to supply a TileSet definition under user control (allowing it to
 * behave as a tile server with the results cached to disk).
 * <p>
 * This IResolveAdaptorFactory is controlled via {@link IGeoResource#getPersistentProperties()}
 * using the {@link PreferenceConstants#P_TILESET_ON_OFF} to control TileSet generation.
 * 
 * @see TileSetPropertyPage
 * @author jhudson
 * @since 1.3.0
 */
public class WMSCTileSetAdapter implements IResolveAdapterFactory {

    @Override
    public boolean canAdapt( IResolve resolve, Class<?> adapter ) {

        if (adapter.isAssignableFrom(TileSet.class)) {

            IGeoResource resource = (IGeoResource) resolve;

            Boolean enabled = (Boolean) resource.getPersistentProperties()
                    .get(PreferenceConstants.P_TILESET_ON_OFF);

            if (enabled==null || !enabled) {
                return false;
            }

            return true;
        }

        return false;
    }

    @Override
    public <T> T adapt( IResolve resolve, Class< T> adapter,
            IProgressMonitor monitor ) throws IOException {

        AbstractOpenWebService< ? , ? > server = null;

        if (resolve.canResolve(WebMapServer.class)) {
            server = resolve.resolve(WebMapServer.class, monitor);
        }
        else if (resolve.canResolve(TiledWebMapServer.class)) {
            server = resolve.resolve(TiledWebMapServer.class, monitor);
        }
        else {
            // if there is no server for the tiles to come from, we can't/wont continue
            WmsPlugin.trace("WMS or WMST required", new ServiceNotFoundException()); //$NON-NLS-1$
            return null;
        }

        if (resolve instanceof IGeoResource && adapter.isAssignableFrom(TileSet.class)) {
            IGeoResource resource = (IGeoResource) resolve;
            TileSet tileSet = WMSCTileUtils.toTileSet(resource, server, monitor);
            
            return adapter.cast( tileSet );
        }
        return null;
    }

}
