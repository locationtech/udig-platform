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
