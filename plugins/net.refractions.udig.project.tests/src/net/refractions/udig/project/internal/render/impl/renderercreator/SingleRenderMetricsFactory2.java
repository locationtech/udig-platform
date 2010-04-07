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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.geotools.util.Range;

/**
 * For testing.  Creates a normal Renderer.  Accepts resources that resolve to RendererCreatorTestObjForSingleRenderer objects.
 * @author Jesse
 * @since 1.1.0
 */
public class SingleRenderMetricsFactory2 implements IRenderMetricsFactory {

    public class SingleRenderMetrics extends AbstractRenderMetrics {

        public SingleRenderMetrics( IRenderContext context, IRenderMetricsFactory factory ) {
            super(context, factory, new ArrayList<String>());
        }

        public boolean canAddLayer( ILayer layer ) {
            return layer.hasResource(RendererCreatorTestObjForSingleRenderer.class);
        }

        public boolean canStyle( String styleID, Object value ) {
            return false;
        }

        public Renderer createRenderer() {
            return new SingleRenderer2();
        }

        @SuppressWarnings("unchecked")
        public Set<Range<Double>> getValidScaleRanges() {
            return new HashSet<Range<Double>>();
        }
        
    }

    public boolean canRender( IRenderContext context ) throws IOException {
        return context.getGeoResource().canResolve(RendererCreatorTestObjForSingleRenderer.class);
    }

    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        return new SingleRenderMetrics(context, this);
    }

    public Class< ? extends IRenderer> getRendererType() {
        return SingleRenderer2.class;
    }

}
