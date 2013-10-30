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
package eu.udig.catalog.teradata.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eu.udig.catalog.teradata.internal.messages"; //$NON-NLS-1$

	public static String GetHTMLDriverMsg;
	public static String GetHTMLCopyPluginMsg;
	public static String GetDriverMsg;
	public static String GetCopyPluginMsg;

	public static String GetDriverTitle;

	public static String TeradataGeoResource_hostPageTitle;
	public static String TeradataServiceExtension_badURL;
	public static String TeradataGeoResource_error_layer_bounds;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
