package net.refractions.udig.tutorials.render.csv;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;

import org.geotools.util.Range;

public class CSVRenderMetrics extends AbstractRenderMetrics {
    public CSVRenderMetrics( IRenderContext context, IRenderMetricsFactory factory ) {
        super(context, factory, Arrays.asList(new String[0]) );
        this.timeToDrawMetric = DRAW_DATA_RAW;
        this.latencyMetric = LATENCY_LOCAL;
        this.resolutionMetric = RES_PIXEL;
    }
    public boolean canAddLayer( ILayer layer ) {
        return false;
    }
    public boolean canStyle( String styleID, Object value ) {
        return false;
    }
    public Renderer createRenderer() {
        return new CSVRenderer();
    }

}
