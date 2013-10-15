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
package net.refractions.udig.project;

/**
 * The interface for all classes that decorate non-eclipse classes. This allows access to the "Real"
 * object if necessary.
 * 
 * @author jeichar
 * @since 0.3
 */
public interface UDIGAdaptableDecorator {
    /**
     * Get the "real" object.
     * 
     * @return the "real object
     */
    Object getObject();
}
