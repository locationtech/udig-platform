/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
