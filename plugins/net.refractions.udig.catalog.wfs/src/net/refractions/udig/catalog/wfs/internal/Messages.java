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
package net.refractions.udig.catalog.wfs.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.catalog.wfs.internal.messages"; //$NON-NLS-1$
	public static String WFSRegistryWizardPage_label_timeout_tooltip;
	public static String WFSRegistryWizardPage_label_timeout_text;
	public static String WFSRegistryWizardPage_label_buffer_tooltip;
	public static String WFSRegistryWizardPage_label_buffer_text;
	public static String WFSRegistryWizardPage_label_post_tooltip;
	public static String WFSRegistryWizardPage_label_get_tooltip;
	public static String WFSRegistryWizardPage_advanced_tooltip;
	public static String WFSRegistryWizardPage_advanced_text;
	public static String WFSRegistryWizardPage_label_password_tooltip;
	public static String WFSRegistryWizardPage_label_password_text;
	public static String WFSRegistryWizardPage_label_username_tooltip;
	public static String WFSRegistryWizardPage_label_username_text;
	public static String WFSRegistryWizardPage_label_url_tooltip;
	public static String WFSRegistryWizardPage_label_url_text;
	public static String UDIGWFSDataStoreFactory_error_usernameAndPassword;
	public static String WFSRegistryWizardPage_problemConnecting;
	public static String WFSRegistryWizardPage_serverConnectionProblem;
    public static String WFSServiceExtension_badService;
    public static String WFSServiceExtension_protocol;
	public static String WFSServiceImpl_broken;
	public static String WFSServiceImpl_task_name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
