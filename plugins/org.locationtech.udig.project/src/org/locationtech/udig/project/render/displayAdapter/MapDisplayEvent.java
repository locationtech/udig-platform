/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.render.displayAdapter;

import java.awt.Dimension;

/**
 * TODO Purpose of org.locationtech.udig.project.render.displayAdapter
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class MapDisplayEvent {
    private IMapDisplay source;

    private Dimension size;

    private Dimension old;

    /**
     * Creates a new instance of MapDisplayEvent
     * 
     * @param source The IMapDisplay that raised the event.
     * @param size The new size of the IMapDisplay
     * @param old The old size of the IMapDisplay
     */
    public MapDisplayEvent( IMapDisplay source, Dimension old, Dimension size ) {
        this.source = source;
        this.size = size;
        this.old = old;
    }

    /**
     * The IMapDisplay that raised the event.
     * 
     * @return The IMapDisplay that raised the event.
     * @uml.property name="source"
     */
    public IMapDisplay getSource() {
        return source;
    }

    /**
     * Returns The new size of the IMapDisplay
     * 
     * @return The new size of the IMapDisplay
     * @see Dimension
     * @uml.property name="size"
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * Returns The old size of the IMapDisplay
     * 
     * @return The old size of the IMapDisplay
     * @see Dimension
     */
    public Dimension getOldSize() {
        return old;
    }
}
