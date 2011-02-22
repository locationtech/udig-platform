package net.refractions.udig.project.tests.ui;

import java.util.HashSet;
import java.util.Set;

import javax.media.jai.util.Range;

import net.refractions.udig.core.Option;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetrics;
import net.refractions.udig.project.render.IRenderMetricsFactory;

public class DummyRenderMetrics implements IRenderMetrics {

    private IRenderContext context;

    public DummyRenderMetrics( IRenderContext context ) {
        this.context=context;
    }

    public Renderer createRenderer() {
        return new DummyRenderer();
    }

    public IRenderContext getRenderContext() {
        return context;
    }

    public IRenderMetricsFactory getRenderMetricsFactory() {
        return null;
    }

    public boolean canStyle( String styleID, Object value ) {
        return true;
    }

    public boolean isOptimized() {
        return false;
    }

    public boolean canAddLayer( ILayer layer ) {
        return false;
    }

    @SuppressWarnings("unchecked")
    public Set<Range> getValidScaleRanges() {
        return new HashSet<Range>();
    }

}
