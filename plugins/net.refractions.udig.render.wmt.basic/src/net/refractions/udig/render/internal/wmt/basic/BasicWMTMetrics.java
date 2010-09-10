package net.refractions.udig.render.internal.wmt.basic;

import java.util.Arrays;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;


public class BasicWMTMetrics extends AbstractRenderMetrics {

    public BasicWMTMetrics( IRenderContext context2, BasicWMTMetricsFactory factory) {
        super(context2, factory, Arrays.asList(new String[0]));
        
        this.timeToDrawMetric = DRAW_DATA_RAW;
        this.latencyMetric = LATENCY_NETWORK_CACHE;
        this.resolutionMetric = RES_PIXEL;
    }

    public Renderer createRenderer() {
        Renderer renderer=new BasicWMTRenderer();
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
        return false;
    }

    public boolean canStyle( String SyleID, Object value ) {
       return false;
    }
}
