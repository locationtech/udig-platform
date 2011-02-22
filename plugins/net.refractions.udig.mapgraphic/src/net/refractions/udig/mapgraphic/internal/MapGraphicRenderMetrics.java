package net.refractions.udig.mapgraphic.internal;

import java.util.HashSet;
import java.util.Set;

import javax.media.jai.util.Range;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetrics;
import net.refractions.udig.project.render.IRenderMetricsFactory;

/**
 * Used to describe the rendering abilities of MapGraphicRenderer
 */
public class MapGraphicRenderMetrics implements IRenderMetrics {

    IRenderContext context;
    private IRenderMetricsFactory factory;

    /**
     * Construct <code>MapGraphicRenderMetrics</code>.
     *
     * @param context
     * @param factory
     */
    public MapGraphicRenderMetrics( IRenderContext context, IRenderMetricsFactory factory ) {
        this.factory = factory;
        this.context = context;
    }
    public Renderer createRenderer() {
        MapGraphicRenderer renderer = new MapGraphicRenderer();
        renderer.setContext(context);
        return renderer;
    }

    public IRenderContext getRenderContext() {
        return context;
    }

    /**
     * @see net.refractions.udig.project.render.IRenderMetrics#getRenderMetricsFactory()
     */
    public IRenderMetricsFactory getRenderMetricsFactory() {
        return factory;
    }

    public boolean canAddLayer( ILayer layer ) {
        if ( layer.hasResource(MapGraphic.class) )
        	return true;
        return false;
    }
    public boolean canStyle( String SyleID, Object value ) {
        return true;
    }
    public boolean isOptimized() {
        return false;
    }
    public Set<Range> getValidScaleRanges() {
        return new HashSet<Range>();
    }
}
