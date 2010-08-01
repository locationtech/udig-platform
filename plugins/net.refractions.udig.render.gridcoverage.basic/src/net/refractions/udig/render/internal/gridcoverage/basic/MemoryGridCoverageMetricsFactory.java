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

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.rasterings.GridCoverageLoader;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.ICompositeRenderContext;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;


/**
 * The RenderMetricFactory Implementation for the BasicGridCoverageRenderer Extension.
 *
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class MemoryGridCoverageMetricsFactory implements IRenderMetricsFactory {

    /**
     * Ensures that we can get an AbstractGridCoverage2DReader out of this class.
     * 
     * @see net.refractions.udig.project.render.RenderMetricsFactory#canRender(net.refractions.udig.project.render.RenderTools)
     * @param context
     * @return true if we can render the provided context using BasicGridCoverageRenderer
     */
    public boolean canRender( IRenderContext context ) {
        if( context instanceof ICompositeRenderContext ){
            return false;
        }
        IGeoResource geoResource = context.getGeoResource();
        ID id = context.getGeoResource().getID();
        if( id.isMemory() ){
            // image already in memory don't double cache!
            // (this could be handled by not allowing the temp entry
            //  to resolve to GridCoverageLoader - so this check is a safety)
            return false;
        }        
		if( geoResource.canResolve(GridCoverageLoader.class) ){
			return true;
		}
		return false;
    }

    /**
     * Strategy object used to indicate how well a renderer can draw.
     * 
     * @see net.refractions.udig.project.render.RenderMetricsFactory#createMetrics(net.refractions.udig.project.render.RenderTools)
     * @param context
     * @return render metrics for the provided context
     */
    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        return new MemoryGridCoverageMetrics(context, this);
    }

    public Class< ? extends IRenderer> getRendererType() {
        return MemoryGridCoverageRenderer.class;
    }

}
