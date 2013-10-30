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
