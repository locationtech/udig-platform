/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.project.ui.preferences;


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
    
}
