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
