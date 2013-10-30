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
