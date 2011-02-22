package net.refractions.udig.tutorials.render.csv;

import java.util.HashSet;
import java.util.Set;

import javax.media.jai.util.Range;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;

public class CSVRenderMetrics extends AbstractRenderMetrics {
    public CSVRenderMetrics( IRenderContext context, IRenderMetricsFactory factory ) {
        super(context, factory);
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
    public Set<Range> getValidScaleRanges() {
        return new HashSet<Range>();
    }
}
