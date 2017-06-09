/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.MultiLayerRenderer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;

import org.geotools.util.Range;

/**
 * Sorts AbstractRenderMetrics objects based on how well the renderer 
 * suits the Layer and the resource.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class AbstractRenderMetricsSorter implements Comparator<AbstractRenderMetrics>, Serializable {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 1L;

    private final List<Layer> layersCopy;

    /**
     * Create new instance
     * 
     * @param layers the layers to sort
     */
    public AbstractRenderMetricsSorter( List<Layer> layers ) {
        super();
        this.layersCopy = layers;
    }

    public List<Layer> getLayers() {
        return layersCopy;
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( AbstractRenderMetrics o1, AbstractRenderMetrics o2 ) {
        if (!canRender(o1)) {
            if (!canRender(o2))
                return 0;
            return 1;
        }

        if (!canRender(o2)) {
            return -1;
        }

        return compareMetrics(o1, o2);
    }

    private boolean canRender( AbstractRenderMetrics o1 ) {
        try {
            if (o1.getRenderMetricsFactory() == null) {
                ProjectPlugin
                        .log(o1
                                + " is not implemented correctly.  getRenderMetricsFactory returns null.  " + //$NON-NLS-1$
                                "Consider extending AbstractRenderMetrics"); //$NON-NLS-1$
                return false;
            }

            if (!o1.getRenderMetricsFactory().canRender(o1.getRenderContext()))
                return false;
        } catch (Throwable e) {
            ProjectPlugin.log("Error while calling CanRender() on " + o1.toString(), e); //$NON-NLS-1$
            return false;
        }
        return true;
    }

    /**
     * Compares two metrics return a value of 0 (same), -1 (o1 preferred), 1 (02 preferred)
     * 
     * @param o1
     * @param o2
     * @return
     */
    private int compareMetrics( AbstractRenderMetrics o1, AbstractRenderMetrics o2 ) {
        int first = -1;
        int last = 1;

        // check the layer blackboards for a preferred or non-preferred renderer
        int perferredMapRenderer = rateUsingBlackboardSettings(o1, o1.getRenderContext().getLayer()
                .getBlackboard());
        if (perferredMapRenderer == 0) {
        	perferredMapRenderer = rateUsingBlackboardSettings(o1, o1.getRenderContext().getLayer()
                    .getStyleBlackboard());
        }
        int perferredMapRenderer2 = rateUsingBlackboardSettings(o2, o2.getRenderContext().getLayer()
                .getBlackboard());
        if (perferredMapRenderer2 == 0) {
        	perferredMapRenderer2 = rateUsingBlackboardSettings(o2, o2.getRenderContext().getLayer()
                    .getStyleBlackboard());
        }
        if (perferredMapRenderer > perferredMapRenderer2) {
            return first;
        }
        if (perferredMapRenderer < perferredMapRenderer2) {
            return last;
        }
        
        // check the map blackboard for a preferred or non-preferred renderer
        perferredMapRenderer = rateUsingBlackboardSettings(o1, o1.getRenderContext().getMap()
                .getBlackboard());
        perferredMapRenderer2 = rateUsingBlackboardSettings(o2, o2.getRenderContext().getMap()
                .getBlackboard());  
        if (perferredMapRenderer > perferredMapRenderer2) {
            return first;
        }
        if (perferredMapRenderer < perferredMapRenderer2) {
            return last;
        }

        //if no BlackboardSettings exist to influence the renderer choice then
        //compute a rate for each metric and compare
        double r1 = rate(o1);
        double r2 = rate(o2);

        // lower rating better
        if (r1 > r2)
            return last;
        if (r1 < r2)
            return first;

        return 0;
    }

    /*
     * Weighting to apply to metrics when computing a rating for a given metrics factory.
     */
    protected final double WEIGHT_RENDER_APPEARANCE_METRIC = 0.2;
    protected final double WEIGHT_USER_APPEARANCE_METRIC = 0.25;
    protected final double WEIGHT_SCALERANGE_METRIC = 0.15;
    protected final double WEIGHT_LATENCY_METRIC = 0.1;
    protected final double WEIGHT_DRAWING_TIME_METRIC = 0.1;
    protected final double WEIGHT_RESOLUTION_METRIC = 0.1;
    protected final double WEIGHT_MULTILAYER_METRIC = 0.1;

    protected final long MAXIMUM_LATENCY = 120000; // 2 minutes
    protected final long MAXIMUM_DRAWINGTIME = 120000; // 2 minutes

    /**
     * Computes a rating for the metric. The rating for the metric is based on the appearance
     * metrics, speed metrics, resolution metrics.
     * <p>
     * Each metric is converted to a percent between 0 & 1 and given a weighting depending on how
     * important the metric is. Appearance metrics currently have higher weighting that performance.
     * </p>
     * 
     * @param metrics
     * @return A rating for a given metric. The lower the rating the better fit the metric is.
     */
    private double rate( AbstractRenderMetrics metrics ) {
        ILayer layer = metrics.getRenderContext().getLayer();
        final IStyleBlackboard style = layer.getStyleBlackboard();

        // render metrics -
        // guarenteed to be between 0 & 1 - higher better so we subtract one to get make lower
        // better
        double renderAppearanceMetric = 1 - metrics.getUserAppearanceMetric(style);
        double userAppearanceMetric = 1 - metrics.getRenderAppearanceMetric(style);

        double latencyMetric = metrics.getLatencyMetric();
        latencyMetric = latencyMetric / MAXIMUM_LATENCY;

        double drawingTimeMetric = metrics.getDrawingTimeMetric();
        drawingTimeMetric = drawingTimeMetric / MAXIMUM_DRAWINGTIME;

        // resolution metric - close to 1 the better
        // worst case is the screen size
        double width = metrics.getRenderContext().getMapDisplay().getWidth();
        double resolutionMetric = metrics.getResolutionMetric();
        double diff = Math.abs(resolutionMetric - 1);
        if (diff == 0) {
            resolutionMetric = 0; // perfect match (lower is better)
        } else {
            resolutionMetric = (diff / width); // lower number
        }

        int multiLayerMetric = 1 - rateMultiLayerRenderer(metrics);
        double scaleRangeMetric = 1 - rateScaleRange(metrics);

        double rating = renderAppearanceMetric * WEIGHT_RENDER_APPEARANCE_METRIC;
        rating += userAppearanceMetric * WEIGHT_USER_APPEARANCE_METRIC;
        rating += latencyMetric * WEIGHT_LATENCY_METRIC;
        rating += drawingTimeMetric * WEIGHT_DRAWING_TIME_METRIC;
        rating += resolutionMetric * WEIGHT_RESOLUTION_METRIC;
        rating += multiLayerMetric * WEIGHT_MULTILAYER_METRIC;
        rating += scaleRangeMetric * WEIGHT_SCALERANGE_METRIC;

        return rating;

        // int rating = rateAppearance(metrics);
        // rating += ratePerformance(metrics);
        // rating += rateScaleRange(metrics);
        // rating = rateMultiLayerRenderer(metrics, rating);
        // rating = rateUsingBlackboardSettings(metrics,
        // metrics.getRenderContext().getMap().getBlackboard(), 100,rating);
        // rating = rateUsingBlackboardSettings(metrics,
        // metrics.getRenderContext().getLayer().getBlackboard(), 200,rating);
        // if (rating == 0)
        // return -1;
        // return rating;
    }

    /**
     * If the renderer can render at the current scale then 1 will be returned
     * 
     * @param metrics
     * @return 1 if the renderer can render at the current scale.
     */
    private int rateScaleRange( AbstractRenderMetrics metrics ) {
        Set<Range<Double>> scales = metrics.getValidScaleRanges();
        for( Range<Double> range : scales ) {
            if (range.contains(metrics.getRenderContext().getViewportModel().getScaleDenominator())) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Looks if the renderer is set on the style blackboard as the preferred renderer. If so then
     * the rating is inflated so that this renderer is always used. Can either add to the renderer
     * score if it is a preferred renderer, or detract from a renderer score if it is a last resort
     * renderer.
     * 
     * @param metrics metrics in question
     * @param blackboard the blackboard to search (map or layer)
     * @return 1 if preferred renderer<br>
     *         0 neither preferred or last resort<br>
     *         -1 if last resort renderer
     */
    private int rateUsingBlackboardSettings( AbstractRenderMetrics metrics, IBlackboard blackboard ) {
        String rendererId = metrics.getId();
        if (rendererId == null)
            return 0;
        String preferredRenderer = blackboard.getString(RendererCreatorImpl.PREFERRED_RENDERER_ID);
        String lastResortRenderer = blackboard
                .getString(RendererCreatorImpl.LAST_RESORT_RENDERER_ID);
        if (rendererId.equals(preferredRenderer)) {
            return 1;
        }
        if (rendererId.equals(lastResortRenderer)) {
            return -1;
        }
        return 0;
    }

    /**
     * if it can render more than one layer in the list
     * 
     * @return 0 - if cannot use multi layer renderer<br>
     *         1 - if multi layer render can be used.
     */
    private int rateMultiLayerRenderer( AbstractRenderMetrics metrics ) {
        if (MultiLayerRenderer.class.isAssignableFrom(metrics.getRenderMetricsFactory()
                .getRendererType())) {
            int indexOf = getLayers().indexOf(metrics.getRenderContext().getLayer());
            if (indexOf > 0)
                if (metrics.canAddLayer(getLayers().get(indexOf - 1)))
                    return 1;
            if (indexOf < getLayers().size() - 1)
                if (metrics.canAddLayer(getLayers().get(indexOf + 1)))
                    return 1;
        }
        return 0;
    }

}
