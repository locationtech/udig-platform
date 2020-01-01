/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.render;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.eclipse.core.runtime.IProgressMonitor;

import org.locationtech.jts.geom.Envelope;

/**
 * Responsible for rendering a layer using AWT Graphics2D - this class is
 * assigned a context with a specific GeoResource to use as a source of data.
 * <p>
 * A renderer has the following responsibilities to the rendering system:
 * <ul>
 * <li>A renderer must draw to the image provided by it RenderContext</li>
 * <li>A renderer reprojects victim data if required</li>
 * <li>A renderer applies styles if necessary</li>
 * </ul>
 * The RenderContext contains many utility methods to help smooth the
 * implementation of this required functionality.
 * <p>
 * The IRenderer implementation also knows the most about the data being
 * displayed - as such it has the following responsibilities:
 * <ul>
 * <li>A renderer is responsible for registering with its layers to obtain notifications when the
 * layers' styles change<br>
 * (This responsibility may be revised in the face of IRenderMetrics ability to evaulate the style blackboard)</li>
 * <li>The renderer must notify its adapters(ie listeners) when its rendering
 * state changes - failure to do this will result in the screen not
 * refreshing correctly!
 * <br>(The easiest way to meet this responsibility is to extend RendererImpl)</li>
 * <li>A renderer must call setState() method when its render state changes or it wishes to notify its listeners
 * that it needs to be redrawn.
 * <br>(The easiest way to do this is to extend RenderImpl)
 * <p>
 * The state that the renderer can use are the following:
 * </p>
 * <ul>
 * <li>RENDERING - should be called when the renderer has data that it wishes to display on the
 * screen. May be ignored if the executor uses a timer for refreshing rather than waiting for
 * updates from the renderers</li>
 * <li>DONE - Should be set when rendering is finished.</li>
 * <li>RENDER_REQUEST - should be called when the renderer has received events indicating that it
 * needs to be rerendered. The event listeners should <b>NEVER </b> call render(), instead
 * setState(RENDER_REQUEST) should be used and the frame work will trigger a rerender, most-likely
 * in a separate thread.</li>
 * </ul>
 * </li>
 * <li>If possible a renderer is responsible for registering with its data source, not layer, so it
 * can receive notices of when the data changes. If another method of recieving update is required
 * then the renderer is held responsible.</li>
 * </ul>
 * 
 * @author jeichar
 * @since 0.1
 */
public interface IRenderer {

    /** The name of the Extension Point for Renderers */
    public static final String RENDER_EXT = "org.locationtech.udig.project.renderer"; //$NON-NLS-1$

    
    
    /** Indicates that the renderer has been reset and does not have anything to show */
    public static final int NEVER = 0x01;

    /** Indicates whether the renderer job is currently running */
    public static final int RENDERING = 0x01 << 1;

    /**
     * Indicates whether the renderer has finished rendering.
     * <p>
     * This implies that rendering has started and finished
     */
    public static final int DONE = 0x01 << 2;

    /** Indicates that the renderer has been disposed and can no longer be used */
    public static final int DISPOSED = 0x01 << 3;
    
    /** Indicates that the renderer has started rendering but does not have data to be displayed */
    public static final int STARTING = 0x01 << 5;
    
    /** Indicates that the renderer has been cancelled rendering */
    public static final int CANCELLED = 0x01 << 6;

    /**
     * When a renderer sets its state to RENDER_REQUEST that indicates that is needs to be
     * rerendered. The container of the renderer(usually a renderExecutor) will respond by calling
     * render. This is done so that the container can include run the renderer in a thread seperate
     * from the one that called setState().
     * <p> {@link #setRenderBounds(Envelope)} can be called to set the area that needs to be rendered}
     */
    public static final int RENDER_REQUEST = 0x01 << 4;
    
