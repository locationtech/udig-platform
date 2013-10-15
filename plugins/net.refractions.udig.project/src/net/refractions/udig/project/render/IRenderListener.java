/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.render;

import java.awt.Rectangle;
import java.util.List;

import net.refractions.udig.project.internal.render.RenderingCoordinator;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * A listener that listeners for the current state of rendering.  
 * <p>
 * An {@link IRenderListener} is notified when rendering starts, finishes,
 * when {@link IMapDisplay} is updated and when renderers are added or removed.
 * </p> 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IRenderListener {
    /**
     * Called when a rendering starts.
     *
     * @param source RenderCoordinator that raised the event.
     * @param worldBounds the area that is being rendered, in map coordinates.
     * @param screenBounds the area that to being rendered, on screen.
     * @param renderers the renderers that are being rendered.
     */
    void renderStarted(RenderingCoordinator source, ReferencedEnvelope worldBounds, 
            Rectangle screenBounds, List<IRenderer> renderers);
    /**
     * Called when a rendering is finished.
     *
     * @param source RenderCoordinator that raised the event.
     * @param worldBounds the area that was rendered, in map coordinates.
     * @param screenBounds the area that was rendered, on screen.
     * @param renderers the renderers that did the rendering.
     */
    void renderEnded(RenderingCoordinator source, ReferencedEnvelope updatedMap, 
            Rectangle updatedScreen, List<IRenderer> renderers);
    /**
     * Called when there is information ready.  The MapDisplay should update when this is called
     *
     * @param source RenderCoordinator that raised the event.
     * @param worldBounds the area that has been updated, in map coordinates.
     * @param screenBounds the area that has been updated, in screen coordinates.
     * @param renderers the renderers that did the rendering.
     */
    void updateReady(RenderingCoordinator source, ReferencedEnvelope updatedMap, 
            Rectangle updatedScreen, List<IRenderer> renderers);
    /**
     * Called when a renderer has been added to the render coordinator
     *
     * @param source  RenderCoordinator that raised the event.
     * @param renderers Renderers that were added
     */
    void RendererAdded(RenderingCoordinator source, List<IRenderer> renderers);
    /**
     * Called when a renderer has been removed from the render coordinator
     *
     * @param source  RenderCoordinator that raised the event.
     * @param renderers Renderers that were added
     */
    void RendererRemoved(RenderingCoordinator source, List<IRenderer> renderers);
}
