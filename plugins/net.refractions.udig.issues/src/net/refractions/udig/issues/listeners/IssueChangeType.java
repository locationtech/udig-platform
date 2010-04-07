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
package net.refractions.udig.issues.listeners;

/**
 * Enumerates the different types of issue changes that issuesListListener can be notified of.
 * @author Jesse
 * @since 1.1.0
 */
public enum IssueChangeType {

    /**
     * Indications the description of the issue has changed.
     */
    DESCRIPTION,
    /**
     * Indications the priority of the issue has changed.
     */
    PRIORITY,
    /**
     * Indications the resolution of the issue has changed.
     */
    RESOLUTION,
    /**
     * Indications the bounds of the issue has changed
     */
    BOUNDS,
    /**
     * Indicates that something else has changed, may be issue specific.
     */
    OTHER
}
