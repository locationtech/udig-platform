package net.refractions.udig.project.internal.render.impl;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.media.jai.util.Range;

import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.MultiLayerRenderer;
import net.refractions.udig.project.internal.render.impl.InternalRenderMetricsFactory.InternalRenderMetrics;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

/**
 * Sorts RenderMetrics objects based on how well the renderer suits the Layer and the resource
 *
 * @author Jesse
 * @since 1.1.0
 */
public class RenderMetricsSorter implements Comparator<InternalRenderMetrics>, Serializable {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 1L;

    private final List<Layer> layersCopy;

    /**
     * Create new instance
     *
     * @param layers the layers to sort
     */
    public RenderMetricsSorter( List<Layer> layers ) {
        super();
        this.layersCopy = layers;
    }

    public List<Layer> getLayers() {
        return layersCopy;
    }
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( InternalRenderMetrics o1, InternalRenderMetrics o2 ) {
        if( !canRender(o1) ){
            if( !canRender(o2) )
                return 0;
            return 1;
        }

        if( !canRender(o2) ){
            return -1;
        }

        int result1 = rate(o1);
        int result2 = rate(o2);
        if (result1 == result2)
            return 0;
        return result1 > result2 ? -1 : 1;
    }

    private boolean canRender( InternalRenderMetrics o1 ) {
        try {
            if (o1.getRenderMetricsFactory() == null) {
                ProjectPlugin.log(o1
                        + " is not implemented correctly.  getRenderMetricsFactory returns null.  " + //$NON-NLS-1$
                        "Consider extending AbstractRenderMetrics"); //$NON-NLS-1$
                return false;
            }

            if (!o1.getRenderMetricsFactory().canRender(o1.getRenderContext()))
                return false;
        } catch (Throwable e) {
            ProjectPlugin.log("Error while calling CanRender() on " + o1.getId(), e); //$NON-NLS-1$
            return false;
        }
        return true;
    }

    private int rateAppearance( final InternalRenderMetrics metrics ) {
        ILayer layer = metrics.getRenderContext().getLayer();
        final IStyleBlackboard style = layer.getStyleBlackboard();
        class Evaluator implements ExtensionPointProcessor {
            int rating = 0;
            public void process( IExtension extension, IConfigurationElement element )
                    throws Exception {
                String id = element.getAttribute("id"); //$NON-NLS-1$
                Object value = style.get(id);
                if (metrics.canStyle(id, value)) {
                    rating++; // more stylish
                    if( style.isSelected(id) )
                        rating++;
                }
            }
        };
        Evaluator judge = new Evaluator();
        ExtensionPointUtil.process(ProjectPlugin.getPlugin(),
                "net.refractions.udig.project.style", judge); //$NON-NLS-1$
        return judge.rating;
    }

    private int ratePerformance( InternalRenderMetrics metrics ) {
        return metrics.isOptimized() ? 1 : 0;
    }

    private int rate( InternalRenderMetrics metrics ) {

        int rating = rateAppearance(metrics);
        rating += ratePerformance(metrics);
        rating += rateScaleRange(metrics);
        rating = rateMultiLayerRenderer(metrics, rating);
        rating = rateUsingBlackboardSettings(metrics, metrics.getRenderContext().getMap().getBlackboard(), 100,rating);
        rating = rateUsingBlackboardSettings(metrics, metrics.getRenderContext().getLayer().getBlackboard(), 200,rating);
        if (rating == 0)
            return -1;
        return rating;
    }

    /**
     * If the renderer can render at the current scale then 1 will be returned
     *
     * @param metrics
     * @return 1 if the renderer can render at the current scale.
     */
    private int rateScaleRange( InternalRenderMetrics metrics ) {
        Set<Range> scales = metrics.getValidScaleRanges();
        for( Range range : scales ) {
            if( range.contains(metrics.getRenderContext().getViewportModel().getScaleDenominator()) ){
                return 1;
            }
        }
        return 0;
    }

    /**
     * @param metrics metrics in question
     * @param blackboard the blackboard to search (map or layer)
     * @param valueOfMatch how much a match is worth
     * @param rating2 the current rating.
     * @return the new rating.
     */
    private int rateUsingBlackboardSettings( InternalRenderMetrics metrics, IBlackboard blackboard, int valueOfMatch, int rating2 ) {
        int rating = rating2;
        String rendererId = metrics.getId();
        if (rendererId == null)
            return rating;
        String preferredRenderer = blackboard.getString(RendererCreatorImpl.PREFERRED_RENDERER_ID);
        String lastResortRenderer = blackboard.getString(RendererCreatorImpl.LAST_RESORT_RENDERER_ID);
        if ( rendererId.equals(preferredRenderer)) {
            rating += valueOfMatch;
        }
        if ( rendererId.equals(lastResortRenderer)) {
            rating -= valueOfMatch;
        }
        return rating;
    }

    private int rateMultiLayerRenderer( InternalRenderMetrics metrics, int rating2 ) {
        int rating = rating2;
        if (MultiLayerRenderer.class.isAssignableFrom(metrics.getRenderMetricsFactory()
                .getRendererType())) {
            int indexOf = getLayers().indexOf(metrics.getRenderContext().getLayer());
            if( indexOf>0 )
                if( metrics.canAddLayer(getLayers().get(indexOf-1)) )
                    rating++;
            if( indexOf<getLayers().size()-1 )
                if( metrics.canAddLayer(getLayers().get(indexOf+1)) )
                    rating++;
        }
        return rating;
    }

}
