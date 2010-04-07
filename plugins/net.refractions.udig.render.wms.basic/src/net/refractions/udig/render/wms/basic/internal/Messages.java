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
package net.refractions.udig.render.wms.basic.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.render.wms.basic.internal.messages"; //$NON-NLS-1$
	public static String BasicWMSRenderer2_error;
	public static String BasicWMSRenderer2_errorObtainingImage;
	public static String BasicWMSRenderer2_no_layers_to_render;
	public static String BasicWMSRenderer2_unable_to_decode_image;
    public static String BasicWMSRendererPreferencePage_setOrder;
	public static String BasicWMSRendererPreferencePage_useDefaults;
	public static String BasicWMSRenderer2_refreshJob_title;
	public static String BasicWMSRendererPreferencePage_warning;
    public static String projectionwarning0;
    public static String WMSCTilePreferencePage_pageTitle;
	public static String WMSCTilePreferencePage_pageDescription;
	public static String WMSCTilePreferencePage_caching_desc;
	public static String WMSCTilePreferencePage_inmemory;
	public static String WMSCTilePreferencePage_ondisk;
	public static String WMSCTilePreferencePage_disklabel;
	public static String WMSCTilePreferencePage_clearcachebtn;
	public static String WMSCTilePreferencePage_clearcacheConfirm;
	public static String WMSCTilePreferencePage_clearcacheError;
	public static String WMSCTilePreferencePage_clearcacheSuccess;
	public static String WMSCTilePreferencePage_maxConRequests;
    
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
