package net.refractions.udig.tutorials.style.color;

import java.io.IOException;

import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.tutorials.catalog.csv.CSV;

public class ColorCSVRenderMetricsFactory implements IRenderMetricsFactory {
    public boolean canRender( IRenderContext context ) throws IOException {
        return context.getLayer().findGeoResource( CSV.class ) != null;
    }
    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        return new ColorCSVRenderMetrics(context, this);
    }
    public Class< ? extends IRenderer> getRendererType() {
        return ColorCSVRenderer.class;
    }
}
