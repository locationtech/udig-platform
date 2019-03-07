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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.internal.Trace;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelListener;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.render.glass.GlassPane;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;
import org.locationtech.udig.ui.graphics.NonAdvancedSWTGraphics;
import org.locationtech.udig.ui.graphics.SWTGraphics;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

/**
 * The ViewportPaneImpl is a java.awt.Panel that is the display 
 * area for a Map. It Registers itself
 * with a RenderStack and obtains the image from the RenderStack 
 * if the RenderStack is "ready"
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ViewportPaneSWT extends Canvas implements ViewportPane {
    private final Repainter REPAINT = new Repainter();

    private static final long serialVersionUID = 1L;

    private ViewportPainter painter = new ViewportPainter(this);

    EventJob eventJob = new EventJob();

    private RenderManager renderManager;

    private EventHandler handler;

    private MapPart editor;

    Dimension displaySize = new Dimension(0, 0);

    private org.eclipse.swt.graphics.Image swtImage;

    private Display display;

    private volatile Object disposeMutex;

    private volatile Rectangle repaintRequest = null;
    private final Rectangle ZERO_RECTANGLE = new Rectangle(0, 0, 0, 0);

    private Object repaintRequestMutex = new Object();

    private Image buffer;

    private final int dpi;
    
    /**
     * The glass pane associated with the viewport pane.
     * Allows direct drawing on the image (similar to 
     * draw commands).
     */
    private GlassPane glass;

    /**
     * Create a image that is compatible with this ViewportPane.
     * <p>
     * This image is a large block of memory that will be blitted into an SWT Image. Currently this
     * is BufferedImage.TYPE_4BYTE_ABGR although you should never depend on the Image type directly.
     * </p>
     * <p>
     * This image is *not* expected to be hardware accelarated, althought the blit process will be.
     * </p>
     * 
     * @see org.locationtech.udig.project.render.ViewportPane#acquireImage(int, int)
     * @param w width
     * @param h height
     * @return BufferedImage with same color model as SWT Image.
     */
    public BufferedImage image( int w, int h ) {
        return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
    }

    public ViewportPaneSWT( Composite comp, MapPart editor ) {
        this(comp, SWT.NO_BACKGROUND|SWT.DOUBLE_BUFFERED, editor );
    }
    
    /**
     * Creates a new ViewportPaneImpl object.
     * 
     * @param comp The Composite that this pane will be embedded into
     * @param style recommended SWT.NO_BACKGROUND|SWT.DOUBLE_BUFFERED
     * @param renderStack The renderstack that is rendering onto this viewport
     * @param vmodel The Viewport model that models this viewport
     */
    public ViewportPaneSWT( Composite comp, int style, MapPart editor ) {
        super(comp, style);
        dpi=calculateDPI();
        ProjectUIPlugin.trace(Trace.VIEWPORT, getClass(), "ViewportPaneSWT created", null); //$NON-NLS-1$

        display = comp.getDisplay();
        this.editor = editor;

        addEventListeners();
    }
   
    private void addEventListeners() {

        handler = new EventHandler(this, eventJob);
        addListener(SWT.Resize, new Listener(){
            public void handleEvent( Event event ) {
                Point size = getSize();
                if (displaySize != null) {
                    if (displaySize.width == size.x && displaySize.height == size.y)
                        return;
                }
                displaySize = new Dimension(size.x, size.y);
                if (buffer != null)
                    buffer.dispose();
                if (size.x == 0 || size.y == 0)
                    buffer = null;
                else
                    buffer = new Image(display, size.x, size.y);
            }
        });
        addListener(SWT.MouseDoubleClick, handler);
        addListener(SWT.MouseDown, handler);
        addListener(SWT.MouseEnter, handler);
        addListener(SWT.MouseExit, handler);
        addListener(SWT.MouseHover, handler);
        addListener(SWT.MouseMove, handler);
        addListener(SWT.MouseUp, handler);
        addListener(SWT.MouseWheel, handler);
        addListener(SWT.Resize, handler);
        addListener(SWT.KeyDown, handler);
        
        addPaintListener(new PaintListener(){
            public void paintControl( PaintEvent event ) {
                paint(event.gc, event.display);
            }
        });
    }
    
    private int calculateDPI() {
        Point dpi = getDisplay().getDPI();
        if (dpi.x != dpi.y)
            return (dpi.x + dpi.y) / 2;
        else
            return dpi.x;

    }

    /**
     * @see org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane#setRenderManager(org.locationtech.udig.project.render.RenderManager)
     */
    public void setRenderManager( RenderManager manager ) {
        this.renderManager = manager;
    }

    /**
     * Called by the Paint listener to update the canvas
     * 
     * @param display the display of the canvas.
     * @param g the GC object to use to draw with.
     */
    public void paint( GC g, final Display display ) {
        synchronized (repaintRequestMutex) {
        	/*
        	 * Refactored for clarity.
        	 */
            if (repaintRequest != ZERO_RECTANGLE) {
            	if (g.getClipping() == null || repaintRequest == null) {
            		repaintRequest = ZERO_RECTANGLE;
            		
            	} else if (g.getClipping().equals(repaintRequest)) {
                    repaintRequest = ZERO_RECTANGLE;
                    
            	} else if (g.getClipping().width==0 && g.getClipping().height==0 ) {
                    repaintRequest = ZERO_RECTANGLE;
                    
            	} else if (clipContainsRepaintRequest(g.getClipping())) {
            		repaintRequest = ZERO_RECTANGLE;
            	}
            }
        }
        int antiAliasing;
        if( ProjectPlugin.getPlugin().getPreferenceStore().getBoolean(PreferenceConstants.P_ANTI_ALIASING) )
            antiAliasing=SWT.ON;
        else
            antiAliasing=SWT.OFF;
        g.setAntialias(antiAliasing);
        Image swtImage = getImage();
        
        int minHeight;
        int minWidth;
        if (swtImage == null) {
            minWidth = displaySize.width;
            minHeight = displaySize.height;
        } else {
            Rectangle bounds = swtImage.getBounds();
            minWidth = Math.min(bounds.width, getWidth());
            minHeight = Math.min(bounds.height, getHeight());
        }
        
        
        getDoubleBufferGraphics(display,g,minWidth, minHeight);


        synchronized (repaintRequestMutex) {
            if (repaintRequest != ZERO_RECTANGLE) {
                Rectangle rect = repaintRequest;
                repaintRequest = null;
                doRepaint(rect.x, rect.y, rect.width, rect.height);
            } else
                repaintRequest = null;
        }
    }

    private void getDoubleBufferGraphics( final Display display, GC gc, int minWidth, int minHeight ) {
    	IPreferenceStore store = UiPlugin.getDefault().getPreferenceStore();
    	boolean useAdvancedGraphics = store.getBoolean(org.locationtech.udig.ui.preferences.PreferenceConstants.P_ADVANCED_GRAPHICS); 
    	
        if ((getStyle()&SWT.DOUBLE_BUFFERED)==0){
            if (buffer == null) {
                buffer = new Image(display, displaySize.width, displaySize.height);
            }

            ViewportGraphics swtGraphics = null;
            
            if (useAdvancedGraphics) { 
            	swtGraphics = new SWTGraphics(buffer, display);
            } else {
            	swtGraphics = new NonAdvancedSWTGraphics(buffer, display);
            }

            painter.paint(swtGraphics, swtImage, minWidth, minHeight);
            swtGraphics.dispose();

            gc.drawImage(buffer, 0, 0);
        }else{

        	ViewportGraphics swtGraphics = null;
        	
        	if (useAdvancedGraphics) {
        		swtGraphics = new SWTGraphics(gc, display);
        	} else {
        		swtGraphics = new NonAdvancedSWTGraphics(gc, display, null);
        	}
            painter.paint(swtGraphics, swtImage, minWidth, minHeight);
            swtGraphics.dispose();
        }
    }

    private boolean clipContainsRepaintRequest( Rectangle clipping ) {

        if (clipping.contains(repaintRequest.x, repaintRequest.y)
                && clipping.contains(repaintRequest.x + repaintRequest.width, repaintRequest.y)
                && clipping.contains(repaintRequest.x + repaintRequest.width,
                        repaintRequest.y = repaintRequest.height)
                && clipping.contains(repaintRequest.x, repaintRequest.y + repaintRequest.height))
            return true;
        return false;
    }

    /**
     * Returns buffer image and if necessary creates the new one while disposing the old.
     * 
     * @return
     */
    org.eclipse.swt.graphics.Image getImage() {
        if (!Platform.isRunning())
            return null;
        if (swtImage != null && disposeMutex == null) {
            return swtImage;
        }

        try {

            if (swtImage != null)
                swtImage.dispose();

            disposeMutex = null;

            swtImage = createImage();
            return swtImage;

        } catch (Throwable e) {
            ProjectUIPlugin.log(null, e);
        }
        return null;
    }

    /**
     * Creates the new buffer image.
     * 
     * @return
     */
    private org.eclipse.swt.graphics.Image createImage() {
        org.eclipse.swt.graphics.Image newImage;
        RenderedImage image = renderManager.getImage();
        if (image != null)
            newImage = AWTSWTImageUtils.createSWTImage(image, false);
        else {
            newImage = new Image(getDisplay(), getWidth(), getHeight());
        }
        return newImage;
    }

    /**
     * Just signals to recreate buffer image in the next redrawing routine.
     */
    void initMap() {
        disposeMutex = new Object();
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
        painter.renderUpdate();
        repaint();
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportPane#setTransform(AffineTransform)
     */

    /**
     * @see org.locationtech.udig.project.render.ViewportPane#dispose()
     */
    public void dispose() {
        super.dispose();
        if (swtImage != null)
            swtImage.dispose();
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportPane#addDrawCommand(org.locationtech.udig.project.internal.commands.draw.IDrawCommand)
     */
    public void addDrawCommand( IDrawCommand command ) {
        painter.addDrawCommand(command);
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportPane#setCursor(java.awt.Cursor)
     */
    public void setCursor( final Cursor cursor ) {
        display.asyncExec(new Runnable(){
            public void run() {
                String name = cursor.getName();
                if (name.equals("Default Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_ARROW));
                } else if (name.equals("Crosshair Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_CROSS));
                } else if (name.equals("Text Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_IBEAM));
                } else if (name.equals("Wait Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_WAIT));
                } else if (name.equals("SW Resize Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_SIZESW));
                } else if (name.equals("SE Resize Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_SIZESE));
                } else if (name.equals("NW Resize Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_SIZENW));
                } else if (name.equals("NE Resize Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_SIZENE));
                } else if (name.equals("N Resize Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_SIZEN));
                } else if (name.equals("S Resize Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_SIZES));
                } else if (name.equals("W Resize Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_SIZEW));
                } else if (name.equals("E Resize Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_SIZEE));
                } else if (name.equals("Hand Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_HAND));
                } else if (name.equals("Move Cursor")) { //$NON-NLS-1$
                    setCursor(new org.eclipse.swt.graphics.Cursor(display, SWT.CURSOR_SIZEALL));
                }
            }
        });
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportPane#removeMouseListener(org.locationtech.udig.project.render.MapMouseListener)
     */
    public void removeMouseListener( MapMouseListener l ) {
        eventJob.removeMouseListener(l);
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportPane#removeMouseMotionListener(org.locationtech.udig.project.render.MapMouseMotionListener)
     */
    public void removeMouseMotionListener( MapMouseMotionListener l ) {
        eventJob.removeMouseMotionListener(l);
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportPane#removeMouseWheelListener(org.locationtech.udig.project.render.MapMouseWheelListener)
     */
    public void removeMouseWheelListener( MapMouseWheelListener l ) {
        eventJob.removeMouseWheelListener(l);
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportPane#addMouseListener(org.locationtech.udig.project.render.MapMouseListener)
     */
    public void addMouseListener( MapMouseListener l ) {
        eventJob.addMouseListener(l);
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportPane#addMouseMotionListener(org.locationtech.udig.project.render.MapMouseMotionListener)
     */
    public void addMouseMotionListener( MapMouseMotionListener l ) {
        eventJob.addMouseMotionListener(l);
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportPane#addMouseWheelListener(org.locationtech.udig.project.render.MapMouseWheelListener)
     */
    public void addMouseWheelListener( MapMouseWheelListener l ) {
        eventJob.addMouseWheelListener(l);
    }

    /**
     * @see org.locationtech.udig.project.render.MapDisplay#getDisplaySize()
     */
    public Dimension getDisplaySize() {
        return displaySize;
    }

    /**
     * @see org.locationtech.udig.project.render.MapDisplay#getWidth()
     */
    public int getWidth() {
        return getDisplaySize().width;
    }

    /**
     * @see org.locationtech.udig.project.render.MapDisplay#getHeight()
     */
    public int getHeight() {
        return getDisplaySize().height;
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
     * @see org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane#getMapEditor()
     */
    public MapPart getMapEditor() {
        return editor;
    }

    public int getDPI() {
        return dpi;
    }

    public Control getControl() {
        return this;
    }

    public void repaint( int x, int y, int width, int height ) {
        if (width == 0 || height == 0)
            return;
        
        synchronized (repaintRequestMutex) {
            Rectangle rectangle = new Rectangle(x, y, width, height);
            if (repaintRequest == null) {
                repaintRequest = rectangle;
                doRepaint(x, y, width, height);
            } else if (repaintRequest == ZERO_RECTANGLE) {
                repaintRequest = rectangle;
            } else {
                repaintRequest.union(rectangle);
            }
        }

    }
    
    public void update(){
        super.update();
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     */
    private void doRepaint( int x, int y, int width, int height ) {
        REPAINT.x = x;
        REPAINT.y = y;
        REPAINT.width = width;
        REPAINT.height = height;
        
        display.asyncExec(REPAINT);
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportPane#repaint()
     */
    public void repaint() {
        repaint(0, 0, displaySize.width, displaySize.height);
    }

    private class Repainter implements Runnable {
        int x;
        int y;
        int width;
        int height;

        public void run() {
            if (PlatformUI.getWorkbench().isClosing())
                return;
            if (!isDisposed()) {
                redraw(x, y, width, height, false);
            } 
        }

    }

    public void enableDrawCommands( boolean enable ) {
        painter.switchOnOff(enable);
    }

    @Override
    public void setCursor( org.eclipse.swt.graphics.Cursor cursor ) {
        super.setCursor(cursor);
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
