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
 * A Listener interested in mouse button event and entry and exit events. MapMouseListeners can sigh
 * up for
 * 
 * @author Jones
 * @since 0.3
 */
public interface MapMouseListener {
    /**
     * Called when a button is pressed down.
     * 
     * @param event the event data.
     * @see MapMouseEvent
     */
    public void mousePressed( MapMouseEvent event );

    /**
     * Called when a button is release.
     * 
     * @param event the event data.
     * @see MapMouseEvent
     */
    public void mouseReleased( MapMouseEvent event );

    /**
     * Called when the mouse cursor enters the map display area.
     * 
     * @param event the event data.
     * @see MapMouseEvent
     */
    public void mouseEntered( MapMouseEvent event );

    /**
     * Called when the mouse cursor exits the map dispaly area.
     * 
     * @param event the event data.
     * @see MapMouseEvent
     */
    public void mouseExited( MapMouseEvent event );

    /**
     * Called when a button has been double clicked.
     * 
     * @param event the event data.
     * @see MapMouseEvent
     */
    public void mouseDoubleClicked( MapMouseEvent event );

}
