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
package net.refractions.udig.printing.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.printing.ui.internal.messages"; //$NON-NLS-1$
	public static String Landscape_Template_Name;
    public static String MapPart_defaultMapTitle;
	public static String MapEditPolicy_label;
	public static String MapTreePart_mapLabel;
	public static String ConnectionCreateCommand_error_targetNull;
	public static String ConnectionCreateCommand_error_sourceNull;
	public static String ConnectionReconnectCommand_label_endpoint;
	public static String ConnectionReconnectCommand_label_startpoint;
	public static String ConnectionReconnectCommand_error_unreacheable;
	public static String ConnectionReconnectCommand_error_nullConnection;
	public static String ConnectionDeleteCommand_label;
	public static String RenameLabelCommand_label;
	public static String DeleteCommand_delete;
	public static String SetConstraintCommand_label;
	public static String PageEditorPaletteFactory_components_title;
	public static String PageEditorPaletteFactory_controlGroup_title;
	public static String PageEditor_error_nullEditor;
	public static String PrintingPreferences_label_defaultTemplate;
	public static String PrintAction_jobStatus;
	public static String PrintAction_jobTitle;
	public static String PrintAction_pageError;
	public static String CreatePageAction_error_cannotFindDefaultTemplate;
	public static String CreatePageAction_dialog_message;
	public static String CreatePageAction_newPageName;
	public static String CreatePageAction_dialog_title;
	public static String CreatePageAction_printError_text;
	public static String CreatePageAction_printError_title;
	public static String EditMapAction_action_tooltip;
	public static String EditMapAction_action_text;
	public static String BasicTemplate_name;
	public static String BasicTemplate_label_defaultTitle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
