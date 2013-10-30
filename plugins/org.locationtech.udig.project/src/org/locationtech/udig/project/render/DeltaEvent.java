/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.render;

/**
 * TODO Purpose of org.locationtech.udig.project.render
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
