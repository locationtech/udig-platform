/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
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
