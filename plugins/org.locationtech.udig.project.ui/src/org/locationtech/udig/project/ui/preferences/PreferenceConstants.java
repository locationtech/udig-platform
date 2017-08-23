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
package org.locationtech.udig.project.ui.preferences;

/**
 * Constants used to access project preferences.
 */
public interface PreferenceConstants {

    /**
     * Indicates whether previously open maps should be re-opened on startup.
     * Defaults to true.
     */
    public final static String P_OPEN_MAPS_ON_STARTUP = "openMapsOnStartup";  //$NON-NLS-1$  

    /**
     * Indicates the variable to threat as double-click speed.
     */
    public final static String MOUSE_SPEED = "mouseSpeed";  //$NON-NLS-1$  

    /**
     * Preference to store the resolutions for this tileset
     */
    public static final String P_TILESET_SCALES = "tilesetScales"; //$NON-NLS-1$

    /**
     * Preference to store the tile width
     */
    public static final String P_TILESET_WIDTH = "tilesetWidth"; //$NON-NLS-1$

    /**
     * Preference to store the tile height
     */
    public static final String P_TILESET_HEIGHT = "tilesetHeight"; //$NON-NLS-1$

    /**
     * Boolean preference to use the tilesets, or not 
     */
    public static final String P_TILESET_ON_OFF = "tilesetOnOff"; //$NON-NLS-1$

    /**
     * Preference to store the tile height
     */
    public static final String P_TILESET_IMAGE_TYPE = "tilesetImageType"; //$NON-NLS-1$

    /**
     * Default tileset size
     */
    public static final Integer DEFAULT_TILE_SIZE = 265;

    /**
     * The default Tileset image type
     */
    public static final String DEFAULT_IMAGE_TYPE = "image/png"; //$NON-NLS-1$

    /**
     * The default feature attribute to be displayed in a popup when a tool action returns multiple
     * features.
     */
    public static final String FEATURE_ATTRIBUTE_NAME = "featureAttributeName"; //$NON-NLS-1$

    /**
     * The scale factor to be used during UI single feature selection search like commands (select,
     * edit etc.).
     */
    public static final String FEATURE_SELECTION_SCALEFACTOR = "featureSelectionRadius"; //$NON-NLS-1$

    /**
     * default scale factor for point selection tool (Default is
     * {@value #DEFAULT_FEATURE_SELECTION_SCALEFACTOR}) see
     * <code>IAbstractContext.getBoundingBox( Point screenLocation, int scalefactor)</code>
     */
    public static final int DEFAULT_FEATURE_SELECTION_SCALEFACTOR = 6;

}