/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.render;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.geotools.util.Range;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.project.internal.render.Renderer;

/**
 * Used to calculate estimated metrics for a renderer while rendering a particular layer.
 * <p>
 * RenderMetrics objects are used to decide which renderer to instantiate to render a layer. As the
 * state of udig changes different renderers may be more suited so the RenderMetrics object are also
 * used to judge whether a new renderer should be used for rendering a layer.
 *
 * <p>
 * The render metrics currently consists of:
 * <ul>
 * <li>latency metric - time to connect to the georesource</li>
 * <li>time to draw metric - time from start rendering to finished rendering</li>
 * <li>resolution metric - ratio of source pixels to screen pixels</li>
 * <li>appearance metric - how well the renderer can style the georesouce given the layer
 * information</li>
 * </ul>
 *
 * @author Jesse Eichar, Emily Gouge
 * @version Since 1.1.0
 */
public abstract class AbstractRenderMetrics {

    protected final IRenderContext context;

    protected final IRenderMetricsFactory factory;

    /**
     * A list of style ids the renderer expects to use during rendering. This is used to compute the
     * appearanceMetric
     */
    private final List<String> expectedStyleIDs;

    /**
     * The Latency Metric - the expected time it takes to connect to a georesource. Generally, local
     * georesources should have a lower latency than network georesources.
     * <p>
     * Measured in milliseconds.
     * </p>
     */
    protected long latencyMetric = LATENCY_LOCAL;

    /**
     * The Time to Draw Metric - the expect time is takes to complete drawing. The time from when it
     * starts to draw to when it is complete. Generally, images already in raster format will draw
     * faster then vector based features that have to be converted to raster format before drawing.
     * <p>
     * Measured in milliseconds.
     * </p>
     */
    protected long timeToDrawMetric = DRAW_DATA_RAW;

    /**
     * The resolution metric - a ratio of source pixels to screen pixels.
     */
    protected double resolutionMetric = RES_PIXEL;

    /**
     * Determine how long we expect the data to draw. For defaults: DRAW_DATA_INDEX,
     * DRAW_DATA_MEMORY, DRAW_DATA_RAW, DRAW_IMAGE_INDEX, DRAW_IMAGE_MEMORY, DRAW_IMAGE_RAW
     *
     * @return Expected time to draw in milliseconds
     */
    public long getTimeToDrawMetric() {
        return timeToDrawMetric;
    }

    /* LATENCY METRIC DEFAULTS */
    /**
     * The datastore is in memory only. No disk access is required.
     */
    public static final long LATENCY_MEMORY = 0;

    /**
     * The datastore is cached in memory once initial read. Can initial be read from disk or
     * network.
     */
    public static final long LATENCY_MEMORY_CACHE = 100;

    /**
     * The datastore is located on local disk. Is is read each time it is rendered.
     *
     * Example: Shapefile
     */
    public static final long LATENCY_LOCAL = 200;

    /**
     * The data is cached locally but may first have to been read from a non-local server. This data
     * is stored on locally on a disk.
     *
     * Example: Zipped Shapefile on wiki
     */
    public static final long LATENCY_LOCAL_CACHE = 300;

    /**
     * The data is available over the network; but there is caching off-site.
     *
     * Example: WMSC TileServer
     */
    public static final long LATENCY_NETWORK_CACHE = 400;

    /**
     * The data is available over the network with no known caching.
     *
     * Example: WMS/WMF Server
     */
    public static final long LATENCY_NETWORK = 500;

    /* DRAWING TIME DEFAULTS */

    /**
     * An indexed image able to draw just part of the image. With an index we should be able to read
     * just what is needed.
     */
    public static final long DRAW_IMAGE_INDEX = 100;

    /**
     * Image in memory; not indexed.
     */
    public static final long DRAW_IMAGE_MEMORY = 200;

    /**
     * Image on disk; not indexed. Without an index the entire image may need to be read.
     */
    public static final long DRAW_IMAGE_RAW = 300;

    /**
     * Used to represent formats like jpeg where the file needs to be decompressed in order to ready
     * any part of the file.
     */
    public static final long DRAW_IMAGE_COMPRESSED = 600;

