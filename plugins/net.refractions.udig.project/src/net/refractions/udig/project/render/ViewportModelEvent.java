/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.render;

import net.refractions.udig.project.UDIGEvent;

/**
 * Encapsulates a ViewportModelEvent
 * @author Jesse
 * @since 1.1.0
 */
public class ViewportModelEvent extends UDIGEvent{

    public static enum EventType{
        /**
         * Indicates the CoordinateReferenceSystem of the ViewportModel has changed.
         */
        CRS,
        /**
         * Indicates the Bounds of the ViewportModel has changed.
         */
        BOUNDS
    }

    private final EventType type;
    
    public ViewportModelEvent( Object source2, EventType type, Object newValue2, Object oldValue2 ) {
        super(source2, newValue2, oldValue2);
        this.type=type;
    }

    @Override
    public IViewportModel getSource() {
        return (IViewportModel) source;
    }

    /**
     * @return Returns the type of the event.
     */
    public EventType getType() {
        return type;
    }

}
