/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests.ui;

import java.io.IOException;
import java.util.List;

import org.locationtech.udig.catalog.tests.DummyGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;
import org.locationtech.udig.project.render.IRenderer;

public class DummyRenderMetricsFactory extends AbstractRenderMetrics {

    protected DummyRenderMetricsFactory( IRenderContext context, IRenderMetricsFactory factory,
            List<String> expectedStyleIds ) {
        super(context, factory, expectedStyleIds);
    }

    public boolean canRender( IRenderContext context ) throws IOException {
        
        return context.getGeoResource() instanceof DummyGeoResource;
    }

    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        return new DummyRenderMetrics(context);
    }

    public Class< ? extends IRenderer> getRendererType() {
        return DummyRenderer.class;
    }

    @Override
    public boolean canAddLayer( ILayer layer ) {
        return false;
    }

    @Override
    public boolean canStyle( String styleID, Object value ) {
        return false;
    }

    @Override
    public Renderer createRenderer() {
        return null;
    }

}
