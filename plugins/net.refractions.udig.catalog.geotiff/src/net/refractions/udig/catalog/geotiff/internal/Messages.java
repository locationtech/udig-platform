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
package net.refractions.udig.catalog.geotiff.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.catalog.geotiff.internal.messages"; //$NON-NLS-1$
	public static String GeoTiffGeoResource_connect;
    public static String GeoTiffServiceExtension_badExt;
    public static String GeoTiffServiceExtension_notExist;
    public static String GeoTiffServiceExtension_notFile;
    public static String GeoTiffServiceExtension_NotRead;
    public static String GeoTiffServiceExtension_notReadWError;
    public static String GeoTiffServiceExtension_NotTiff;
    public static String GeoTiffServiceExtension_NotTiff1;
    public static String GeoTiffServiceExtension_nullURL;
    public static String GeoTiffServiceExtension_unknown;
	public static String GeoTiffServiceImpl_connecting_to;
	public static String GeoTiffServiceImpl_loading_task_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
