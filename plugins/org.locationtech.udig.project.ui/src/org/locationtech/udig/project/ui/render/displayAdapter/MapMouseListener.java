/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.render.displayAdapter;

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
