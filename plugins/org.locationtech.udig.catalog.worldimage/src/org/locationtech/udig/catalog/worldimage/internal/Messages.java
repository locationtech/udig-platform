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
package org.locationtech.udig.catalog.worldimage.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.catalog.worldimage.internal.messages"; //$NON-NLS-1$
	public static String InMemoryCoverageLoader_close_button;
    public static String InMemoryCoverageLoader_message;
    public static String InMemoryCoverageLoader_msgTitle;
    public static String InMemoryCoverageLoader_restart_button;
    public static String WorldImageGeoResourceImpl_PrjUnavailable;
    public static String WorldImageServiceExtension_badFileExtension;
    public static String WorldImageServiceExtension_geotoolsDisagrees;
    public static String WorldImageServiceExtension_IllegalFilePart1;
    public static String WorldImageServiceExtension_IllegalFilePart2;
    public static String WorldImageServiceExtension_mustBeFIle;
    public static String WorldImageServiceExtension_needsFile;
    public static String WorldImageServiceExtension_noID;
    public static String WorldImageServiceExtension_or;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
