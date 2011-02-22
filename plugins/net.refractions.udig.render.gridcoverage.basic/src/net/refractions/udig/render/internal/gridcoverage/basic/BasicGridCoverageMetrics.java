package net.refractions.udig.render.internal.gridcoverage.basic;

import java.util.HashSet;
import java.util.Set;

import javax.media.jai.util.Range;

import net.refractions.udig.core.MinMaxScaleCalculator;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.style.sld.SLDContent;
import net.refractions.udig.ui.graphics.SLDs;

import org.geotools.styling.Style;

/**
 * Creates a Metrics object for the basic gridcoverage renderer
 *
 * @author jeichar
 * @since 0.3
 */
public class BasicGridCoverageMetrics extends AbstractRenderMetrics {


    /**
     * Construct <code>BasicGridCoverageMetrics</code>.
     *
     * @param context2
     * @param factory
     */
    public BasicGridCoverageMetrics( IRenderContext context2, BasicGridCoverageMetricsFactory factory ) {
        super( context2, factory);
    }

    public Renderer createRenderer() {
        Renderer r=new BasicGridCoverageRenderer();
        r.setContext(context);
        return r;
    }

    /**
     * @see net.refractions.udig.project.render.RenderMetrics#getRenderContext()
     */
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
        if( value == null || !(value instanceof Style)) return false;

        return !Double.isNaN( SLDs.rasterOpacity( (Style) value ) );
    }

    public boolean isOptimized() {
        return false;
    }

    public Set<Range> getValidScaleRanges() {
        Style style = (Style) context.getLayer().getStyleBlackboard().get(SLDContent.ID);
        if( style == null ) {
            return new HashSet<Range>();
        }
        MinMaxScaleCalculator minMaxScaleCalculator = new MinMaxScaleCalculator();
        style.accept(minMaxScaleCalculator);
        return minMaxScaleCalculator.getRanges();
    }

}
