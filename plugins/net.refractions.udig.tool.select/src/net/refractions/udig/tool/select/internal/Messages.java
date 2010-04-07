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
package net.refractions.udig.tool.select.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.tool.select.internal.messages"; //$NON-NLS-1$
	public static String ArrowSelection_0;
    public static String TableView_0;
    public static String TableView_1;
	public static String TableView_allCheckText;
	public static String TableView_allToolTip;
    public static String TableView_compositeName;
    public static String TableView_deleteCommandName;
    public static String TableView_featureSelected;
    public static String TableView_noFeatureWarning;
    public static String TableView_promote_text;
    public static String TableView_promote_tooltip;
    public static String TableView_search;
    public static String TableView_search_any;
    public static String TableView_zoomToolText;
    public static String TableView_zoomToolToolTip;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
