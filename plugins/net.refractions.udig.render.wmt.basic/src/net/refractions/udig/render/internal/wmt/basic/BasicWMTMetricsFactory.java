/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.render.internal.wmt.basic;

import net.refractions.udig.catalog.internal.wmt.WMTGeoResource;
import net.refractions.udig.catalog.internal.wmt.ww.WWGeoResource;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;


public class BasicWMTMetricsFactory implements IRenderMetricsFactory {


    public boolean canRender(IRenderContext toolkit) {
        return toolkit.getLayer().hasResource(WMTGeoResource.class) || 
                toolkit.getLayer().hasResource(WWGeoResource.class);
    }

    /**
     * @see net.refractions.udig.project.render.RenderMetricsFactory#createMetrics(net.refractions.udig.project.render.RenderContext)
     */
    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        return new BasicWMTMetrics(context, this);
    }
    
    /**
     * @see net.refractions.udig.project.render.RenderMetrics#getRendererType()
     */
    public Class<BasicWMTRenderer> getRendererType() {
        return BasicWMTRenderer.class;
    }
}
