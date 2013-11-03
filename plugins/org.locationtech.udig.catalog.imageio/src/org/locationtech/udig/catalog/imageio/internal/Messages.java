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
package org.locationtech.udig.catalog.imageio.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.catalog.imageio.internal.messages"; //$NON-NLS-1$
	public static String ImageServiceImpl_connecting_to;
	public static String ImageGeoResourceImpl_PrjUnavailable;
	public static String ImageServiceExtension_badFileExtension;
    public static String ImageServiceExtension_geotoolsDisagrees;
    public static String ImageServiceExtension_IllegalFilePart1;
    public static String ImageServiceExtension_IllegalFilePart2;
    public static String ImageServiceExtension_mustBeFIle;
    public static String ImageServiceExtension_noID;
    public static String ImageServiceExtension_or;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
