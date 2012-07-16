/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.wms.tileset;

import java.io.IOException;

import javax.management.ServiceNotFoundException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.internal.wms.WmsPlugin;
import net.refractions.udig.catalog.wmsc.server.TileSet;
import net.refractions.udig.catalog.wmsc.server.TiledWebMapServer;
import net.refractions.udig.catalog.wmsc.server.WMSTileSet;
import net.refractions.udig.project.internal.render.impl.ScaleUtils;
import net.refractions.udig.project.ui.preferences.PreferenceConstants;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.ows.AbstractOpenWebService;
import org.geotools.data.ows.CRSEnvelope;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.StyleImpl;
import org.geotools.data.wms.WebMapServer;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

/**
 * An object to allow a IGeoResource to adapt to a TileSet, thus allowing any wms service the
 * ability to masquerade as a tile cache.
 * 
 * @author jhudson
 * @since 1.2.0
 */
public class WMSCTileSetAdapter implements IResolveAdapterFactory {

    @Override
    public boolean canAdapt( IResolve resolve, Class< ? extends Object> adapter ) {

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
    public Object adapt( IResolve resolve, Class< ? extends Object> adapter,
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

        if (adapter.isAssignableFrom(TileSet.class)) {

            IGeoResource resource = (IGeoResource) resolve;
            IGeoResourceInfo info = resource.getInfo(monitor);

            String source = server.getInfo().getSource().toString();
            String version = server.getCapabilities().getVersion();

            if (source == null || "".equals(source)) { //$NON-NLS-1$
                WmsPlugin
                        .log("Tileset must have a service URL defined, source is null", new ServiceNotFoundException()); //$NON-NLS-1$
                return null;
            }

            if (version == null || "".equals(version)) { //$NON-NLS-1$
                WmsPlugin
                        .log("Tileset must have a service version defined, version is null", new ServiceNotFoundException()); //$NON-NLS-1$
                return null;
            }

            String srs = CRS.toSRS(info.getCRS());
            TileSet tileset = new WMSTileSet();

            double minX = info.getBounds().getMinimum(0);
            double maxX = info.getBounds().getMaximum(0);
            double minY = info.getBounds().getMinimum(1);
            double maxY = info.getBounds().getMaximum(1);

            CRSEnvelope bbox = new CRSEnvelope(srs, minX, minY, maxX, maxY);
            tileset.setBoundingBox(bbox);
            tileset.setCoorindateReferenceSystem(srs);

            Integer width = Integer.parseInt((String) resource
                    .getPersistentProperties()
                    .get(PreferenceConstants.P_TILESET_WIDTH));
            Integer height = Integer.parseInt((String) resource
                    .getPersistentProperties()
                    .get(PreferenceConstants.P_TILESET_HEIGHT));

            if (width == null) {
                width = PreferenceConstants.DEFAULT_TILE_SIZE;
            }

            if (height == null) {
                height = PreferenceConstants.DEFAULT_TILE_SIZE;
            }

            tileset.setWidth(width);
            tileset.setHeight(height);

            String imageType = (String) resource
                    .getPersistentProperties()
                    .get(PreferenceConstants.P_TILESET_IMAGE_TYPE);

            if (imageType == null || "".equals(imageType)) { //$NON-NLS-1$
                imageType = PreferenceConstants.DEFAULT_IMAGE_TYPE;
            }

            tileset.setFormat(imageType);

            /*
             * The layer ID
             */
            tileset.setLayers(info.getName());

            String scales = (String) resource
                    .getPersistentProperties()
                    .get(PreferenceConstants.P_TILESET_SCALES);

            String resolutions = workoutResolutions(scales, new ReferencedEnvelope(bbox), width);

            /*
             * If we have no resolutions to try - we wont.
             */
            if ("".equals(resolutions)) { //$NON-NLS-1$
                WmsPlugin
                        .log("Tileset must have resolutions based on the maps scale, resolutions are null", new ServiceNotFoundException()); //$NON-NLS-1$
                return null;
            }

            tileset.setResolutions(resolutions);

            /*
             * The styles
             */
            String style = ""; //$NON-NLS-1$
            if (resolve.canResolve(Layer.class)) {
                Layer layer = resolve.resolve(Layer.class, monitor);
                StringBuilder sb = new StringBuilder(""); //$NON-NLS-1$
                for( StyleImpl layerStyle : layer.getStyles() ) {
                    sb.append(layerStyle.getName()+","); //$NON-NLS-1$
                }
                style = sb.toString();
            }
            if (style.length()>0){
                tileset.setStyles(style.substring(0, style.length()-1));
            } else {
                tileset.setStyles(style);
            }

            /*
             * The server is where tiles can be retrieved
             */
            tileset.setServer(server);

            return tileset;
        }
        return null;
    }

    /**
     * From a list of scales turn them into a list of resolutions
     * 
     * @param rawScales
     * @param bounds
     * @param tileWidth
     * @return space separated String of resolutions based on the scale values of the WMSTileSet
     */
    private String workoutResolutions( String rawScales, ReferencedEnvelope bounds, int tileWidth ) {
        String[] scales = rawScales.split(" "); //$NON-NLS-1$
        StringBuffer sb = new StringBuffer();
        for( String scale : scales ) {
            Double scaleDouble = Double.parseDouble(scale);
            Double calculatedScale = ScaleUtils.calculateResolutionFromScale(bounds, scaleDouble,
                    tileWidth);
            sb.append(calculatedScale + " "); //$NON-NLS-1$
        }
        return sb.toString();
    }
}
