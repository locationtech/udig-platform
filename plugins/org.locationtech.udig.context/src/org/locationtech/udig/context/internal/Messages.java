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
package net.refractions.udig.context.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.context.internal.messages"; //$NON-NLS-1$
	public static String WizardDataTransferPage_error_transferError;
	public static String WizardDataTransferPage_group_options_text;
	public static String WizardDataTransferPage_dialog_title;
	public static String ContextExportWizard_prompt_error_fileExists;
	public static String MapExportPage_prompt_ready;
	public static String MapExportPage_prompt_error_fileExists;
	public static String MapExportPage_checkbox_overwrite_text;
	public static String MapExportPage_group_options_text;
	public static String MapExportPage_dialog_import_text;
	public static String MapExportPage_button_browse_text;
	public static String MapExportPage_label_url_text;
	public static String MapExportPage_prompt_initial;
	public static String URLWizardPage_prompt_error_fileNotExist;
	public static String URLWizardPage_prompt_import;
	public static String URLWizardPage_dialog_text;
	public static String URLWizardPage_button_browse_text;
	public static String URLWizardPage_label_url_text;
	public static String URLWizardPage_prompt_initial;
	public static String ContextImportWizard_wizard_title;
	public static String ContextExportWizard_prompt_done;
	public static String ContextImportWizard_wizard_name;
	public static String ContextImportWizard_adding;
	public static String ContextExportWizard_page_name;
	public static String ContextImportWizard_importing;
	public static String ContextExportWizard_page_title;
	public static String ContextImportWizard_parsingxml;
	public static String ContextImportWizard_task_connecting;
	public static String ContextImportWizard_task_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
