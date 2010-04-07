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
