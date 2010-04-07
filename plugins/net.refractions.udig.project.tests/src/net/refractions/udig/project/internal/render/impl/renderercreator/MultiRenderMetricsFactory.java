/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.render.impl.renderercreator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.geotools.util.Range;

/**
 * For testing.  Creates a MultiLayerRenderer.  Accepts resources that resolve to RendererCreatorTestObjForMulitRenderer objects.
 * @author Jesse
 * @since 1.1.0
 */
public class MultiRenderMetricsFactory implements IRenderMetricsFactory {

    public class MultiRenderMetrics extends AbstractRenderMetrics {

        public MultiRenderMetrics( IRenderContext context, IRenderMetricsFactory factory ) {
            super(context, factory, new ArrayList<String>());
        }

        public boolean canAddLayer( ILayer layer ) {
            if (!layer.hasResource(RendererCreatorTestObjForMulitRenderer.class))
                return false;
            
            List<IGeoResource> resources = layer.getGeoResources();
            for( IGeoResource resource : resources ) {
                try {
                    URL identifier = resource.parent(null).getIdentifier();
                    URL id2=getRenderContext().getGeoResource().parent(null).getIdentifier();
                    if( identifier.toString().contains("dummy") &&  id2.toString().contains("dummy")) //$NON-NLS-1$ //$NON-NLS-2$
                        return true;
                } catch (IOException e) {
                    // do nothing.
                }
            }
            return false;
        }

        public boolean canStyle( String styleID, Object value ) {
            return value instanceof MultiRendererStyleContent;
        }

        public Renderer createRenderer() {
            return new MultiLayerRenderer();
        }
        @SuppressWarnings("unchecked")
        public Set<Range<Double>> getValidScaleRanges() {
            return new HashSet<Range<Double>>();
        }

    }

    public boolean canRender( IRenderContext context ) throws IOException {
        return context.getGeoResource().canResolve(RendererCreatorTestObjForMulitRenderer.class);
    }

    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        return new MultiRenderMetrics(context, this);
    }

    public Class< ? extends IRenderer> getRendererType() {
        return MultiLayerRenderer.class;
    }

}
