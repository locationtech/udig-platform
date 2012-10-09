/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.project.internal.impl;

/**
 * Common interface used by resource enteries that need to take part in
 * transaction use.
 * 
 * @author jody
 * @since 1.2.1
 */
public interface UDIGStore {

    /**
     * Used to start a transaction.
     * <p>
     * Q: V(italus) Think out how to provide for developers the opportunity to use its own FeatureStore
     * wrapper, not UDIGFeatureStore.
     * <p>
     * A: (Jody) For direct access use id can grab actual resource out of catalog.
     * A: (Jody) For wrapper need an interceptor; and the ability to register interceptors for specific roles
     */
    void startTransaction();
    
    void editComplete();
    
    boolean sameSource( Object resource );
    
    Object wrapped();
}
