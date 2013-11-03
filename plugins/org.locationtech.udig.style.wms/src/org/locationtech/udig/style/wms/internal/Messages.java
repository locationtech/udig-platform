/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.wms.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.style.wms.internal.messages"; //$NON-NLS-1$

	public static String WMSStyleConfigurator_abstract_format;

	public static String WMSStyleConfigurator_featureStyles_format;

	public static String WMSStyleConfigurator_no_info;

	public static String WMSStyleConfigurator_style_label;

	public static String WMSStyleConfigurator_styleURL_format;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
