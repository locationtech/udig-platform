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
    public static final String P_TILESET_RESOLUTIONS = "tilesetResolutions"; //$NON-NLS-1$
    
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
    
}
