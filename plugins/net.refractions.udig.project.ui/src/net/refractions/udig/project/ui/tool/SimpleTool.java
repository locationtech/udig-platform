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
package net.refractions.udig.project.ui.tool;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;

/**
 * An abstract class for all tools that wish to be "selection" tools. A selection tool is
 * distinquished from other tools as it opens a context menu with the normal context menu mouse
 * button.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public class SimpleTool extends AbstractModalTool {
	
	/**
	 * By default SimpleTool will simply respond to MOUSE.
	 * <p>
	 * To respond to additional stimulus please override your constuctor
	 * to call SimpleTool( targets ):<pre><code>
	 * public class MyTool extends SimpleTool {
	 *      public MyTool(){ // default constructor called by extention point
	 *          super( MOUSE | WHEEL );
	 *      }
	 *      ...
	 * }
	 * </code></pre>
	 */
	public SimpleTool(){
		super( MOUSE );
	}

    /**
     * @see AbstractModalTool#AbstractModalTool(int)
     */
    public SimpleTool( int targets ) {
        super(targets);
    }

    /**
     * Called when a double clicked event occurs. It will never be a context-menu request
     * 
     * @param e the mouse event
     */
    protected void onMouseDoubleClicked( MapMouseEvent e ) {
        // do nothing
    }
    /**
     * Called when a mouse pressed event occurs. It will never be a context-menu request
     * 
     * @param e the mouse event
     */
    protected void onMousePressed( MapMouseEvent e ) {
        // do nothing
    }
    /**
     * Called when a mouse released event occurs. It will never be a context-menu request
     * 
     * @param e the mouse event
     */
    protected void onMouseReleased( MapMouseEvent e ) {
        // do nothing
    }

    /**
     * Called when a entered event occurs. It will never be a context-menu request
     * 
     * @param e the mouse event
     */
    protected void onMouseEntered( MapMouseEvent e ) {
        // do nothing
    }
    /**
     * Called when a moved event occurs. It will never be a context-menu request
     * 
     * @param e the mouse event
     */
    protected void onMouseMoved( MapMouseEvent e ) {
        // do nothing
    }    
    /**
     * Called when a hovered event occurs. It will never be a context-menu request
     * 
     * @param e the mouse event
     */
    protected void onMouseHovered( MapMouseEvent e ) {
        // do nothing
    }
    /**
     * Called when a exited event occurs. It will never be a context-menu request
     * 
     * @param e the mouse event
     */
    protected void onMouseExited( MapMouseEvent e ) {
        // do nothing
    }
    /**
     * Called when a mouse wheel moved event occurs. It will never be a context-menu request
     * 
     * @param e the mouse event
     */
    protected void onMouseWheelMoved( MapMouseWheelEvent e ) {
        // do nothing
    }
    /**
     * Called when a mouse dragged event occurs. It will never be a context-menu request
     * 
     * @param e the mouse event
     */
    protected void onMouseDragged( MapMouseEvent e ) {
        // do nothing
    }
    /**
     * Opens the context menu if the second mouse button is pressed and calls
     * noContextMouseReleased().
     * 
     * @see AbstractTool#mousePressed(MapMouseEvent)
     * @param e the mouse event
     * @see MapMouseEvent
     */
    public final void mousePressed( MapMouseEvent e ) {
        if (e.button == MapMouseEvent.BUTTON3)
            ((ViewportPane) e.source).getMapEditor().openContextMenu();
        else
            onMousePressed(e);
    }

    /**
     * Consumes the event if the second mouse button is released and calls noContextMousePressed().
     * 
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public final void mouseReleased( MapMouseEvent e ) {
        if (e.button != MapMouseEvent.BUTTON3)
            onMouseReleased(e);
    }

    /**
     * Consumes the event if the second mouse button is doubleclicked and calls
     * onContextMouseDoubleClicked().
     * 
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseDoubleClicked(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public final void mouseDoubleClicked( MapMouseEvent e ) {
        if (e.button != MapMouseEvent.BUTTON3)
            onMouseDoubleClicked(e);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public final void mouseDragged( MapMouseEvent e ) {
        if (e.button != MapMouseEvent.BUTTON3)
            onMouseDragged(e);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseEntered(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public final void mouseEntered( MapMouseEvent e ) {
        if (e.button != MapMouseEvent.BUTTON3)
            onMouseEntered(e);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseMoved(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public final void mouseMoved( MapMouseEvent e ) {
        if (e.button != MapMouseEvent.BUTTON3)
            onMouseMoved(e);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseMoved(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     * @see MapMouseEvent
     */
    public final void mouseHovered( MapMouseEvent e ) {
        if (e.button != MapMouseEvent.BUTTON3)
            onMouseHovered(e);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseExited(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public final void mouseExited( MapMouseEvent e ) {
        if (e.button != MapMouseEvent.BUTTON3)
            onMouseExited(e);
    }
    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseWheelMoved(net.refractions.udig.project.render.displayAdapter.MapMouseWheelEvent)
     * @see MapMouseEvent
     */
    public final void mouseWheelMoved( MapMouseWheelEvent e ) {
        if (e.button != MapMouseEvent.BUTTON3)
            onMouseWheelMoved(e);
    }

}
