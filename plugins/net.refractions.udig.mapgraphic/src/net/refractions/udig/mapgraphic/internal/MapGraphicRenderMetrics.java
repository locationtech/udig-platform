package net.refractions.udig.mapgraphic.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;

import org.geotools.util.Range;

/**
 * Used to describe the rendering abilities of MapGraphicRenderer
 */
public class MapGraphicRenderMetrics extends AbstractRenderMetrics{

    /**
     * Construct <code>MapGraphicRenderMetrics</code>.
     * 
     * @param context
     * @param factory
     */
    public MapGraphicRenderMetrics( IRenderContext context, IRenderMetricsFactory factory) {
        super(context, factory, new ArrayList<String>());
    }
    
    public Renderer createRenderer() {
        MapGraphicRenderer renderer = new MapGraphicRenderer();
        renderer.setContext(context);
        return renderer;
    }

    public boolean canAddLayer( ILayer layer ) {
        if ( layer.hasResource(MapGraphic.class) )
        	return true;
        return false;
    }
    public boolean canStyle( String SyleID, Object value ) {
        return true;
    }
    
    public Set<Range<Double>> getValidScaleRanges() {
        return new HashSet<Range<Double>>();
    }
}