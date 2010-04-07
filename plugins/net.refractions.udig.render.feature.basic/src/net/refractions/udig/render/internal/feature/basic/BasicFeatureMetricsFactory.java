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
package net.refractions.udig.render.internal.feature.basic;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FeatureSource;

/**
 * The RenderMetricsFactory object for the BasicFeatureRenderer Extension
 *
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class BasicFeatureMetricsFactory implements IRenderMetricsFactory {

    /**
     * Creates a new render metrics that supports the given styles
     * @see net.refractions.udig.project.render.IRenderMetricsFactory#createMetrics(net.refractions.udig.project.render.IRenderContext)
     */
    public AbstractRenderMetrics createMetrics(IRenderContext context) {
        return new BasicFeatureMetrics(context, this);
    }

    /**
     * @see net.refractions.udig.project.render.IRenderMetricsFactory#canRender(net.refractions.udig.project.render.IRenderContext)
     */
    public boolean canRender(IRenderContext context) {
    	IGeoResource geoResource = context.getGeoResource();
		if( geoResource.canResolve(AbstractGridCoverage2DReader.class) ){
			return false; // give image moasic priority over shapefile
		}
		return context.getGeoResource().canResolve(FeatureSource.class);
    }

    /**
     * @see IRenderMetricsFactory#getRendererType()
     */
    public Class<? extends IRenderer> getRendererType() {
        return BasicFeatureRenderer.class;
    }


}
