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
package org.locationtech.udig.tools.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.tools.internal.messages"; //$NON-NLS-1$
	public static String CommitTool_Error_message;
	public static String CommitTool_error_shell_title;
	public static String CursorPosition_infinity;
	public static String CursorPosition_not_a_number;
	public static String CursorPosition_transformError;
	public static String CursorPosition_tooltip;
    public static String Navigation_Description;
    public static String Navigation_Title;
    public static String Navigation_Scale;
    public static String Navigation_Tiled;
	public static String ScrollZoom_scroll_zoom;


	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
