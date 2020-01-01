/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.render;

import java.awt.image.RenderedImage;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

import org.geotools.geometry.jts.ReferencedEnvelope;

import org.locationtech.jts.geom.Envelope;

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
     * the Filter.EXCLUDE is set as a filter to the layer and the old selection
     * should be cleared.
     * 
     * @param layer the layer whose selection should be cleared
     */
    public void clearSelection(ILayer layer);
    
    /**
     * Forces layer to be re-rendered. Depending on the renderer the entire viewport may be
     * re-rendered.
     * <p>
     * If layer is <code>null</code>no layers will be rendered.
     * </p>
     * The provided bounds are either:
     * <ul>
     * <li>Envelope: Assumed to be in the Map CRS, ie getContext().getCRS() which maps to the viewport model geCRS()
     * <li>Referenced Envelope: crs may not be null
     * </ul>
     * @param layer the layer that will be re-rendered.
     * @param bounds the area the re-render, either a referenced envelope or assumed to be in the map CRS
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
     * <p>The image is from the context
     *  associated with the render executor.
     * </p>
     * 
     * @return Returns the most recently rendered Image.  May return null if the none of the refresh() methods
     * has been called previously.
     */
    public RenderedImage getImage();
    
    /**
     * 
     * Returns true if these two layers are related in any way.  Two layers are related if:
     * <ul>
     *   <li>They are the same (layer = contained)
     *   <li>Layer is a part of a composite context and contained is also part of that context.</li>
     * </ul>
     *
     * @param layer
     * @param contained
     * 
     * @returns true if the two layers are part of the same context
     */
    public boolean areLayersRelatedByContext(ILayer layer, ILayer contained);

    /**
     * Returns a list of tiles matching the given collection of bounds.  If bounds is null 
     * then the bounds of the viewport are used and calculated into tiles.
     * 
     * @return Returns list of tiles matching given bounds.  May return null if the given
	 * bounds are somehow invalid or empty.
     */
    public Map<ReferencedEnvelope, Tile> getTiles(Collection<ReferencedEnvelope> bounds);
    
    /**
     * Computes the tiles associated with a bounds and resolution.  If a renderer doesn't
     * support tiles, then this should return null.
     *
     * @param viewBounds                bounds to find tiles for
     * @param worldunitsperpixel        resolution
     * 
     * @return  Collection of referenced envelopes that represent the tiles in the bounds
     */
    public Collection<ReferencedEnvelope> computeTileBounds(ReferencedEnvelope viewBounds, double worldunitsperpixel);
    
}
