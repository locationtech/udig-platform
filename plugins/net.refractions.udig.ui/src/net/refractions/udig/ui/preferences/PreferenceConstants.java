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
package net.refractions.udig.ui.preferences;

import net.refractions.udig.ui.FeatureTypeEditor;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

    /**
     * Work around for a bug in SWT on linux.  Allows the use of advancedGraphics to be turned off.
     */
	public static final String P_ADVANCED_GRAPHICS = "advancedGraphics"; //$NON-NLS-1$
    
    /**
     * Fully qualified extension id pointing to an extension instance of point 
     * "net.refractions.udig.ui.workbenchConfigurations". The value specified by
     * the extension id will be called upon to configure the workbench when it 
     * starts up.
     */
    public static final String P_WORKBENCH_CONFIGURATION = "workbenchConfiguration"; //$NON-NLS-1$
    
    /**
     * Fully qualified extension id pointing to an instance of extension point
     * "net.refractions.udig.ui.menuBuilders". The MenuBuilder specified by the
     * extension will be called by uDig to configure the menubar and coolbar.
     */
    public static final String P_MENU_BUILDER = "menuBuilder"; //$NON-NLS-1$
    
    /**
     * The CRS to use by default when creating a new SimpleFeatureType with the {@link FeatureTypeEditor}
     * 
     * It may be an EPSG code like "EPSG:3005" or wkt 
     */
    public static final String P_DEFAULT_GEOMEMTRY_CRS = "P_DEFAULT_GEOMEMTRY_CRS"; //$NON-NLS-1$

	public static final String P_DEFAULT_PERSPECTIVE = "defaultPerspective"; //$NON-NLS-1$

	/**
	 * The default units tools that display units should use.
	 * For now there are two options: Metric and Imperial
	 * <p>Used by such things as the distance tool.</p>
	 */
	public static final String P_DEFAULT_UNITS = "P_DEFAULT_UNITS"; //$NON-NLS-1$
	/**
	 * Represents metric units
	 */
	public static final String METRIC_UNITS = "METRIC_UNITS"; //$NON-NLS-1$
    /**
     * Represents imperial units
     */
    public static final String IMPERIAL_UNITS = "IMPERIAL_UNITS"; //$NON-NLS-1$
    /**
     * Represents automatically determined units
     */
    public static final String AUTO_UNITS = "AUTO_UNITS"; //$NON-NLS-1$
	
    /**
     * Controls whether the TipDialog should be displayed by default on startup.
     * Defaults to true, but can be set in plugin_customization.ini. 
     * Valid values: "true" or "false"
     */
    public static final String P_SHOW_TIPS = "showTips"; //$NON-NLS-1$

    public static final String P_DEFAULT_CHARSET = "P_DEFAULT_CHARSET"; //$NON-NLS-1$

}
