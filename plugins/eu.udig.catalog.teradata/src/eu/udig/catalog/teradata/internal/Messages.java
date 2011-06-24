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
