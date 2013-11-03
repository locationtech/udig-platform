/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.issues.internal;

import org.locationtech.udig.core.internal.Icons;

/**
 * Image constants for use with Images class.
 * 
 * @author jgarnett
 */
public interface ImageConstants {

    public static final String PRIORITY_CRITICAL = Icons.MISC + "priority_critical_obj.gif"; //$NON-NLS-1$
    public static final String PRIORITY_HIGH = Icons.MISC + "priority_error_obj.gif"; //$NON-NLS-1$
    public static final String PRIORITY_WARNING = Icons.MISC + "priority_warning_obj.gif"; //$NON-NLS-1$
    public static final String PRIORITY_LOW = Icons.MISC + "priority_info_obj.gif"; //$NON-NLS-1$
    public static final String PRIORITY_TRIVIAL = Icons.MISC + "priority_minor_obj.gif"; //$NON-NLS-1$

    public static final String RESOLUTION_RESOLVED = Icons.MISC + "resolved.png"; //$NON-NLS-1$
    public static final String RESOLUTION_UNKNOWN = Icons.MISC + "unknown.png"; //$NON-NLS-1$
    public static final String RESOLUTION_UNRESOLVED = Icons.MISC + "unresolved.png"; //$NON-NLS-1$
    public static final String RESOLUTION_VIEWED = Icons.MISC + "viewed.png"; //$NON-NLS-1$

    public static final String GOTO_ISSUE = Icons.ELOCALTOOL + "goto_issue.gif"; //$NON-NLS-1$
    public static final String DELETE = Icons.ETOOL + "delete.gif"; //$NON-NLS-1$
    public static final String DELETE_GROUP = Icons.ETOOL + "delete_group.gif"; //$NON-NLS-1$
}
