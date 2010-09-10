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
package net.refractions.udig.catalog.wmt.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.catalog.wmt.internal.messages"; //$NON-NLS-1$
		
	public static String Properties_Layer_Title;
    public static String Properties_Layer_GroupBox;
    public static String Properties_Layer_AutomaticSelection;
    public static String Properties_Layer_ManualSelection;
    public static String Properties_Layer_UseZoomLevel;
    public static String Properties_Layer_Recommended;
    public static String Properties_Layer_Warning;
    public static String Properties_Layer_Error;
    
    public static String Preferences_ScaleFactor_Title;
    public static String Preferences_ScaleFactor_FastRendering;
    public static String Preferences_ScaleFactor_HighestQuality;
    public static String Preferences_ScaleFactor_Description;
    public static String Preferences_TileLimit_Title;
    public static String Preferences_TileLimit_Warning;
    public static String Preferences_TileLimit_Error;
    public static String Preferences_TileLimit_Description;
    
    public static String ZoomLevelSwitcher_Layer; 
    public static String ZoomLevelSwitcher_ZoomLevel;
    public static String ZoomLevelSwitcher_ZoomIn;
    public static String ZoomLevelSwitcher_ZoomOut;
    
    public static String Wizard_Title;
    public static String Wizard_CloudMade_StyleFromGroup;
    public static String Wizard_CloudMade_GroupCloudMade;
    public static String Wizard_CloudMade_GroupFeatured;
    public static String Wizard_CloudMade_StyleFromId;
    public static String Wizard_CloudMade_StyleId;
    public static String Wizard_CloudMade_DefaultStyleId;
    public static String Wizard_CloudMade_RefreshPreview;
    public static String Wizard_CloudMade_StyleEditorInfo;
    public static String Wizard_CloudMade_Preview;
    public static String Wizard_CloudMade_PreviewName;
    public static String Wizard_CloudMade_PreviewId;
    public static String Wizard_CloudMade_PreviewAuthor;
    public static String Wizard_CloudMade_PreviewGetFullMap;
    
    public static String Wizard_Osm_Info;
    public static String Wizard_Osm_InfoLink;
    
    public static String Wizard_Mq_Info;
    public static String Wizard_Mq_InfoLink;
    
    public static String Wizard_Ww_Example_Title;
    public static String Wizard_Ww_Example_Demis_Title;
    public static String Wizard_Ww_Example_Demis_Info;
    public static String Wizard_Ww_Example_Demis_Link;
    public static String Wizard_Ww_Example_Demis_LinkUrl;
    
    public static String Wizard_Nasa_Info;
    public static String Wizard_Nasa_InfoLink;
    
    public static String Wizard_WW_Title;
    public static String Wizard_WW_Description;
    public static String Wizard_WW_Url;
    public static String Wizard_WW_LocalFile;
    public static String Wizard_WW_SelectFile;
    public static String Wizard_WW_Connecting;
    public static String Wizard_WW_ConnectionProblem;
    public static String Wizard_WW_Error_InvalidURL;
    
    public static String Wizard_CS_Title;
    public static String Wizard_CS_Description;
    public static String Wizard_CS_UrlTileNames;
    public static String Wizard_CS_Url;
    public static String Wizard_CS_UrlDefault;
    public static String Wizard_CS_ZoomLevel;
    public static String Wizard_CS_Min;
    public static String Wizard_CS_Max;
    public static String Wizard_CS_AvailableTags;
    public static String Wizard_CS_TagZoom;
    public static String Wizard_CS_TagX;
    public static String Wizard_CS_TagY;
    
    public static String WWServiceExtension_NeedsKey;
    public static String WWServiceExtension_NullValue;
    public static String WWServiceExtension_NullURL;
    public static String WWServiceExtension_Protocol;
    
    public static String WWService_Connecting_to;
    public static String WWService_Could_not_connect;
    public static String WWService_NoValidFile;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
