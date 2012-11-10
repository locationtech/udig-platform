/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
