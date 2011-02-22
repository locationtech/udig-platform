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
package net.refractions.udig.render.internal.feature.shapefile;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetrics;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.render.internal.feature.basic.BasicFeatureMetricsFactory;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.view.DefaultView;

/**
 * The RenderMetricsFactory object for the BasicFeatureRenderer Extension
 *
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ShapefileFeatureMetricsFactory extends BasicFeatureMetricsFactory {

    /**
     * @see net.refractions.udig.project.render.IRenderMetricsFactory#createMetrics(net.refractions.udig.project.render.IRenderContext)
     */
    public IRenderMetrics createMetrics(IRenderContext context) {
        return new ShapefileFeatureMetrics(context, this);
    }

    /**
     * @see net.refractions.udig.project.render.IRenderMetricsFactory#canRender(net.refractions.udig.project.render.IRenderContext)
     */
    public boolean canRender(IRenderContext context) {
        try {
            IGeoResource geoResource = context.getGeoResource();
            FeatureSource fs=geoResource.resolve(FeatureSource.class, null);

            boolean notAView = !(fs instanceof DefaultView);
            boolean isAShapefile = (fs.getDataStore() instanceof ShapefileDataStore);
            return notAView && fs!=null && isAShapefile;
        }
        catch( Throwable t ) {
            return false;
        }
    }

    /**
     * @see IRenderMetricsFactory#getRendererType()
     */
    public Class<? extends IRenderer> getRendererType() {
        return ShapefileFeatureRenderer.class;
    }

}
