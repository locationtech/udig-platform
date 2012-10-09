/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.tutorials.style.color;

import java.io.IOException;

import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.tutorials.catalog.csv.CSV;

public class ColorCSVRenderMetricsFactory implements IRenderMetricsFactory {
    public boolean canRender( IRenderContext context ) throws IOException {
        return context.getLayer().findGeoResource( CSV.class ) != null;
    }
    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        return new ColorCSVRenderMetrics(context, this);
    }
    public Class< ? extends IRenderer> getRendererType() {
        return ColorCSVRenderer.class;
    }
}
