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

import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

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
