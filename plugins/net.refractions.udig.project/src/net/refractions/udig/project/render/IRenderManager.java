/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.render;

import java.awt.image.RenderedImage;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Used by the map viewers/editors to manage the rendering process. Responsibilities:
 * <ul>
 * <li>Respond to model change events and start rendering processes depending on the event. For
 * example: If the Map CRS is changed then all layers must be rerendered with the new CRS.</li>
 * <li>Create Renderers when a map editor is opened.</li>
 * <li>Create and remove renderers when renderers are added or removed</li>
 * </ul>
 * </p>
 *
 * @author jeichar
 * @since 0.1
 */
public interface IRenderManager {

    /**
     * Returns the Map associated with the current renderManager.
     *
     * @return the Map associated with the current renderManager.
     */
    public IMap getMap();

    /**
     * Returns the root Renderer Executor.
     *
     * @return the root
     */
    List<IRenderer> getRenderers();

    /**
     * Gets the ViewportPane for the current RenderManager.
     *
     * @return the ViewportPane for the current RenderManager
     */
    public IMapDisplay getMapDisplay();

    /**
     * Forces the area in all layers to be re-rendered.
     * <p>
     * Depending on the renderer the entire viewport may be re-rendered.
     * </p>
     *
     * @param bounds the area to be re-rendered
     * @see #refresh(ILayer, Envelope)
     * @see #refreshSelection(ILayer, Envelope)
     */
    public void refresh( Envelope bounds );

    /**
     * Forces the selection in the area to be re-rendered.
     * <p>
     * Depending on the renderer the entire viewport may be re-rendered.
     * </p>
     * <p>
     * If layer is null no layers will be rendered.
     * </p>
     *
     * @param layer the layer whose selection layer should be rerendered
     * @param the area that will be rerendered
     * @see #refresh(Envelope)
     * @see #refreshSelection(ILayer, Envelope)
     */
    public void refreshSelection( ILayer layer, Envelope bounds );


    /**
     * Clears selection from the specified layer. It is used when
     * the Filter.ALL is set as a filter to the layer and the old selection
     * should be cleared.
     *
     * @param layer the layer whose selection should be cleared
     */
    public void clearSelection(ILayer layer);

    /**
     * Forces layer to be re-rendered. Depending on the renderer the entire viewport may be
     * re-rendered.
     * <p>
     * If layer is null no layers will be rendered.
     * </p>
     *
     * @param layer the layer that will be re-rendered.
     * @param bounds the area the re-render
     * @see #refresh(Envelope)
     * @see #refresh(ILayer, Envelope)
     */
    public void refresh( ILayer layer, Envelope bounds );

    /**
     * Stops the current rendering process if currently rendering.
     */
    public void stopRendering();

    /**
     * Returns the most recently rendered Image.
     *
     * @return Returns the most recently rendered Image.  May return null if the none of the refresh() methods
     * has been called previously.
     */
    public RenderedImage getImage();

}
