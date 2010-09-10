/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
