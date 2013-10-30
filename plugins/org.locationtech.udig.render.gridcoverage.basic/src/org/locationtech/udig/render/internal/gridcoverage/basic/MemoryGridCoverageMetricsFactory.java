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