    /**
     * In memory data that needs to be converted to image before being drawn.
     */
    public static final long DRAW_DATA_MEMORY = 400;

    /**
     * Data that is indexed that needs to be converted to image before being drawn. The index cuts
     * down on the amount of features considered for drawing.
     */
    public static final long DRAW_DATA_INDEX = 500;

    /**
     * Data with no index that needs to be converted to image before being drawn. Without an index
     * every feature will be considered and or clipped.
     */
    public static final long DRAW_DATA_RAW = 600;

    /* RESOLUTION METRIC DEFAULTS */
    /**
     * The data is more dense than what can be displayed on screen.
     */
    public static final double RES_DENSE = 0.5;

    /**
     * 1:1 - one pixel of the original content is one pixel on screen
     */
    public static final double RES_PIXEL = 1.0;

    /**
     * sparse - The data is being streched past intended use.
     */
    public static final double RES_SPARSE = 42.0;

    /**
     * An identifier associated with the render metrics.
     *
     * <p>
     * This id will be assigned by the RendererExtensionProcessor when the render metrics are
     * processed.
     *
     * <p>
     * It is currently used by the render metrics to override the renderer choice by storing this id
     * on the blackboard to force the renderer to be chosen.
     *
     *
     */
    private String id;

    /**
     * Create New instance
     *
     * @param context context to use for determining the metrics of the associated renderer
     * @param factory the factory associated with this metrics.
     * @param expectedStyleIds list of expected style Ids.
     */
    protected AbstractRenderMetrics(final IRenderContext context,
            final IRenderMetricsFactory factory, final List<String> expectedStyleIds) {
        this.context = context;
        this.factory = factory;
        this.expectedStyleIDs = expectedStyleIds;
    }

    /**
     * Creates a new Renderer that is represented by this Metrics object
     *
     * @return a new Renderer that is represented by this Metrics object
     */
    public abstract Renderer createRenderer();

    /**
     * True if renderer use the provided style information.
     * <p>
     * The style information should be considered with respect to the current getRenderContext().
     * </p>
     *
     * @param SyleID
     * @param value
     * @return true if renderer can use the provided style information
     */
    public abstract boolean canStyle(String styleID, Object value);

    /**
     * Check to see if this layers can be added to rendered.
     * <p>
     * This is only called for renderers that are MultiLayer renderers; implementors should check
     * the layer and see if it can be combined into a single rendercontext.
     * </p>
     */
    public boolean canAddLayer(ILayer layer) {
        return false;
    }

    /**
     * @return the RenderContext that this RenderMetrics is valid for.
     * @see IRenderContext
     */
    public IRenderContext getRenderContext() {
        return context;
    }

    /**
     * Returns the IRenderMetricsFactory that created this object.
     *
     * @return the IRenderMetricsFactory that created this object.
     * @see IRenderMetricsFactory
     */
    public IRenderMetricsFactory getRenderMetricsFactory() {
        return factory;
    }

    /**
     * Returns a set of valid ranges of <strong>doubles</strong> that indicates the scales that the
     * layer is valid. This is based on restrictions inherent in the IGeoResource. For example WMS
     * layers have a valid min and max scale. Other parameters such as the style can also affect the
     * range of valid scales.
     * <p>
     * The value of a RenderMetrics with a scale that is within the Viewport's scale will be
     * increased. an empty set is return then this will be disregarded
     *
     * No restriction on scale by default - override to provide one or more valid scale ranges for
     * this renderer.
     * </p>
     *
     * @return a set of valid ranges of <strong>doubles</strong>that indicates the scales that the
     *         layer is valid
     */
    public Set<Range<Double>> getValidScaleRanges() {
        return new HashSet<>();
    }

    /**
     * Returns a number that represents the expected latency of connecting to the Measured in
     * milliseconds.
     * <p>
     * This will be used by the RenderCreator to determine which renderer to use for a given
     * georesource.
     * </p>
     *
     * For defaults: use one of LATENCY_MEMROY, LATENCY_MEMORY_CACHE, LATENCY_LOCAL,
     * LATENCY_LOCAL_CACHE, LATENCY_NETWORK, LATENCY_NETWORK_CACHE
     *
     * @return the default is LATENCY_LOCAL
     */
    public long getLatencyMetric() {
        return latencyMetric;
    }

