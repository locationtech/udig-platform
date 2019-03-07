/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

    public static final String P_SNAP_RADIUS = "P_SNAP_RADIUS"; //$NON-NLS-1$
    public static final String P_VERTEX_SIZE = "P_VERTEX_SIZE"; //$NON-NLS-1$
    public static final String P_SNAP_CIRCLE_COLOR = "P_SNAP_CIRCLE_COLOR"; //$NON-NLS-1$
    public static final String P_FILL_VERTICES = "P_FILL_VERTICES"; //$NON-NLS-1$
    public static final String P_FILL_POLYGONS = "P_FILL_POLYGONS"; //$NON-NLS-1$
    public static final String P_SNAP_BEHAVIOUR = "P_SNAP_OPERATION"; //$NON-NLS-1$
    public static final String P_HIDE_SELECTED_FEATURES = "P_HIDE_SELECTED_FEATURES"; //$NON-NLS-1$
    public static final String P_ADVANCED_ACTIVE = "P_ADVANCED_ACTIVE"; //$NON-NLS-1$
    public static final String P_SELECT_POST_ACCEPT = "P_DESELECT_POST_ACCEPT"; //$NON-NLS-1$

    /**
     * The confirmation setting for delete tool.
     */
    public static final String P_DELETE_TOOL_CONFIRM = "P_DELETE_TOOL_CONFIRM"; //$NON-NLS-1$

    /**
     * The scale factor to be used during UI delete command.
     */
    public static final String P_DELETE_TOOL_SEARCH_SCALEFACTOR = "P_DELETE_TOOL_SEARCH_SCALEFACTOR"; //$NON-NLS-1$

    /**
     * default scale factor for delete tool (Default is
     * {@value #P_DEFAULT_DELETE_SEARCH_SCALEFACTOR}) see
     * <code>IAbstractContext.getBoundingBox( Point screenLocation, int scalefactor)</code>
     */
    public static final int P_DEFAULT_DELETE_SEARCH_SCALEFACTOR = 6;

    /** 
     * The opacity of color used to fill the geoms on the EditBlackboard.
     */
    public static final String P_FILL_OPACITY = "P_FILL_OPACITY"; //$NON-NLS-1$
    
    /** 
     * The opacity of color used for the vertex of the edit geoms.
     */
    public static final String P_VERTEX_OPACITY = "P_VERTEX_OPACITY"; //$NON-NLS-1$

}