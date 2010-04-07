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
package net.refractions.udig.catalog.shp.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.catalog.shp.internal.messages"; //$NON-NLS-1$
	public static String ShpPreferencePage_rtree;
	public static String ShpPreferencePage_typechoice;
	public static String ShpPreferencePage_createindex;
	public static String ShpPreferencePage_description;
    public static String ShpServiceExtension_badExtension;
    public static String ShpServiceExtension_cantCreateURL;
    public static String ShpServiceImpl_indexing;
	public static String ShpServiceImpl_taskName;
	public static String ShpPreferencePage_quadtree;
	public static String ShpServiceImpl_dialogLabel;
	public static String ShpGeoResourceImpl_error_layer_bounds;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
