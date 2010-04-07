/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.render;

import net.refractions.udig.project.internal.render.RenderingCoordinator;

/**
 * Encapsulates an rendering event.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class RenderEvent {
    private final RenderingCoordinator source;
    private final RenderEventType type; 
    private final Object oldValue, newValue;
    
    public RenderEvent( final RenderingCoordinator source, final RenderEventType type, final Object oldValue, final Object newValue ) {
        super();
        this.source = source;
        this.type = type;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public RenderEventType getType() {
        return type;
    }

    public RenderingCoordinator getSource() {
        return source;
    }
    
}