    /**
     * Returns the current state of rendering.
     * <p>
     * The state is the current state of the {@linkplain org.eclipse.core.runtime.jobs.Job}
     * </p>
     * Options are:
     * <ul>
     * <li>{@linkplain #RENDERING}</li>
     * <li>{@linkplain #DONE}</li>
     * <li>{@linkplain #NEVER}</li>
     * <li>{@linkplain #DISPOSED}</li>
     * </ul>
     * 
     * @return the current state of rendering.
     */
    public int getState();

    /**
     * Requests the Renderer to render with the graphics2d object
     * <p>
     * Since this method will be performing IO to access data it is expected to block; we ask
     * that any exception be wrapped up in an RenderException to communicate the problem
     * to the user. 
     * </p>
     * 
     * @param destination The objects that the Renderer will use for rendering
     * @throws RenderException
     */
    public void render( Graphics2D destination, IProgressMonitor monitor ) throws RenderException;

    /**
     * Ask the renderer to update the internal image using the smaller of getRenderBounds() or ViewportBounds.
     * 
     * <h2>Render State</h2>
     * 
     * Normally the RenderManager will update the screen every second.  However if the renderer has data that must be displayed then
     * it can call setState(RENDERING) and the Screen will be updated immediately.  This should be called with care however since many
     * such calls can cause performance problems.
     * 
     * <h2>Internal Image</h2>
     * 
     * The RenderContext maintains an "internal image" that you can access using getContext().getGraphics();
     * you can use Image.createGraphics() to retrive a Graphics2d to draw with.
     *
     * Example Implementation:<pre><code>
     * public void render( IProgressMonitor monitor ) throws RenderException {
     *     if( monitor == null ) monitor = NullProgressMonitor();
     *     Graphics2D g = getContext().getImage().createGraphics();
     *     render(g, monitor);
     * }   
     * </code></pre>
     * 
     * <h2>Updating a Portion of the Screen</h2>
     * 
     * When the envelope (ie getRenderBounds()) is smaller than the ViewportBounds, the rendered area
     * <b>DOES NOT TAKE UP THE WHOLE MapDisplay</b>. It only takes up the area that the envelope
     * would map to. The purpose of this functionality is to allows only a small area of the
     * MapDisplay to be refreshed rather than the whole area.
     * <p>
     * Please choose the smallest of:
     * <ul>
     * <li>getRenderBounds() - an Envelope in your CRS
     * <li>viewPortBounds()
     * </ul>
     * 
     * </p>
     * @see #getContext()
     * @see IRenderContext#getImage()
     * @see IRenderContext#getImage(int, int)
     *      <p>
     *      This method will block
     *      </p>
     *      <p>
     *      A value of null renders the bounds the entire viewport obtained from the
     *      ViewportModel.getBounds().
     *      </p>
     * @throws RenderException
     */
    public void render( IProgressMonitor monitor ) throws RenderException;

    /**
     * Returns the renderer's context object
     * 
     * @return the the renderer's context object
     * @see IRenderContext
     */
    public IRenderContext getContext();

    /**
     * Informs the renderer to dispose of resources
     */
    public void dispose();
    
    /**
     * Indicates whether the framework is permitted to cache the results of the renderer.
     * 
     * @return true if the framework may cache the resulting image and only request the new dirty
     * areas.
     */
    public boolean isCacheable();
    
    /**
     * Called to set the area that will be rendered.
     * <p>
     * The provided boundsToRenderer are assumed to be in getContext().getCRS() (
     * although you may be able to pass in a ReferencedEnevelope).
     */
    public void setRenderBounds(Envelope boundsToRender);
    
    /**
     * Gets the area that will be rendered next.
     * 
     * @return bounds to be drawn next 
     */
    public Envelope getRenderBounds();

    /**
     * Similar to render(Envelope) except the area is defined in screen coordinates. Performs the
     * screen to world transformation and calls render(Envelope).
     * 
     * @param screenArea the area of the screen to re-render.
     */
    public void setRenderBounds( Rectangle screenArea );

}
