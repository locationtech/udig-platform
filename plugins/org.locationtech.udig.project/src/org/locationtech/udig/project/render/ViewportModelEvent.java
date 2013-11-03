/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.render;

import org.locationtech.udig.project.UDIGEvent;

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
