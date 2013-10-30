/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.internal;

import java.io.IOException;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;

public class MapGraphicRenderMetricsFactory implements IRenderMetricsFactory {

    public boolean canRender( IRenderContext context ) throws IOException {
		return context.getLayer().hasResource(MapGraphic.class);
	}

    public AbstractRenderMetrics createMetrics(IRenderContext context) {
        return new MapGraphicRenderMetrics(context, this);
    }

    /**
     * @see org.locationtech.udig.project.render.IRenderMetricsFactory#getRendererType()
     */
    public Class<MapGraphicRenderer> getRendererType() {
        return MapGraphicRenderer.class;
    }
    
}
