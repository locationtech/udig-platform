/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit;

/**
 * Enumerates the various states an editing tool may be in.
 * 
 * @author jones
 * @since 1.1.0
 */
public enum EditState {
    /**
     * No editing has occured
     */
    NONE, 
    /**
     * An existing feature is being modified
     */
    MODIFYING,
    /**
     * A vertex or geom or shape (or something) is being moved
     */
    MOVING,
    /**
     * A new feature is being created
     */
    CREATING,
    /**
     * Editing is not permitted
     */
    ILLEGAL, 
    /**
     * Something is going on 
     */
    BUSY, 
    /**
     * A commit is taking place.
     */
    COMMITTING 
    
}
