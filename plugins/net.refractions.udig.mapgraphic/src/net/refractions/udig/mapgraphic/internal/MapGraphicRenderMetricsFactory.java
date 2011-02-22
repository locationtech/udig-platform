package net.refractions.udig.mapgraphic.internal;

import java.io.IOException;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetrics;
import net.refractions.udig.project.render.IRenderMetricsFactory;

public class MapGraphicRenderMetricsFactory implements IRenderMetricsFactory {

    public boolean canRender( IRenderContext context ) throws IOException {
		return context.getLayer().hasResource(MapGraphic.class);
	}

    public IRenderMetrics createMetrics(IRenderContext context) {
        return new MapGraphicRenderMetrics(context, this);
    }

    /**
     * @see net.refractions.udig.project.render.IRenderMetricsFactory#getRendererType()
     */
    public Class<MapGraphicRenderer> getRendererType() {
        return MapGraphicRenderer.class;
    }

}
