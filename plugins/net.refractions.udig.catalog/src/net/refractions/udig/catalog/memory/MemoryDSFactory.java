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
package net.refractions.udig.catalog.memory;

/**
 * Permits the instantiation of custom version of ActiveMemoryDataStore.
 * @author rgould
 * @since 1.1.0
 */
public interface MemoryDSFactory {

    /**
     * Construct and return a new instance of an ActiveMemoryDataStore.
     * Typically this is a custom sub-class implementation
     *
     * @return a new instance of an ActiveMemoryDataStore
     */
    ActiveMemoryDataStore createNewDS();
}
