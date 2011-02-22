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
package net.refractions.udig.catalog.worldimage.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.catalog.worldimage.internal.messages"; //$NON-NLS-1$
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
