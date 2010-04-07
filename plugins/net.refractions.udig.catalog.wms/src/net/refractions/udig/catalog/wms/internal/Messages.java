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
package net.refractions.udig.catalog.wms.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.catalog.wms.internal.messages"; //$NON-NLS-1$
	public static String WMSCServiceExtension_nottiled;
    public static String WMSCWizardPage_WMSCTitle;
    public static String WMSGeoResourceImpl_bounds_unavailable;
	public static String WMSGeoResourceImpl_downloading_icon;
	public static String WMSGeoResourceImpl_acquiring_task;
    public static String WMSServiceExtension_badService;
    public static String WMSServiceExtension_needsKey;
    public static String WMSServiceExtension_nullURL;
    public static String WMSServiceExtension_nullValue;
    public static String WMSServiceExtension_protocol;
	public static String WMSWizardPage_connectionProblem;
	public static String WMSWizardPage_serverConnectionError;
	public static String WMSWizardPage_title;
	public static String WMSServiceImpl_broken;
	public static String WMSWizardPage_error_invalidURL;
	public static String WMSServiceImpl_could_not_connect;
	public static String WMSWizardPage_label_url_text;
	public static String WMSServiceImpl_connecting_to;
	public static String WMSCTileUtils_preloadtitle;
	public static String WMSCTileUtils_preloadtask;
	public static String WMSCTileUtils_preloadtasksub;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
