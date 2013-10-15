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
