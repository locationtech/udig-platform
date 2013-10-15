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