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
