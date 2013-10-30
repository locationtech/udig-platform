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
package org.locationtech.udig.catalog.oracle.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.catalog.oracle.internal.messages"; //$NON-NLS-1$
	public static String OraclePreferences_driverLabel;
	public static String OraclePreferences_title;
    public static String OracleServiceExtension_badUrl;
	public static String OracleSpatialWizardPage_connectionTask;
	public static String OracleGeoResource_error_layerBounds;
	public static String OracleServiceImpl_oracle_spatial;
    public static String OracleSpatialWizardPage_drivers;
    public static String OracleSpatialWizardPage_restart;
    public static String OracleSpatialWizardPage_wizardTitle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
