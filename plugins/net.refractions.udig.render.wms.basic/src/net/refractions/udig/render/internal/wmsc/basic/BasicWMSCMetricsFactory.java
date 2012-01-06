/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.render.internal.wmsc.basic;

import java.io.IOException;

import net.refractions.udig.catalog.wmsc.server.TileSet;
import net.refractions.udig.catalog.wmsc.server.TiledWebMapServer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.render.wms.basic.WMSPlugin;

import org.geotools.data.wms.WebMapServer;

/**
 * Creates metrics for WMS-C renderer.
 * 
 * @author Emily Gouge (Refractions Research, Inc)
 * @since 1.1.0
 */
public class BasicWMSCMetricsFactory implements IRenderMetricsFactory {

    public boolean canRender( IRenderContext context ) throws IOException {
        if (context.getLayer().hasResource(TiledWebMapServer.class)) {
            return true;
        }

        if (context.getLayer().hasResource(TileSet.class)
                && context.getLayer().hasResource(WebMapServer.class)) {
            return true;
        }

        return false;
    }

    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        TileSet tileset = null;
        try {
            tileset = context.getLayer().getResource(TileSet.class, null);
        } catch (IOException e) {
            WMSPlugin.log("Cannot create render metrics from wmsc", e);
        }
        return new BasicWMSCRenderMetrics(context, this, tileset);
    }

    public Class< ? extends IRenderer> getRendererType() {
        return BasicWMSCRenderer.class;
    }

}
