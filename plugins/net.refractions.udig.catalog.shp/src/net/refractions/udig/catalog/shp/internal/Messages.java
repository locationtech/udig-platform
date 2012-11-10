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
