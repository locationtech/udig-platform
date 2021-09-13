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
package org.locationtech.udig.catalog.internal.ui;

/**
 * Image constants for use with Images class.
 * <p>
 * Example use:
 *
 * <pre><code>
 *  Images.
 *  TODO WRITE MORE DOCS HERE, JODY
 * </code></pre>
 *
 * </p>
 *
 * @author jgarnett
 */
public interface ImageConstants {

    /**
     * Enabled toolbar icons
     */
    public static final String PATH_ETOOL = "etool16/"; //$NON-NLS-1$
    /** Discovery wizard icon */
    public static final String DISCOVERY_WIZ = PATH_ETOOL + "discovert_wiz.gif"; //$NON-NLS-1$

    /**
     * Disabled toolbar icons.
     */
    // public static final String PATH_DTOOL = ICONS_PATH+"dtool16/"; //$NON-NLS-1$
    /**
     * Enabled local toolbar icons.
     */
    public static final String PATH_ELOCALTOOL = "elcl16/"; //$NON-NLS-1$
    /** Add command icon */
    public static final String ADD_CO = PATH_ELOCALTOOL + "add_co.gif"; //$NON-NLS-1$
    /** Refresh command icon */
    public static final String REFRESH_CO = PATH_ELOCALTOOL + "refresh_co.gif"; //$NON-NLS-1$
    /** Remove command icons */
    public static final String REMOVE_CO = PATH_ELOCALTOOL + "remove_co.gif"; //$NON-NLS-1$

    /**
     * Disabled local toolbar icons
     */
    public static final String PATH_DLOCALTOOL = "dlcl16/"; //$NON-NLS-1$

    /**
     * View icons
     */
    public static final String PATH_EVIEW = "eview16/"; //$NON-NLS-1$

    /**
     * Product images
     */
    public static final String PATH_PROD = "prod/"; //$NON-NLS-1$

    /**
     * Model object icons
     */
    public static final String PATH_OBJECT = "obj16/"; //$NON-NLS-1$

    /** <code>DATABASE_OBJ</code> field */
    public static final String DATABASE_OBJ = PATH_OBJECT + "database_obj.gif"; //$NON-NLS-1$
    /** <code>DATASTORE_OBJ</code> field */
    public static final String DATASTORE_OBJ = PATH_OBJECT + "datastore_obj.gif"; //$NON-NLS-1$
    /** <code>FEATURE_OBJ</code> field */
    public static final String FEATURE_OBJ = PATH_OBJECT + "feature_obj.gif"; //$NON-NLS-1$
    /** <code>FEATURE_FILE_OBJ</code> field */
    public static final String FEATURE_FILE_OBJ = PATH_OBJECT + "feature_file_obj.gif"; //$NON-NLS-1$
    /** <code>FOLDER_OBJ</code> field */
    public static final String FOLDER_OBJ = PATH_OBJECT + "folder_obj.gif"; //$NON-NLS-1$
    /** <code>GCE_OBJ</code> field */
    public static final String GCE_OBJ = PATH_OBJECT + "gce_obj.gif"; //$NON-NLS-1$
    /** <code>GRID_OBJ</code> field */
    public static final String GRID_OBJ = PATH_OBJECT + "grid_obj.gif"; //$NON-NLS-1$
    /** <code>GRID_FILE_OBJ</code> field */
    public static final String GRID_FILE_OBJ = PATH_OBJECT + "grid_file_obj.gif"; //$NON-NLS-1$
    /** <code>MAPFOLDER_OBJ</code> field */
    public static final String MAPFOLDER_OBJ = PATH_OBJECT + "mapfolder_obj.gif"; //$NON-NLS-1$
    /** <code>MAPFOLDER_NOEXIST_OBJ</code> field */
    public static final String MAPFOLDER_NOEXIST_OBJ = PATH_OBJECT + "mapfolder_noexist_obj.gif"; //$NON-NLS-1$
    /** <code>MEMORY_OBJ</code> field */
    public static final String MEMORY_OBJ = PATH_OBJECT + "memory_obj.gif"; //$NON-NLS-1$
    /** <code>LAYER_OBJ</code> field */
    public static final String LAYER_OBJ = PATH_OBJECT + "layer_obj.gif"; //$NON-NLS-1$
    /** <code>REPOSITORY_OBJ</code> field */
    public static final String REPOSITORY_OBJ = PATH_OBJECT + "repository_obj.gif"; //$NON-NLS-1$
    /** <code>SERVER_OBJ</code> field */
    public static final String SERVER_OBJ = PATH_OBJECT + "server_obj.gif"; //$NON-NLS-1$
    /** <code>WFS_OBJ</code> field */
    public static final String WFS_OBJ = PATH_OBJECT + "wfs_obj.gif"; //$NON-NLS-1$
    /** <code>WMS_OBJ</code> field */
    public static final String WMS_OBJ = PATH_OBJECT + "wms_obj.gif"; //$NON-NLS-1$

    /**
     * Pointer icons
     */
    public static final String PATH_POINTER = "pointer/"; //$NON-NLS-1$

    /**
     * Wizard banners
     */
    public static final String PATH_WIZBAN = "wizban/"; //$NON-NLS-1$

    /** <code>ADD_WIZBAN</code> field */
    public static final String ADD_WIZBAN = PATH_WIZBAN + "add_wiz.gif"; //$NON-NLS-1$

    /** <code>DATA_WIZBAN</code> field used for import data wizard */
    public static final String DATA_WIZBAN = PATH_WIZBAN + "catalog_wiz.gif"; //$NON-NLS-1$

    /** <code>DATA_WIZBAN</code> field used for selecting resources */
    public static final String CHOOSE_LAYER_WIZARD = PATH_WIZBAN+"chooselayer_wiz.gif";

    /**
     * Misc icons
     */
    public static final String PATH_MISC = "misc/"; //$NON-NLS-1$

    /**
     * icons Overlays
     */
    public static final String PATH_OVERLAY = "ovr16/"; //$NON-NLS-1$
    /** <code>CONNECTED_OVR</code> field */
    public static final String CONNECTED_OVR = PATH_OVERLAY + "connected_ovr.gif"; //$NON-NLS-1$
    /** <code>ERROR_OVR</code> field */
    public static final String ERROR_OVR = PATH_OVERLAY + "error_ovr.gif"; //$NON-NLS-1$
    /** <code>UNCONFIGURED_OVR</code> field */
    public static final String UNCONFIGURED_OVR = PATH_OVERLAY + "unconfigurated_ovr.gif"; //$NON-NLS-1$
    /** <code>WAIT_OVR</code> field */
    public static final String WAIT_OVR = PATH_OVERLAY + "wait_ovr.gif"; //$NON-NLS-1$
    /** <code>WARNING_OVR</code> field */
    public static final String WARNING_OVR = PATH_OVERLAY + "warning_ovr.gif"; //$NON-NLS-1$
}
