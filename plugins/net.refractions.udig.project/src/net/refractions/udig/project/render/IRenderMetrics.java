/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.render;

import java.util.Set;

import javax.media.jai.util.Range;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;

/**
 * A RenderMetrics object is used to calculate estimated metrics for a renderer while rendering a
 * particular layer. RenderMetrics objects are used to decide which renderer to instantiate to
 * render a layer. As the state of udig changes different renderers may be more suited so the
 * RenderMetrics object are also used to judge whether a new renderer should be used for rendering a
 * layer.
 *
 * @see AbstractRenderMetrics
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public interface IRenderMetrics {
    /**
     * Creates a new Renderer that is represented by this Metrics object
     *
     * @return a new Renderer that is represented by this Metrics object
     */
    public Renderer createRenderer();

    /**
     * @return the RenderContext that this RenderMetrics is valid for.
     * @see IRenderContext
     */
    public IRenderContext getRenderContext();
    /**
     * Returns the IRenderMetricsFactory that created this object.
     *
     * @return the IRenderMetricsFactory that created this object.
     * @see IRenderMetricsFactory
     */
    public IRenderMetricsFactory getRenderMetricsFactory();

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
    public boolean canStyle( String styleID, Object value );

    /**
     * Returns a set of valid ranges of <strong>doubles</strong> that indicates the scales that the layer is valid. This is
     * based on restrictions inherent in the IGeoResource. For example WMS layers have a valid min
     * and max scale. Other parameters such as the style can also affect the range of valid scales.
     * <p>
     * The value of a RenderMetrics with a scale that is within the Viewport's scale will be
     * increased. an empty set is return then this will be disregarded
     * </p>
     *
     * @return a set of valid ranges of <strong>doubles</strong>that indicates the scales that the layer is valid
     */
    public Set<Range> getValidScaleRanges();

    /**
     * Returns true if this renderer is built specifically to render the GeoResource.
     *
     * @return true if renderer is optimized to render
     */
    public boolean isOptimized();

    /**
     * Check to see if this layers can be added to rendered.
     * <p>
     * This is only called for renderers that are MultiLayer renderers.
     * </p>
     */
    public boolean canAddLayer( ILayer layer );
}
