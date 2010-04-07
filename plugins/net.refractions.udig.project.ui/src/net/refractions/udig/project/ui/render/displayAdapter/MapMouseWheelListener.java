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
 * An Listener interested int Mouse Wheel Events. Does not work with the ViewportPaneSWT class. API
 * use
 * 
 * @author jeichar
 */
public interface MapMouseWheelListener {
    /**
     * called when the mouse wheel has moved.
     * 
     * @param e The mouse wheel event.
     * @see MapMouseWheelEvent
     */
    public void mouseWheelMoved( MapMouseWheelEvent e );
}
