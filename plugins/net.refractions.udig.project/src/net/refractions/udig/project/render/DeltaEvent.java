/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.render;

/**
 * TODO Purpose of net.refractions.udig.project.render
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class DeltaEvent<T> {
    private T legacy = null;

    private T current = null;

    /**
     * Creates an new instance of DeltaEvent
     * 
     * @param legacy a copy of the state of the changed object before the change occured
     * @param current a copy of the state of the changed object after the change occured
     */
    public DeltaEvent( T legacy, T current ) {
        this.legacy = legacy;
        this.current = current;
    }

    /**
     * Returns a copy of the state of the changed object before the change occured
     * 
     * @return a copy of the state of the changed object before the change occured
     * @uml.property name="legacy"
     */
    public T getLegacy() {
        return legacy;
    }

    /**
     * Returns a copy of the state of the changed object after the change occured
     * 
     * @return a copy of the state of the changed object after the change occured
     * @uml.property name="current"
     */
    public T getCurrent() {
        return current;
    }

}
