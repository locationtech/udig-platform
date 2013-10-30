/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl.renderercreator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;
import org.locationtech.udig.project.render.IRenderer;

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
