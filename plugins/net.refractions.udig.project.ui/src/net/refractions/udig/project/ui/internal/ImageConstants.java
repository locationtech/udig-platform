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
package net.refractions.udig.project.ui.internal;

import net.refractions.udig.core.internal.Icons;

/**
 * Image constants for use with Images class.
 * 
 * @author jgarnett
 */
public interface ImageConstants {

    // ELOCALTOOL
    //
    /** Up arrow */
    public final static String UP_CO = Icons.ELOCALTOOL + "up_co.gif"; //$NON-NLS-1$
    /** Down arrow */
    public final static String DOWN_CO = Icons.ELOCALTOOL + "down_co.gif"; //$NON-NLS-1$
    /** Left arrow * */
    public final static String LEFT_CO = Icons.ELOCALTOOL + "left_co.gif"; //$NON-NLS-1$
    /** Right arrow * */
    public final static String RIGHT_CO = Icons.ELOCALTOOL + "left_co.gif"; //$NON-NLS-1$
    /** drop down arrow * */
    public final static String DROP_DOWN_BUTTON = Icons.OVERLAY + "drop_down_ovr.gif"; //$NON-NLS-1$

    // OVR
    //

    /** <code>WARN_OVR</code> indicates render produced warnings, often due to reprojection */
    public final static String WARN_OVR = Icons.OVERLAY + "warning_ovr.gif"; //$NON-NLS-1$

    /** <code>CHANGED_OVR</code> indicates layer has been changed, and a commit is needed */
    public final static String CHANGED_OVR = Icons.OVERLAY + "changed_ovr.gif"; //$NON-NLS-1$

    /** <code>WRITE_OVR</code> indicates layer is editable */
    public final static String WRITE_OVR = Icons.OVERLAY + "write_ovr.gif"; //$NON-NLS-1$

    /** <code>ERROR_OVR</code> indicates renderer was unable to function, check log */
    public final static String ERROR_OVR = Icons.OVERLAY + "error_ovr.gif"; //$NON-NLS-1$

    /** <code>HUH_OVR</code> indicates data could not be located for layer */
    public final static String HUH_OVR = Icons.OVERLAY + "huh_ovr.gif"; //$NON-NLS-1$

    /** <code>CONFIG_OVR</code> indicates service providing data is not configured properly */
    public final static String UNCONFIGURED_OVR = Icons.OVERLAY + "unconfigured_ovr.gif"; //$NON-NLS-1$

    /**
     * <code>WAIT_OVR</code> rendering process has started (requests have been made), please wait
     * ...
     */
    public final static String WAIT_OVR = Icons.OVERLAY + "wait_ovr.gif"; //$NON-NLS-1$

    /** <code>CLOCK0_OVR</code> rendering process is now updaing the screen ... */
    public final static String CLOCK0_OVR = Icons.OVERLAY + "clock0_ovr.gif"; //$NON-NLS-1$
    /** <code>CLOCK1_OVR</code> rendering process is now updaing the screen ... */
    public final static String CLOCK1_OVR = Icons.OVERLAY + "clock1_ovr.gif"; //$NON-NLS-1$
    /** <code>CLOCK2_OVR</code> rendering process is now updaing the screen ... */
    public final static String CLOCK2_OVR = Icons.OVERLAY + "clock2_ovr.gif"; //$NON-NLS-1$
    /** <code>CLOCK3_OVR</code> rendering process is now updaing the screen ... */
    public final static String CLOCK3_OVR = Icons.OVERLAY + "clock3_ovr.gif"; //$NON-NLS-1$
    /** <code>CLOCK4_OVR</code> rendering process is now updaing the screen ... */
    public final static String CLOCK4_OVR = Icons.OVERLAY + "clock4_ovr.gif"; //$NON-NLS-1$
    /** <code>CLOCK5_OVR</code> rendering process is now updaing the screen ... */
    public final static String CLOCK5_OVR = Icons.OVERLAY + "clock5_ovr.gif"; //$NON-NLS-1$
    /** <code>CLOCK6_OVR</code> rendering process is now updaing the screen ... */
    public final static String CLOCK6_OVR = Icons.OVERLAY + "clock6_ovr.gif"; //$NON-NLS-1$
    /** <code>CLOCK7_OVR</code> rendering process is now updaing the screen ... */
    public final static String CLOCK7_OVR = Icons.OVERLAY + "clock7_ovr.gif"; //$NON-NLS-1$

    /** <code>SELECT_UNDR</code> underlay used to indicate something is selectable... */
    public final static String SELECT_UDR = Icons.OVERLAY + "select_udr.gif"; //$NON-NLS-1$

    // WIZBAN
    //
    /** New Wizard banner */
    public final static String NEW_WIZBAN = Icons.WIZBAN + "new_wiz.gif"; //$NON-NLS-1$
    /** New folder wizard banner */
    public final static String NEWFOLDER_WIZBAN = Icons.WIZBAN + "newfolder_wiz.gif"; //$NON-NLS-1$
    /** New Layer banner */
    public final static String NEWLAYER_WIZBAN = Icons.WIZBAN + "newlayer_wiz.gif"; //$NON-NLS-1$
    /** New Map banner */
    public final static String NEWMAP_WIZBAN = Icons.WIZBAN + "newmap_wiz.gif"; //$NON-NLS-1$
    /** New Page banner */
    public final static String NEWPAGE_WIZBAN = Icons.WIZBAN + "newpage_wiz.gif"; //$NON-NLS-1$    
    /** New Project banner */
    public final static String NEWPROJECT_WIZBAN = Icons.WIZBAN + "newprj_wiz.gif"; //$NON-NLS-1$
    /** New Template banner */
    public final static String NEWTEMPLATE_WIZBAN = Icons.WIZBAN + "newtemplate_wiz.gif"; //$NON-NLS-1$
    /** Choose Layer banner */
    public final static String CHOOSE_LAYER_WIZBAN = Icons.WIZBAN + "chooselayer_wiz.gif"; //$NON-NLS-1$
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
    public static final String LINKED_ENABLED_CO = Icons.ELOCALTOOL + "link_co.gif"; //$NON-NLS-1$
    public static final String LINKED_DISABLED_CO = Icons.DLOCALTOOL + "link_co.gif"; //$NON-NLS-1$
}
