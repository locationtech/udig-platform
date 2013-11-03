/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.listeners;

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
