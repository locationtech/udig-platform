/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.issues.internal;

import net.refractions.udig.core.internal.Icons;

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
