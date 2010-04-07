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
package net.refractions.udig.catalog.db2.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.catalog.db2.internal.messages"; //$NON-NLS-1$
	public static String DB2Preferences_driverNotValid;
	public static String DB2Preferences_renameError;
	public static String DB2Preferences_restartQuestion;
    public static String DB2ServiceExtension_notDB2URL;
    public static String DB2ServiceExtension_nullURL;
	public static String DB2WizardPage_title;
	public static String DB2WizardPage_warning;
	public static String DB2WizardPage_connectionTask;
	public static String DB2WizardPage_installDrivers;
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
