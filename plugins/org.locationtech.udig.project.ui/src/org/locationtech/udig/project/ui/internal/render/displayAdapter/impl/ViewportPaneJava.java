/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.render.displayAdapter.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.VolatileImage;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelListener;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.render.glass.GlassPane;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.graphics.AWTGraphics;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * The ViewportPaneImpl is a java.awt.Panel that is the display area for a Map. It Registers itself
 * with a CompositeRenderer and obtains the image from the CompositeRenderer if the
 * CompositeRenderer is "ready"
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ViewportPaneJava extends Panel implements ViewportPane {
    ViewportPainter painter = new ViewportPainter(this);
    private static final long serialVersionUID = 1L;
	private static final int DEFAULT_DPI = 72;    
	ViewportPane pane = this;
    private final static AffineTransform IDENTITY = new AffineTransform();
    private VolatileImage vImage;
    VolatileImage buffer;
    private Composite composite = null;
    EventJob eventJob = new EventJob();
    private RenderManager renderManager;
    private EventHandler handler;
    private MapPart editor;

    /**
     * The glass pane associated with the viewport pane.
     * Allows direct drawing on the image (similar to 
     * draw commands).
     */
    private GlassPane glass;
    
    /**
     * Create a image that is compatable with this ViewportPane.
     * <p>
     * This image is expected to be a "Managed Image" and thus hardware accelarated.
     * </p>
     * 
     * @see ViewportPane#image(int, int)
     * @param w width
     * @param h height
     * @return BufferedImage directly backed by AWT
     */
    public BufferedImage image( int w, int h ) {
        return (BufferedImage) createImage(w, h);
    }
    /**
     * Creates a new ViewportPaneImpl object.
     * 
     * @param comp The Composite that this pane will be embedded into
     * @param editor editor that contains this viewport
     */
    public ViewportPaneJava( Composite comp, MapPart editor ) {
        this.editor = editor;
        handler = new EventHandler(this, eventJob);
        
        comp.addListener(SWT.MouseDoubleClick, handler);
        comp.addListener(SWT.MouseDown, handler);
        comp.addListener(SWT.MouseEnter, handler);
        comp.addListener(SWT.MouseExit, handler);
        comp.addListener(SWT.MouseHover, handler);
        comp.addListener(SWT.MouseMove, handler);
        comp.addListener(SWT.MouseUp, handler);
        comp.addListener(SWT.MouseWheel, handler);
        comp.addListener(SWT.Resize, handler);
        comp.addListener(SWT.KeyDown, handler);
        
        Composite child = new Composite(comp, SWT.EMBEDDED | SWT.NO_BACKGROUND );
        child.setEnabled(false);
        SWT_AWT.new_Frame(child).add(this);
        composite = child;
        setBackgroundColor();
    }

    private void setBackgroundColor() {
        composite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        setBackground(Color.WHITE);
    }

    /**
     * @see org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane#setRenderManager(org.locationtech.udig.project.render.RenderManager)
     */
    public void setRenderManager( RenderManager manager ) {
        this.renderManager = manager;
    }

    /**
     * @see java.awt.Canvas#paint(java.awt.Graphics)
     */
    public void paint( Graphics g ) {
        initializeViewportModel();
        if (vImage == null) {
            clearDisplay(g);
            return;
        }

        initializeBuffer();
        do {
            drawOnOffScreenBuffer();

            drawOnScreen(g);
        } while( buffer.contentsLost() );
    }

    private void drawOnScreen( Graphics g ) {
        int minWidth = Math.min(buffer.getWidth(), g.getClipBounds().width);
        int minHeight = Math.min(buffer.getHeight(), g.getClipBounds().height);
        g.drawImage(buffer, 0, 0, minWidth, minHeight, 0, 0, minWidth, minHeight, this);
    }

    private void drawOnOffScreenBuffer() {

        Graphics2D graphics = buffer.createGraphics();
//        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ViewportGraphics vg = new AWTGraphics(graphics);
        do {
            int minWidth = Math.min(buffer.getWidth(), getWidth());
            int minHeight = Math.min(buffer.getHeight(), getHeight());
            validateVolitileImage();
            painter.paint(vg, vImage, minWidth, minHeight);
        } while( vImage.contentsLost() );
    }

    private void validateVolitileImage() {
        int returnCode;
        returnCode = vImage.validate(getGraphicsConfiguration());
        if (returnCode == VolatileImage.IMAGE_RESTORED) {
            // Contents need to be restored
            copyImageToVolitileImage(false); // restore contents
        } else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
            // old vImg doesn't work with new GraphicsConfig; re-create
            // it
            vImage = createVolatileImage(getWidth(), getHeight());
            copyImageToVolitileImage(false);
        }
    }

    private void initializeBuffer() {
        if (buffer == null || buffer.getWidth() < getWidth() || buffer.getHeight() < getHeight())
            buffer = createVolatileImage(getWidth(), getHeight());
    }

    private void clearDisplay( Graphics g ) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.clearRect(0, 0, getWidth(), getHeight());
        return;
    }

    private void initializeViewportModel() {
        if (renderManager != null && !renderManager.getViewportModelInternal().isInitialized()) {
            Event event = new Event();
            event.display=Display.getDefault();
            // eventJob.fire( EventJob.RESIZED, new MapDisplayEvent(this, new Dimension(0,0),
            // getSize()) );
            handler.controlResized(event);
        }

    }

    void copyImageToVolitileImage( boolean clearImage ) {
        if (vImage == null)
            return;
        do {
            if (vImage.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
                // old vImg doesn't work with new GraphicsConfig; re-create it
                vImage = createVolatileImage(getWidth(), getHeight());
            }
            Graphics2D graphics = vImage.createGraphics();
            if (clearImage)
                graphics.clearRect(0, 0, vImage.getWidth(), vImage.getHeight());
            graphics.drawRenderedImage(getImage(), IDENTITY);
            graphics.dispose();
        } while( vImage.contentsLost() );
    }

    private void initMap() {
        if (getImage() == null) {
            return;
        }
        if (vImage == null || vImage.getWidth() < getWidth() || vImage.getHeight() < getHeight()) {
            vImage = createVolatileImage(getWidth(), getHeight());
        }
        copyImageToVolitileImage(true);
    }

    RenderedImage getImage() {
        if (!Platform.isRunning())
            return null;
        try {
            RenderExecutor renderExecutor = renderManager.getRenderExecutor();
            Renderer renderer = renderExecutor.getRenderer();
            if (renderer.getState() == IRenderer.DISPOSED)
                return null;
            IRenderContext context = renderer.getContext();
            return context.getImage();
        } catch (Throwable e) {
            ProjectUIPlugin.log(null, e);
            return null;
        }
    }

    /**
     * @see java.awt.Component#update(java.awt.Graphics)
     */
    public void update( Graphics g ) {
        paint(g);
    }

    public void update(){
        //do nothing
    }
    
    /**
     * @see ViewportPane#dispose()
     */
    public void dispose() {
        composite.dispose();
    }

    /**
     * @see ViewportPane#addDrawCommand(IDrawCommand)
     */
    public void addDrawCommand( IDrawCommand command ) {
        painter.addDrawCommand(command);
    }

    /**
     * @see org.locationtech.udig.project.render.displayAdapter.IMapDisplay#getDisplaySize()
     * @return the size of the viewportpane
     */
    public Dimension getDisplaySize() {
        return getSize();
    }

    /**
     * @see ViewportPane#removeMouseListener(MapMouseListener)
     */
    public void removeMouseListener( MapMouseListener l ) {
        eventJob.removeMouseListener(l);
    }
    /**
     * @see ViewportPane#removeMouseMotionListener(MapMouseMotionListener)
     */
    public void removeMouseMotionListener( MapMouseMotionListener l ) {
        eventJob.removeMouseMotionListener(l);
    }
    /**
     * @see ViewportPane#removeMouseWheelListener(MapMouseWheelListener)
     */
    public void removeMouseWheelListener( MapMouseWheelListener l ) {
        eventJob.removeMouseWheelListener(l);
    }
    /**
     * @see ViewportPane#addMouseListener(MapMouseListener)
     */
    public void addMouseListener( MapMouseListener l ) {
        eventJob.addMouseListener(l);
    }
    /**
     * @see ViewportPane#addMouseMotionListener(MapMouseMotionListener)
     */
    public void addMouseMotionListener( MapMouseMotionListener l ) {
        eventJob.addMouseMotionListener(l);
    }
    /**
     * @see ViewportPane#addMouseWheelListener(MapMouseWheelListener)
     */
    public void addMouseWheelListener( MapMouseWheelListener l ) {
        eventJob.addMouseWheelListener(l);
    }

    /**
     * @see org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane#renderStarting()
     */
    public void renderStarting() {
        painter.renderStart();
        repaint();
    }
    /**
     * @see org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane#renderDone()
     */
    public void renderDone() {
        renderUpdate();
        painter.renderDone();
    }

    /**
     * @see org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane#renderUpdate()
     */
    public void renderUpdate() {
        initMap();
        if (getImage() != null) {
            painter.renderUpdate();
            repaint();
        }
    }

    /**
     * @see org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane#addPaneListener(org.locationtech.udig.project.render.displayAdapter.MapDisplayListener)
     */
    public void addPaneListener( IMapDisplayListener listener ) {
        eventJob.addMapEditorListener(listener);
    }
    /**
     * @see org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane#removePaneListener(org.locationtech.udig.project.render.displayAdapter.MapDisplayListener)
     */
    public void removePaneListener( IMapDisplayListener listener ) {
        eventJob.removeMapEditorListener(listener);
    }
    /**
     * @see org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane#setCursor(org.eclipse.swt.graphics.Cursor)
     */
    public void setCursor( final Cursor cursor ) {
    	PlatformGIS.asyncInDisplayThread(new Runnable() {
			public void run() {
				composite.getParent().setCursor(cursor);
			}
		}, true);
    }

    /**
     * @see org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane#getMapEditor()
     */
    public MapPart getMapEditor() {
        return editor;
    }
	public int getDPI() {
		return DEFAULT_DPI;
	}
	public Control getControl() {
		return composite;
	}

    @Override
    public void repaint() {
        super.repaint();
    }
    
	public void enableDrawCommands(boolean enable) {
		painter.switchOnOff(enable);
	}
    public boolean isDisposed() {
        return composite.isDisposed();
    }
    
    /**
     * Gets the GlassPane.
     * <p>
     * Will return null if no glass pane set.
     * </p>
     * 
     * @return the GlassPane if set; or null if no GlassPane set
     */
    public GlassPane getGlass() {
        return this.glass;
    }

    /**
     * Sets the GlassPane
     * 
     * @param g
     */
    public void setGlass( GlassPane glass ) {
        this.glass = glass;
    }
}
