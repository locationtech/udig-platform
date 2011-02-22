package net.refractions.udig.project.tests.ui;

import java.io.IOException;

import net.refractions.udig.catalog.tests.DummyGeoResource;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetrics;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

public class DummyRenderMetricsFactory implements IRenderMetricsFactory {

    public boolean canRender( IRenderContext context ) throws IOException {

        return context.getGeoResource() instanceof DummyGeoResource;
    }

    public IRenderMetrics createMetrics( IRenderContext context ) {
        return new DummyRenderMetrics(context);
    }

    public Class< ? extends IRenderer> getRendererType() {
        return DummyRenderer.class;
    }

}
