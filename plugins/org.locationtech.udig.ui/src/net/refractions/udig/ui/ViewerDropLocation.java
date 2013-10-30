/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

import org.eclipse.jface.viewers.ViewerDropAdapter;

/**
 * Enumerates the number of places that a element can be dropped on an element in a viewer.
 * 
 * For example when dropping something on an item in a ListViewer the item can be BEFORE, ON or AFTER 
 * @author Jesse
 * @since 1.1.0
 */
public enum ViewerDropLocation {

    /**
     * Indicates the dropped item was dropped before the item
     */
    BEFORE,
    /**
     * Indicates the dropped item was dropped on the item
     */
    ON,
    /**
     * Indicates the dropped item was dropped after the item
     */
    AFTER,
    /**
     * Indicates the dropped item was not dropped on an item
     */
    NONE;
    
    public static ViewerDropLocation valueOf( int location ){
        switch( location ) {
        case ViewerDropAdapter.LOCATION_BEFORE:
            return BEFORE;
        case ViewerDropAdapter.LOCATION_AFTER:
            return AFTER;
        case ViewerDropAdapter.LOCATION_ON:
            return ON;
        default:
            return NONE;
        }
    }
    
}
