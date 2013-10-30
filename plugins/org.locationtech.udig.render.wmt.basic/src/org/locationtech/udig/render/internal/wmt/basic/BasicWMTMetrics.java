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
package org.locationtech.udig.render.internal.wmt.basic;

import java.util.Arrays;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;


public class BasicWMTMetrics extends AbstractRenderMetrics {

    public BasicWMTMetrics( IRenderContext context2, BasicWMTMetricsFactory factory) {
        super(context2, factory, Arrays.asList(new String[0]));
        
        this.timeToDrawMetric = DRAW_DATA_RAW;
        this.latencyMetric = LATENCY_NETWORK_CACHE;
        this.resolutionMetric = RES_PIXEL;
    }

    public Renderer createRenderer() {
        Renderer renderer=new BasicWMTRenderer();
        renderer.setContext(context);
        
        return renderer;
    }
    
    public IRenderContext getRenderContext() {
        return context;
    }

    /**
     * @see org.locationtech.udig.project.render.IRenderMetrics#getRenderMetricsFactory()
     */
    public IRenderMetricsFactory getRenderMetricsFactory() {
        return factory;
    }

    public boolean canAddLayer( ILayer layer ) {
        return false;
    }

    public boolean canStyle( String SyleID, Object value ) {
       return false;
    }
}
