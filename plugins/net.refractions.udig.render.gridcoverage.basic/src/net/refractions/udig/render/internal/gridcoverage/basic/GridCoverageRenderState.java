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
package net.refractions.udig.render.internal.gridcoverage.basic;

import java.awt.Rectangle;

import net.refractions.udig.project.render.IRenderContext;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Encapsulates the state required to render a GridCoverage
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class GridCoverageRenderState {

    public float opacity;
    public double minScale;
    public double maxScale;
    public IRenderContext context;
    public ReferencedEnvelope bounds;
    public Rectangle displayArea;

    public GridCoverageRenderState( IRenderContext context, ReferencedEnvelope bbox, Rectangle displayArea, float opacity,
            double minScale, double maxScale ) {
        this.opacity = opacity;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.context = context;
        this.bounds = bbox;
        this.displayArea = displayArea;
    }

}