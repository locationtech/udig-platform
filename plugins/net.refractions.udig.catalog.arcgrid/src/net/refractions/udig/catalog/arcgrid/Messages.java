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
package net.refractions.udig.catalog.arcgrid;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.catalog.arcgrid.messages"; //$NON-NLS-1$
	public static String ArcGridGeoResource_connect;
    public static String ArcGridServiceExtension_badExt;
    public static String ArcGridServiceExtension_notExist;
    public static String ArcGridServiceExtension_notFile;
    public static String ArcGridServiceExtension_nullURL;
    public static String ArcGridServiceExtension_unknown;
	public static String ArcGridServiceImpl_connecting_to;
	public static String ArcGridServiceImpl_loading_task_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
