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
package org.locationtech.udig.project.ui.render.displayAdapter;

import java.awt.image.BufferedImage;

import org.eclipse.swt.widgets.Control;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.render.glass.GlassPane;

/**
 * The ViewportPane is the display area for a Map.
 * <p>
 * The default implementation is a java.awt.Pane. It Registers itself with a CompositeRenderer and
 * obtains the image from the CompositeRenderer if the CompositeRenderer is "ready"
 * </p>
 * <p>
 * <ul>
 * <li>Models: CompositeRenderer and Viewport
 * <li>View: ViewportPane
 * <li>Control: RenderManager
 * </ul>
 * <p>
 * Viewport is responsible for:
 * <ul>
 * <li>Image Creation for CompositeRenderer an renders
 * <li>Listening to redraw requests from CompositeRenderer
 * <li>Isolating everyone from SWT / AWT graphics pipeline
 * </ul>
 * 
 * @author jeichar
 * @since 0.1
 */
public interface ViewportPane extends IMapDisplay {

    /**
     * Create a image that is compatable with this ViewportPane.
     * 
     * @param w width
     * @param h height
     * @return BufferedImage (may be hardware accelarated)
     */
    BufferedImage image( int w, int h );

    /**
     * Repaints the ViewportPane
     * <p>
     * Requests a repaint actually - may not occur right away.
     * </p>
     */
    public void repaint();

    /**
     * Requests that the area indicated is repainted
     * <p>
     * Requests a repaint actually - may not occur right away.
     * </p>
     * @param x x coordinate of the top left corner of the box to repaint 
     * @param y y coordinate of the top left corner of the box to repaint 
     * @param width width of the box to repaint
     * @param height height of the box to repaint
     */
    public void repaint(int x, int y, int width, int height);

    
    /**
     * This function is used to force paint events for a control.  See the Canvas.update() method.
     * <p>
     * Forces all oustanding paint events for this control to be delivered before returning.  
     * The children of the control are unaffected.  Only paint events are dispatched.
     * </p> 
     * <p>
     * Using update is generally unnecessary and can cause flickering and
     * performance problems.  This is true because update() defats the merging of paint
     * events implemented by the operating system.
     * </p>
     * <p>This function is currently being used by the Pan tool to try to reduce the number
     * of incorrectly positioned images drawn on the screen.  (To ensure all paint events are processed
     * before we invalidate the transform and move the image).
     * </p>
     * 
     *    
     */
    public void update();
    
    /**
     * Sets the mouse cursor.
     * <p>
     * Limited to System cursors at the moment. Custom cursors will be forthcoming.
     * </p>
     * 
     * @param cursor The cursor to use
     */
    public void setCursor( org.eclipse.swt.graphics.Cursor cursor );

    /**
     * See {@linkplain java.awt.Component#removeMouseListener(java.awt.event.MouseListener)}
     * 
     * @param l A mouse listener
     */
    public void removeMouseListener( MapMouseListener l );

    /**
     * See
     * {@linkplain java.awt.Component#removeMouseMotionListener(java.awt.event.MouseMotionListener)}
     * 
     * @param l A mouse listener
     */
    public void removeMouseMotionListener( MapMouseMotionListener l );

    /**
     * See
     * {@linkplain java.awt.Component#removeMouseWheelListener(java.awt.event.MouseWheelListener)}
     * 
     * @param l A mouse listener
     */
    public void removeMouseWheelListener( MapMouseWheelListener l );

    /**
     * See {@linkplain java.awt.Component#addMouseListener(java.awt.event.MouseListener)}
     * 
     * @param l A mouse listener
     */
    public void addMouseListener( MapMouseListener l );

    /**
     * See
     * {@linkplain java.awt.Component#addMouseMotionListener(java.awt.event.MouseMotionListener)}
     * 
     * @param l A mouse listener
     */
    public void addMouseMotionListener( MapMouseMotionListener l );

    /**
     * See {@linkplain java.awt.Component#addMouseWheelListener(java.awt.event.MouseWheelListener)}
     * 
     * @param l A mouse listener
     */
    public void addMouseWheelListener( MapMouseWheelListener l );

    /**
     * Adds a listener interested in size changes to the pane
     */
    public void addPaneListener( IMapDisplayListener listener );
    /**
     * Removes a MapDisplayListener.
     */
    public void removePaneListener( IMapDisplayListener listener );

    /**
     * disposes of any system resources
     */
    public void dispose();

    /**
     * Adds a Draw command to the list of draw commands.  This will not refresh
     * the ViewportPane.
     * 
     * @param command The new draw command.
     * @model
     */
    public void addDrawCommand( IDrawCommand command );
    
    
    /**
     * Switches <code>ViewportPainter</code> to run custom <code>IDrawCommand</code>s
     * during map repainting or disable them.
     * 
     * @param enable <code>true</code> is to run custom draw commands, <code>false</code> otherwise
     */
    public void enableDrawCommands(boolean enable);
    
    
    /**
     * Called when rendering is about to start.
     */
    public void renderStarting();

    /**
     * Called when renderer has rendered for single Job scheduled run.
     */
    public void renderUpdate();

    /**
     * Called when a renderer has data that can be displayed.
     * <p>
     * May be called when a refresh is required because the rendered data has changed
     * </p>
     */
    public void renderDone();

    /**
     * Called to set the owning renderManager()
     */
    void setRenderManager( RenderManager manager );

    /**
     * Returns the associated MapEditor object.
     * 
     * @return the associated MapEditor object.
     */
    MapPart getMapEditor();

    /**
     * Returns the SWT Control that listeners, including drag and drop, can be added to.
     * @return  the SWT Control that listeners, including drag and drop, can be added to.
     */
	Control getControl();

    /**
     * Returns true if the viewportPane component is visible.
     *
     * @return true if the viewportPane component is visible.
     */
    boolean isVisible();
    
    /**
     * returns if the viewportpane has been disposed
     *
     * @return true if the viewportpane has been disposed
     */
    boolean isDisposed();

    /**
     * Returns the glass pane.  The GlassPane contains a draw
     * function that can be used to draw directly on the image.
     *
     * @return glass pane if set; null if not set
     */
    GlassPane getGlass();
    
    /**
     * Sets the glass Pane
     *
     */
    void setGlass(GlassPane glass);
}
