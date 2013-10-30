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
package net.refractions.udig.render.internal.feature.basic;

import net.refractions.udig.catalog.IGeoResource;
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
     * 
     * @see net.refractions.udig.project.render.IRenderMetricsFactory#createMetrics(net.refractions.udig.project.render.IRenderContext)
     */
    public BasicFeatureMetrics createMetrics( IRenderContext context ) {
        return new BasicFeatureMetrics(context, this);
    }

    /**
     * @see net.refractions.udig.project.render.IRenderMetricsFactory#canRender(net.refractions.udig.project.render.IRenderContext)
     */
    public boolean canRender( IRenderContext context ) {
        IGeoResource geoResource = context.getGeoResource();
        if (geoResource.canResolve(AbstractGridCoverage2DReader.class)) {
            return false; // give image moasic priority over shapefile
        }
        return context.getGeoResource().canResolve(FeatureSource.class);
    }

    /**
     * @see IRenderMetricsFactory#getRendererType()
     */
    public Class< ? extends IRenderer> getRendererType() {
        return BasicFeatureRenderer.class;
    }

}