    /**
     * Returns a number the represents the expected time to draw of the georesouce. The time to draw
     * is the difference between the time when drawing started and the time when drawing completed.
     * It should not include the latency between when the request is made and drawing can start.
     * Measured in milliseconds.
     *
     * <p>
     * This will be used by the RenderCreator to determine which renderer to use for a given
     * georesouce.
     * </p>
     *
     * @return
     */
    public long getDrawingTimeMetric() {
        return this.timeToDrawMetric;
    }

    /**
     * This represents a ratio of source pixels to screen pixels.
     * <p>
     * This number is used to represents how must distortion will be shown to the user when this
     * renderer is used to drawn on the screen.
     * <p>
     * This will be used by the RenderCreator to determine which renderer to use for a given
     * georesouce.
     * </p>
     *
     * @return
     */
    public double getResolutionMetric() {
        return this.resolutionMetric;
    }

    /**
     * Computes a "renderer" appearance metric identifying how well a renderer can style the
     * resource given the styles on the blackboard.
     *
     * <p>
     * Based on the number of styles on the blackboard that the renderer can use divided by the
     * number styles the renderer users.
     * </p>
     *
     * <p>
     * Makes use of canStyle(String, Object) to ensure the renderer can use the style on the
     * blackboard.
     * </p>
     *
     *
     * @return How well the renderers needs are fufilled by this style blackboard
     */
    public double getRenderAppearanceMetric(IStyleBlackboard blackboard) {
        int numberStylesICanUse = 0;
        for (String styleID : blackboard.keySet()) {
            Object style = blackboard.get(styleID);
            if (this.expectedStyleIDs.contains(styleID) && canStyle(styleID, style)) {
                numberStylesICanUse++;
            }
        }

        double renderMetric = 1.0;
        if (!expectedStyleIDs.isEmpty()) {
            renderMetric = (double) numberStylesICanUse / (double) expectedStyleIDs.size();
        }

        return renderMetric;
    }

    /**
     * Computes a "user" appearance metric identifying how well a renderer can style the resource
     * given the styles on the blackboard.
     *
     * <p>
     * Based on the number of styles on the blackboard that the renderer can use divided by the
     * number of styles on the blackboard (provided by the user).
     * </p>
     *
     * <p>
     * Makes use of canStyle(String, Object) to ensure the renderer can use the style on the
     * blackboard.
     * </p>
     *
     * @return How well the users wishes (ie style blackboard) are respected by this renderer
     */
    public double getUserAppearanceMetric(IStyleBlackboard blackboard) {
        int numberStylesICanUse = 0;
        for (String styleID : blackboard.keySet()) {
            Object style = blackboard.get(styleID);
            if (this.expectedStyleIDs.contains(styleID) && canStyle(styleID, style)) {
                numberStylesICanUse++;
            }
        }

        double userMetric = 1;
        if (!blackboard.keySet().isEmpty()) {
            userMetric = (double) numberStylesICanUse / (double) blackboard.keySet().size();
        }
        return userMetric;
    }

    /**
     * The list of expected Style IDs.
     * <p>
     * The list of expected style IDs is used to determine how wel the current style blackboard
     * meets your needs:
     * <ul>
     * <li>Are all the styles you need available? Use getRenderAppearanceMetric() to learn more.
     * <li>Are all the styles provided the user going to be respected? Use getUserAppearanceMetric()
     * to learn more.
     * </ul>
     * Developers please note that you must pass in the list of expected style in to the
     * constructor.
     *
     * @return List of Style IDs expected by the current renderer
     */
    public final List<String> getExpectedStyles() {
        return this.expectedStyleIDs;
    }

    /**
     * Gets the id associated with the render metrics.
     *
     * @return
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the id associated with the render metrics.
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, latencyMetric, resolutionMetric, timeToDrawMetric);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractRenderMetrics other = (AbstractRenderMetrics) obj;
        return Objects.equals(context, other.context) && latencyMetric == other.latencyMetric
                && Double.doubleToLongBits(resolutionMetric) == Double
                        .doubleToLongBits(other.resolutionMetric)
                && timeToDrawMetric == other.timeToDrawMetric;
    }
}
