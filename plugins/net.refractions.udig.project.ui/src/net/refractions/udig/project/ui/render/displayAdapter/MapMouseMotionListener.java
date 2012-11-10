/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.ui.render.displayAdapter;

/**
 * A Listener that is interested in mouse motion events. API use?
 * 
 * @author Jones
 * @since 0.3
 */
public interface MapMouseMotionListener {
    /**
     * called when a mouse is moved <b>without </b> buttons down.
     * 
     * @param event The event data.
     * @see MapMouseEvent
     */
    public void mouseMoved( MapMouseEvent event );

    /**
     * Called when a mouse is moved <b>with </b> buttons down.
     * 
     * @param event The event data.
     * @see MapMouseEvent
     */
    public void mouseDragged( MapMouseEvent event );

    /**
     * Called when a mouse is considered to be "hovering"
     *
     * @param event
     */
    public void mouseHovered( MapMouseEvent event );
}
