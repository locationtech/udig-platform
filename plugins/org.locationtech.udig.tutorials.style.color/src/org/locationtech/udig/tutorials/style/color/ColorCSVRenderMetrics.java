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
package org.locationtech.udig.tutorials.style.color;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;

import org.geotools.util.Range;

public class ColorCSVRenderMetrics extends AbstractRenderMetrics {
    public ColorCSVRenderMetrics( IRenderContext context, IRenderMetricsFactory factory ) {
        super(context, factory, Arrays.asList(new String[]{ColorStyle.ID}));
        this.resolutionMetric = RES_PIXEL;
        this.latencyMetric = LATENCY_LOCAL;
        this.timeToDrawMetric = DRAW_DATA_RAW;
    }
    public boolean canAddLayer( ILayer layer ) {
        return false;
    }
    public boolean canStyle( String styleID, Object value ) {
        return ColorStyle.ID.equals( styleID ) && value instanceof Color;
    }
    public Renderer createRenderer() {
        return new ColorCSVRenderer();
    }
}
