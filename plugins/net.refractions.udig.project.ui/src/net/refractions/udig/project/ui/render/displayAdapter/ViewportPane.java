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
package net.refractions.udig.project.ui.render.displayAdapter;

import java.awt.image.BufferedImage;

import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.project.render.displayAdapter.IMapDisplayListener;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.project.ui.internal.MapPart;

import org.eclipse.swt.widgets.Control;

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

}
