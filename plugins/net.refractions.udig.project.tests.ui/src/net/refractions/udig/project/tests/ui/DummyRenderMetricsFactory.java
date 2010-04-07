package net.refractions.udig.project.tests.ui;

import java.io.IOException;
import java.util.List;

import net.refractions.udig.catalog.tests.DummyGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

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
