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
package org.locationtech.udig.catalog.db2.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.catalog.db2.internal.messages"; //$NON-NLS-1$
	public static String DB2Preferences_driverNotValid;
	public static String DB2Preferences_renameError;
	public static String DB2Preferences_restartQuestion;
    public static String DB2ServiceExtension_notDB2URL;
    public static String DB2ServiceExtension_nullURL;
	public static String DB2WizardPage_title;
	public static String DB2WizardPage_warning;
	public static String DB2WizardPage_connectionTask;
	public static String DB2WizardPage_installDrivers;
	public static String DB2WizardPage_button_lookup_text;	
	public static String DB2WizardPage_button_lookup_tooltip;		
	public static String DB2Preferences_restartNeeded;
	public static String DB2Preferences_restartTitle;
	public static String DB2Preferences_copyError;
	public static String DB2Preferences_licenceLabel;
	public static String DB2Preferences_archive;
	public static String DB2Preferences_browse;
	public static String DB2Preferences_fileExists;
	public static String DB2Preferences_fileNotFound;
	public static String DB2Preferences_driverLabel;
	public static String DB2Preferences_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
