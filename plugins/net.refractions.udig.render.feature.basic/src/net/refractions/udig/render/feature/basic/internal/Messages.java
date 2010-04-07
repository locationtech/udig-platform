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
package net.refractions.udig.render.feature.basic.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.render.feature.basic.internal.messages"; //$NON-NLS-1$
	public static String BasicFeatureRenderer_0;
	public static String BasicFeatureRenderer_layer_has_no_geometry;
	public static String BasicFeatureRenderer_noFeatures;
	public static String BasicFeatureRenderer_rendering_status;
	public static String BasicFeatureRenderer_renderingProblem;
	public static String BasicFeatureRenderer_request_timed_out;
    public static String BasicFeatureRenderer_warning1;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
