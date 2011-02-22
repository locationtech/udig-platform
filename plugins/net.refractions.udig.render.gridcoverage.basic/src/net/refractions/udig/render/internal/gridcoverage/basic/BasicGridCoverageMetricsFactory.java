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
package net.refractions.udig.render.internal.gridcoverage.basic;

import net.refractions.udig.project.render.ICompositeRenderContext;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetrics;
import net.refractions.udig.project.render.IRenderMetricsFactory;

import org.opengis.coverage.grid.GridCoverage;


/**
 * The RenderMetricFactory Implementation for the BasicGridCoverageRenderer Extension.
 *
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class BasicGridCoverageMetricsFactory implements IRenderMetricsFactory {

    /**
     * TODO summary sentence for canRender ...
     *
     * @see net.refractions.udig.project.render.RenderMetricsFactory#canRender(net.refractions.udig.project.render.RenderTools)
     * @param context
     * @return
     */
    public boolean canRender( IRenderContext context ) {
        if( context instanceof ICompositeRenderContext ){
            return false;
        }
        return context.getGeoResource().canResolve(GridCoverage.class);
    }

    /**
     * TODO summary sentence for createMetrics ...
     *
     * @see net.refractions.udig.project.render.RenderMetricsFactory#createMetrics(net.refractions.udig.project.render.RenderTools)
     * @param context
     * @return
     */
    public IRenderMetrics createMetrics( IRenderContext context ) {
        return new BasicGridCoverageMetrics(context, this);
    }

    public Class getRendererType() {
        return BasicGridCoverageRenderer.class;
    }

}
