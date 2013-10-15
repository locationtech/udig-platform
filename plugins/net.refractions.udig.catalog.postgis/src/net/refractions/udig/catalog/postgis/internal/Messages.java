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
package net.refractions.udig.catalog.postgis.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.catalog.postgis.internal.messages"; //$NON-NLS-1$

	public static String PostGISServiceExtension_badURL;

    public static String PostGisWizardPage_0;
	public static String PostGisWizardPage_title;
	public static String PostGisWizardPage_button_looseBBox_tooltip;
	public static String PostGisWizardPage_button_looseBBox_text;
	public static String PostGisWizardPage_button_wkb_tooltip;
	public static String PostGisWizardPage_button_wkb_text;
	public static String PostGISGeoResource_error_layer_bounds;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
