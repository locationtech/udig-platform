package net.refractions.udig.tutorials.style.color;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;

import org.geotools.util.Range;

public class ColorCSVRenderMetrics extends AbstractRenderMetrics {
    public ColorCSVRenderMetrics( IRenderContext context, IRenderMetricsFactory factory ) {
        super(context, factory, Arrays.asList(new String[]{ColorStyle.ID}));
        this.resolutionMetric = RES_PIXEL;
        this.latencyMetric = LATENCY_LOCAL;
        this.timeToDrawMetric = DRAW_DATA_RAW;
    }
    public boolean canAddLayer( ILayer layer ) {
        return false;
    }
    public boolean canStyle( String styleID, Object value ) {
        return ColorStyle.ID.equals( styleID ) && value instanceof Color;
    }
    public Renderer createRenderer() {
        return new ColorCSVRenderer();
    }
}
