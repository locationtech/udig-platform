/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.render;

import java.io.IOException;

/**
 * The RenderMetricsFactory is used by the RenderManager (more specifically the
 * RenderCreationDecisive) to obtain metrics on renderer extensions. The RenderMetrics objects are
 * used to decide which renderers to instantiate to render a layer. As the state of udig changes
 * different renderers may be more suited so the RenderMetrics object are also used to judge whether
 * a new renderer should be used for rendering a layer.
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public interface IRenderMetricsFactory {

    /**
     * This method is used to determine whether the renderer can render the layer. It is used so
     * that not all metrics need to be created.
     * 
     * @param toolkit A toolkit containing reference to everything a renderer needs.
     * @return true if the associated renderer can render the layer using the data provided by data.
     * @throws IOException
     * @see IRenderContext
     */
    public boolean canRender( IRenderContext context ) throws IOException;

    /**
     * Returns a RenderMetrics object which provides metrics for a renderer while rendering
     * <code>layer</code> using <code>data</code> as the data source and rendering to a viewport
     * modeled by <code>vmodel</code>. NOTE: These metrics is a active object and cached by the
     * RenderManager. As such no references should be maintained to the objects returned by this
     * method.
     * 
     * @param toolkit A toolkit containing reference to everything a renderer needs.
     * @return A RenderMetrics object which provides metrics for a renderer while rendering
     *         <code>layer</code> using <code>data</code> as the data source and rendering to a
     *         viewport modeled by <code>vmodel</code>.
     * @see IRenderMetrics
     * @see IRenderContext
     */

    public AbstractRenderMetrics createMetrics( IRenderContext context );

    /**
     * Returns the type of the Renderer that will be created.
     * <p>
     * Note if this is a MultiLayerRenderer the metrics values will be weighted depending on how
     * many adjacent layers can be rendered by the renderer for the current map.
     * </p>
     * Please return the class that this RenderMetricsFactory will create (if given
     * the appropriate context).
     * 
     * @return the type of the Renderer that will be created
     */
    public Class< ? extends IRenderer> getRendererType();
}
