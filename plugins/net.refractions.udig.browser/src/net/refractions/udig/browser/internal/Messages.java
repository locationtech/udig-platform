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
package net.refractions.udig.browser.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.browser.internal.messages"; //$NON-NLS-1$
	public static String BrowserContainerView_back_text;
	public static String BrowserContainerView_back_tooltip;
	public static String BrowserContainerView_forward_text;
	public static String BrowserContainerView_forward_tooltip;
    public static String BrowserContainerView_loadingMessage;
	public static String BrowserContainerView_pageCount;
	public static String BrowserContainerView_refresh;
	public static String BrowserContainerView_tabTitle;
	public static String CatalogueBrowserWizard_windowTitle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
