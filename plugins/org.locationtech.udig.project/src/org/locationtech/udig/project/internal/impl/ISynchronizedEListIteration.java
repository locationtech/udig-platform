/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2015, Refractions Research Inc. and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

/**
 * @param <E> the generic type hold by this list
 * 
 * @author Frank Gasdorf
 * @author Erdal Karaca
 *
 * @since 2.0.0
 */
public interface ISynchronizedEListIteration<E> {

    /**
     * Iterates over the elements of this list and calls the visit method of the provided visitor
     * implementation for each element. While this list is iterated over, the internal lock is used
     * to sync concurrent access.
     *
     * @param visitor callback to the visitor to handle the element of this list
     * @param <T> the generic type hold by this list, the caller is responsible for making sure that
     *        no class cast exceptions are caused while traversing this list
     */
    void syncedIteration(IEListVisitor<E> visitor);

}