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
package net.refractions.udig.ui;

import net.refractions.udig.internal.ui.UDIGActionBarAdvisor;

import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * Constants used to layout the uDig menu structure.
 * <p>
 * You can use these constants when defining menu paths; do yourself a favour
 * and only make use of the *XXX_EXT* entries; since they will usually be
 * there for you.
 * <ul>
 * <li>Additional IActions
 * <li>Additional menu contributions using the org.eclipse.ui.menu extention point
 * </ul>
 * @see UDIGActionBarAdvisor for the exact break down of expected menus
 */
public interface Constants {
    // Note:  all menus have a IWorkbenchActionConstants group
    
    // Group Markers for the File menu in the menu bar
    /** menu path: "file/fileStart" */
    public static final String FILE_START=IWorkbenchActionConstants.FILE_START;
    /** menu path: "file/open.ext" */
    public static final String OPEN_EXT=IWorkbenchActionConstants.OPEN_EXT;
    /** menu path: "file/project.ext" */
    public static final String PROJECT_EXT=IWorkbenchActionConstants.OPEN_EXT;
    /** menu path: "file/close.ext" */
    public static final String CLOSE_EXT=IWorkbenchActionConstants.CLOSE_EXT;
    /** menu path: "file/save.ext" */
    public static final String SAVE_EXT=IWorkbenchActionConstants.SAVE_EXT;
    /** menu path: "file/fileEnd" */
    public static final String FILE_END=IWorkbenchActionConstants.FILE_END;
    
    /** menu path: "file/config.ext" */
    public static final String CONFIG_EXT = "config.ext"; //$NON-NLS-1$
    /** menu path: "file/new.start" */
    public static final String NEW_START = "new.start"; //$NON-NLS-1$
    /** menu path: "file/rename.ext" */
    public static final String RENAME_EXT = "rename.ext"; //$NON-NLS-1$

    
    // Group Marker for the Edit menu in the menu bar
    public static final String EDIT_START=IWorkbenchActionConstants.EDIT_START;
    public static final String UNDO_EXT=IWorkbenchActionConstants.UNDO_EXT;
    public static final String CUT_EXT=IWorkbenchActionConstants.CUT_EXT;
    public static final String ADD_EXT=IWorkbenchActionConstants.ADD_EXT;
    public static final String EDIT_END=IWorkbenchActionConstants.EDIT_END;
    /** menu path: "edit/other" */
    public static final String OTHER = "other";
    /** menu path: "edit/commit" */
    public static final String COMMIT_EXT = "commit.exe"; //$NON-NLS-1$
    
    // Group Marker for the Navigation menu in the menu bar
    /** menu path: "navigate" */
    public static final String M_NAVIGATE=IWorkbenchActionConstants.M_NAVIGATE;
    /** menu path: "navigate/navStart" */
    public static final String NAV_START=IWorkbenchActionConstants.NAV_START;
    /** menu path: "navigate/zoom.ext" */
    public static final String NAV_ZOOM_EXT="zoom.ext"; //$NON-NLS-1$
    /** menu path: "navigate/bottom" */
    public static final String NAV_BOTTOM="bottom"; //$NON-NLS-1$
    /** menu path: "navigate/navEnd" */
    public static final String NAV_END=IWorkbenchActionConstants.NAV_END;
    
    // Group Marker for the Layer menu in the menu bar
    public static final String M_LAYER="layer"; //$NON-NLS-1$
    public static final String LAYER_ADD_EXT="add.ext"; //$NON-NLS-1$
    public static final String LAYER_EDIT_EXT="edit.ext"; //$NON-NLS-1$
    public static final String LAYER_MAPGRAPHIC_EXT="mapGraphic.ext"; //$NON-NLS-1$
    public static final String LAYER_MAPGRAPHIC_OTHER="mapGraphicOther.ext"; //$NON-NLS-1$
    
    // Group Marker for the Window menu in the menu bar - Only MB_ADDITIONS
    // Group Marker for the Tool menu in the menu bar
    /** menu path: "tools" */
    public static final String M_TOOL="tools"; //$NON-NLS-1$
    /** menu path: "tools/action.ext" */
    public static final String TOOL_ACTION="action.ext"; //$NON-NLS-U1$
    /** menu path: "tools/modal.ext" */
    public static final String TOOL_MODAL="modal.ext"; //$NON-NLS-1$
    
    // Group Marker for the Help menu in the menu bar
    public static final String HELP_START=IWorkbenchActionConstants.HELP_START;
    public static final String HELP_END=IWorkbenchActionConstants.HELP_END;
    
    
}
