package net.refractions.udig.project.preferences;

import net.refractions.udig.project.internal.render.impl.ScaleUtils;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final String P_ANTI_ALIASING = "antiAliasingPreference"; //$NON-NLS-1$

	public static final String P_TRANSPARENCY = "transparencyPreference"; //$NON-NLS-1$

	/**
	 * The default crs assigned to a new map.  -1 indicates the crs of the first layer added should be used.
	 */
	public static final String P_DEFAULT_CRS = "defaultCRSPreference"; //$NON-NLS-1$

    public static final String P_DEFAULT_PALETTE = "defaultPalette"; //$NON-NLS-1$

    public static final String P_BACKGROUND = "backgroundColor"; //$NON-NLS-1$

    public static final String P_TILING_RENDERER = "tilingRendererActivated"; //$NON-NLS-1$
    
    public static final String P_SELECTION_COLOR = "P_SELECTION_COLOR"; //$NON-NLS-1$
    public static final String P_SELECTION2_COLOR = "P_SELECTION2_COLOR"; //$NON-NLS-1$

    public static final String P_REMOVE_LAYERS = "P_REMOVE_LAYERS"; //$NON-NLS-1$

    public static final String P_HIGHLIGHT = "LayerPreferencePage.HighlightBehaviour"; //$NON-NLS-1$
    
    public static final String P_HIGHLIGHT_NONE = "NONE"; //$NON-NLS-1$
    public static final String P_HIGHLIGHT_FOREGROUND = "FOREGROUND"; //$NON-NLS-1$
    public static final String P_HIGHLIGHT_BACKGROUND = "BACKGROUND"; //$NON-NLS-1$

    public static final String P_WARN_IRREVERSIBLE_COMMAND = "P_WARN_IRREVERSIBLE_COMMAND"; //$NON-NLS-1$

    public static final String P_IRREVERSIBLE_COMMAND_VALUE = "P_IRREVERSIBLE_COMMAND_VALUE"; //$NON-NLS-1$
    
    public static final String P_STYLE_DEFAULT_PERPENDICULAR_OFFSET = "P_STYLE_DEFAULT_PERPENDICULAR_OFFSET"; //$NON-NLS-1$
    public static final String P_LAYER_RESOURCE_CACHING_STRATEGY = "P_LAYER_RESOURCE_CACHING_STRATEGY"; //$NON-NLS-1$

    public static final String P_PROJECT_DELETE_FILES = "P_PROJECT_DELETE_FILES"; //$NON-NLS-1$

    public static final String P_SHOW_ANIMATIONS = "P_SHOW_ANIMATIONS"; //$NON-NLS-1$

    public static final String P_MAX_UNDO = "P_MAX_UNDO"; //$NON-NLS-1$

    public static final String P_DEFAULT_FEATURE_EDITOR = "P_DEFAULT_FEATURE_EDITOR"; //$NON-NLS-1$
    

    /**
     * The on/off switch for using tiled rendering
     */
    public static final String P_TILED_RENDERING = "P_TILED_RENDERING";  //$NON-NLS-1$
    
    /**
     * The property value for the preferred scale to ZOOM IN when extents of the layer
     * are not really correct or too small to be zoomed and displayed.
     * <p>
     * The example: only one point exists in the layer and its extents are like 
     * (Xpoint, Ypoint, Xpoint, Ypoint) - so extents are collapsed to the point.
     * <p>
     * Used in "Zoom to extents" action.
     * 
     */
    public static final String P_MINIMUM_ZOOM_SCALE = "P_MINIMUM_ZOOM_SCALE"; //$NON-NLS-1$
    
    
    /**
     * If this property contains <code>true</code> then no matter what are the bounds of feature events,
     * all the viewport will be refreshed by render manager.
     * <p>
     * This is a customization property to avoid drawback effects because of non-uniform and not complete
     * implementation of feature events notifiers and bugs in rendering workflow.
     * 
     * The idea is to get the up-to-date picture with all changes in the layer no matter of feature
     * events that might cause refreshing only its bounding box.
     * 
     */
    public static final String P_FEATURE_EVENT_REFRESH_ALL = "P_FEATURE_EVENT_REFRESH_ALL"; //$NON-NLS-1$
    
    
    
    /**
     * This property says to <code>LabelCacheDefault</code> whether to ignore labels overlapping checkings
     * or to force them no matter what is a "spaceAround" parameter of the TextSymbolizer in SLD style.
     * <p>
     * If this property is <code>true</code> then no matter what is a SLD style parameters, all labels
     * are rendered.
     */
    public static final String P_IGNORE_LABELS_OVERLAPPING = "P_IGNORE_LABELS_OVERLAPPING"; //$NON-NLS-1$
    
    
    
    /**
     * The property forces to make checking for layers diplicating - from the same georesource - in context
     * of the map where new layers are added.
     * <p>
     * If the property is <code>true</code> then no duplicate layers are possible.
     */
    public static final String P_CHECK_DUPLICATE_LAYERS = "P_CHECK_DUPLICATE_LAYERS"; //$NON-NLS-1$

    /**
     * Controls the point at which zooming to the next level of closeness occurs.
     *
     * @see ScaleUtils#calculateClosestScale
     */
	public static final String P_ZOOM_REQUIRED_CLOSENESS = "P_ZOOM_REQUIRED_CLOSENESS";
}
