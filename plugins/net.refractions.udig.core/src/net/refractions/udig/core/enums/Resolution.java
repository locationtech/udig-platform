/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.core.enums;

/**
 * Indicates whether a situation is resolved(fixed), unresolved or unknown.
 * 
 * @author jones
 * @since 1.0.0
 */
public enum Resolution {
    /**
     * The situation has yet to be resolved
     */
    UNRESOLVED,
    /**
     * The user has looked at the situation
     */
    IN_PROGRESS,
    /**
     * The situation has been resolved
     */
    RESOLVED,
    /**
     * User input is required to determine whether the situation has been resolved
     */
    UNKNOWN
}
