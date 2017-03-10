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
package org.locationtech.udig.catalog.wms.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.catalog.wms.internal.messages"; //$NON-NLS-1$
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
	public static String WMSPreferencePage_wmstimeout;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
