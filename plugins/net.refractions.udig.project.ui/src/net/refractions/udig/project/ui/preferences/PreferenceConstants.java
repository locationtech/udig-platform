package net.refractions.udig.project.ui.preferences;


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
     * Indicates the variable to threat as long-click speed.
     */
    public final static String MOUSE_LONGCLICK_SPEED = "mouseLongClickSpeed";  //$NON-NLS-1$  
    
    /**
     * The default speed for mouse double click in milliseconds.
     */
    public static final int DEFAULT_DOUBLECLICK_SPEED_MILLIS = 1000;

    /**
     * The default speed for mouse long click in milliseconds.
     */
    public static final int DEFAULT_LONGCLICK_SPEED_MILLIS = 5000;
    
}
