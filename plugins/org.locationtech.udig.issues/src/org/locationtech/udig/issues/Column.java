/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues;

/**
 * Indicates a column in the issues view
 * 
 * @author Jesse
 * @since 1.1.0
 */
public enum Column {
    /**
     * Indicates the priority column
     */
    PRIORITY,
    /**
     * Indicates the resolution column
     */
    RESOLUTION,
    /**
     * Indicates the Name column
     */
    PROBLEM_OBJECT,
    /**
     * Indicates the description column
     */
    DESCRIPTION
}
