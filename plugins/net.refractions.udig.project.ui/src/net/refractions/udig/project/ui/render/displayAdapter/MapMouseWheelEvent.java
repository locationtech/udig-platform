/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.render.displayAdapter;

import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

/**
 * Event Mouse wheel events. API use?example?
 * 
 * @author Jones
 * @since 0.3
 * @see MapMouseEvent
 */
public class MapMouseWheelEvent extends MapMouseEvent {

    /** The number of wheel clicks. Positive indicates forward/up scroll direction. */
    public final int clickCount;

    /**
     * Construct <code>MapMouseWheelEvent</code>.
     * 
     * @param source The object that raised the event
     * @param state the state of the event ORed together
     * @param x the x position of the event
     * @param y the y position of the event
     * @param clickCount The number of wheel clicks. Positive indicates forward/up scroll direction.
     */
    public MapMouseWheelEvent( IMapDisplay source, int x, int y, int modifiers, int buttons, int button, int clickCount ) {
        super(source, x, y, modifiers, buttons, NONE);
        this.clickCount = clickCount;
    }

    /**
     * Returns the number of wheel clicks. Positive indicates forward/up scroll direction. API is
     * this method required?
     * 
     * @return the number of wheel clicks.
     */
    public int getClickCount() {
        return clickCount;
    }
}
