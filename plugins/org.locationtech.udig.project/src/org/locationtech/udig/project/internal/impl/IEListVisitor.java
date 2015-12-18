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
 * Visitor pattern used to callback when an element is visited, for example, while iterating
 * over lists. This is used to provide synced access to list traversals without making internal
 * APIs public.
 * 
 * @param T type of element hold by the EList
 * 
 * @author Frank Gasdorf
 * @author Erdal Karaca
 * 
 * @since 2.0.0
 * 
 * @see {@link SynchronizedEObjectWithInverseResolvingEList#syncedIteration(IEListVisitor)}.
 */
public interface IEListVisitor<T> {
    /**
     * 
     * @param t element to work with 
     */
    void visit( T t );
}
