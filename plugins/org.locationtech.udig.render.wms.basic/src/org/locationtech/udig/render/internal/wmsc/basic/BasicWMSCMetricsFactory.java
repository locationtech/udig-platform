/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.render.internal.wmsc.basic;

import java.io.IOException;

import org.geotools.ows.wms.WebMapServer;
import org.locationtech.udig.catalog.wmsc.server.TileSet;
import org.locationtech.udig.catalog.wmsc.server.TiledWebMapServer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.render.wms.basic.WMSPlugin;

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
