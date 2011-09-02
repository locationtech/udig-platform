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
package net.refractions.udig.ui.internal;

import java.lang.reflect.Field;

import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.ui.internal.messages"; //$NON-NLS-1$
	public static String AttributeValidator_missingAtt1;
    public static String AttributeValidator_missingAtt2;
    public static String AttributeValidator_restriction;
    public static String AttributeValidator_wrongType;
    public static String BooleanCellEditor_FALSE;
    public static String BooleanCellEditor_TRUE;
    public static String CharSetFieldEditor_select;
    public static String CharsetSelectionDialog_title;
    public static String CRSChooser_unknownWKT;
	public static String CRSChooserDialog_title;
	public static String ErrorManager_very_informative_error;
	public static String ExceptionDisplayer_very_informative_error;
    public static String FeatureTableContentProvider_loadedFeatures;
    public static String FeatureTableContentProvider_loading ;
    public static String FeatureTableContentProvider_outOfMemory;
    public static String FeatureTableContentProvider_sortTable;
    public static String FeatureTableContentProvider_unexpectedErro;
    public static String FeatureTableContentProvider_probablecharseterror;
    public static String FeatureTableContentProvider_updateTaskName;
    public static String FeatureTableContentProvider_updatingFeatures;
    public static String FeatureTableControl_1;
    public static String FeatureTableControl_loading1;
    public static String FeatureTableControl_loading2;
    public static String FeatureTableControl_noEditor1;
    public static String FeatureTableControl_noEditor2;
    public static String FeatureTableControl_warningMessage;
    public static String FeatureTableControl_warningTitle;
    public static String FeatureTableControl_warningToggle;
	public static String FeatureTableSelectionProvider_loading_new_selection;
    public static String FeatureTypeEditor_booleanType;
    public static String FeatureTypeEditorDialog_ShellTitle;
    public static String FileExportOperation_allFiles;
    public static String FileExportOperation_defaultName;
    public static String FileExportOperation_finishStatus;
    public static String FileExportOperation_prompt;
    public static String FileExportOperation_writingStatus;
    public static String OpAction_errorMessage;
    public static String OpAction_errorTitle;
    public static String OperationDialog_Message;
    public static String OperationDialog_Operate;
    public static String OperationDialog_PleaseSelect;
    public static String OperationDialog_Title;
	public static String OperationLabelProvider_unknown;
    public static String PlatformGIS_background;
	public static String RunOperationDialog_run_operation;
	public static String RuntimeFieldEditor_error;
    public static String RuntimeFieldEditor_locale;
    public static String RuntimeFieldEditor_maxheap;
    public static String RuntimeFieldEditor_memory_positive;
    public static String RuntimeFieldEditor_path_not_existing;
    public static String RuntimeFieldEditor_restart;
    public static String RuntimeFieldEditor_workspace_path;
    public static String SendLogDialog_contact;
    public static String SendLogDialog_contact_message;
    public static String SendLogDialog_description;
    public static String SendLogDialog_empty;
    public static String SendLogDialog_log;
    public static String SendLogDialog_notes;
    public static String SendLogDialog_notes_message;
    public static String SendLogDialog_reading;
    public static String SendLogDialog_submit;
    public static String SendLogDialog_title;
    public static String SubmitIssueDialog_instructions;
    public static String SubmitIssueDialog_copy;
    public static String ShutdownTaskList_shutDown;
    public static String TransferPreference_transfer_preference_description;
	public static String FeatureTextTransfer_transfer_name;
	public static String FeatureTextTransfer_strategy_wkt_name;
	public static String FeatureTextTransfer_strategy_gml_name;
	public static String TipDialog_question;
	public static String TipDialog_shellText;
	public static String NewFeatureTypeOp_duplicateTypeName;
	public static String deleteAttributeAction_tooltip;
    public static String deleteAttributeAction_label;
    public static String deleteAttributeAction_description;
	public static String FeatureTypeEditor_newAttributeTypeDefaultName;
	public static String FeatureTypeEditor_newFeatureTypeName;
	public static String FeatureTypeEditor_typeColumnName;
	public static String FeatureTypeEditor_nameColumnName;
	public static String FeatureTableControl_loadingMessage;
	public static String FeatureTypeEditor_multiPolygonType;
	public static String FeatureTypeEditor_defaultNameAttributeName;
	public static String FeatureTypeEditor_defaultGeometryName;
	public static String FeatureTypeEditor_multiLineStringType;
	public static String FeatureTypeEditor_multiPointType;
	public static String FeatureTypeEditor_geometryType;
	public static String FeatureTypeEditor_polygonType;
	public static String FeatureTypeEditor_lineStringType;
	public static String FeatureTypeEditor_pointType;
	public static String FeatureTypeEditor_floatType;
	public static String FeatureTypeEditor_doubleType;
	public static String FeatureTypeEditor_longType;
	public static String FeatureTypeEditor_integerType;
	public static String FeatureTypeEditor_dateType;
	public static String FeatureTypeEditor_stringType;
	public static String UDIGApplication_error2;
	public static String UDIGApplication_error1;
	public static String UDIGApplication_title;
	public static String UDIGApplication_error;
	public static String UDIGDropHandler_jobName;
	public static String UDIGDropHandler_error;
	public static String OperationMenuFactory_menu_text;
	public static String AuthenticationDialog_dialog_title;
	public static String AuthenticationDialog_label_rememberPassword;
	public static String AuthenticationDialog_label_password;
	public static String AuthenticationDialog_label_username;
	public static String AuthenticationDialog_label_prompt;
	public static String UDIGDropHandler_performing_task;
	public static String UDIGWorkbenchAdvisor_welcome_text;
	public static String UDIGWorkbenchAdvisor_closeAllPerspectives_text;
	public static String UDIGWorkbenchAdvisor_closePerspective_text;
	public static String UDIGWorkbenchAdvisor_aboutUDig_text;
	public static String UDIGWorkbenchAdvisor_newWindow_text;
	public static String UDIGWorkbenchAdvisor_navigationMenu;
	public static String UDIGWorkbenchAdvisor_helpContents_text;
	public static String UDIGWorkbenchAdvisor_preferences_text;
	public static String UDIGWorkbenchAdvisor_resetPerspective_text;
	public static String UDIGWorkbenchAdvisor_open_perspective;
	public static String UDIGWorkbenchAdvisor_help;
	public static String UDIGWorkbenchAdvisor_layerMenu;
	public static String UDIGWorkbenchAdvisor_show_view;
	public static String UDIGWorkbenchAdvisor_window;
	public static String UDIGWorkbenchAdvisor_tools;
	public static String UDIGWorkbenchAdvisor_edit;
	public static String UDIGWorkbenchAdvisor_new;
	public static String UDIGWorkbenchAdvisor_file;
	public static String UDIGApplication_error_jai_warning_text;
	public static String UDIGApplication_error_jai_warning_title;
    public static String UDIGApplication_helpstring;
    public static String UDIGWorkbenchWindowAdvisor_classNotFound;
    public static String UDIGWorkbenchWindowAdvisor_specifiedButNotFound;
	public static String RuntimePreferences_desc;
	public static String UiPreferences_advancedGraphics_label;
    public static String UiPreferences_charset;
	public static String UiPreferences_description;
    public static String UiPreferences_ImperialUnits;
    public static String UiPreferences_MetricUnits;
    public static String UiPreferences_AutoUnits;
    public static String UiPreferences_UnitsLabel;
    public static String CRSChooser_tooltip;
    public static String CRSChooser_unnamed;
    public static String CRSChooser_keywordsLabel;
    public static String CRSChooser_tab_customCRS;
    public static String CRSChooser_tab_standardCRS;
    public static String CRSChooser_label_crs;
    public static String CRSChooser_label_crsWKT;
    
    public static String cancel_label;
    public static String cancel_image;
//    public static String cancel_description;
//    public static String cancel_tooltip;
    
    public static String orientation_horizontal_label;
    public static String orientation_horizontal_image;
//    public static String orientation_horizontal_description;
//    public static String orientation_horizontal_tooltip;
    
    public static String orientation_vertical_label;
    public static String orientation_vertical_image;
//    public static String orientation_vertical_description;
//    public static String orientation_vertical_tooltip;
    
    public static String orientation_single_label;
    public static String orientation_single_image;
//    public static String orientation_single_description;
//    public static String orientation_single_tooltip;
    
    public static String orientation_automatic_label;
    public static String orientation_automatic_image;
//    public static String orientation_automatic_description;
//    public static String orientation_automatic_tooltip;
    
    public static String addAttributeAction_label;

    public static String ExceptionDetailsEditorMessage;
    
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
	
	/**
     * Initialize the given Action from a ResourceBundle.
     * <p>
     * Makes use of the following keys:
     * <ul>
     * <li>prefix.label
     * <li>prefix.tooltip
     * <li>prefix.image
     * <li>prefix.description
     * </p>
     * <p>
     * Note: The use of a single image value is mapped to images for both the enabled and distabled
     * state of the IAction. the Local toolbar (elcl16/ and dlcl16/) is assumed if a path has not
     * been provided.
     * 
     * <pre><code>
     *  add_co.gif              (prefix.image)
     *     enabled: elcl16/add_co.gif
     *    disabled: dlcl/remove_co.gif
     *  tool16/discovery_wiz.16 (prefix.image)
     *     enabled: etool16/discovery_wiz.16
     *    disabled: etool16/discovery_wiz.16
     * </code></pre>
     * 
     * </p>
     * 
     * @param a action 
     * @param id used for binding (id.label, id.tooltip, ...)
     * @deprecated not safe, using this will cause bugs.  jeichar
     */
    public static void initAction( IAction a, String id ) {
        String labelKey = "_label"; //$NON-NLS-1$
        String tooltipKey = "_tooltip"; //$NON-NLS-1$
        String imageKey = "_image"; //$NON-NLS-1$
        String descriptionKey = "_description"; //$NON-NLS-1$
        if (id != null && id.length() > 0) {
            labelKey = id + labelKey;
            tooltipKey = id + tooltipKey;
            imageKey = id + imageKey;
            descriptionKey = id + descriptionKey;
        }
        String s = bind(labelKey);
        if (s != null)
            a.setText(s);
        s = bind(tooltipKey);
        if (s != null)
            a.setToolTipText(s);
        s = bind(descriptionKey);
        if (s != null)
            a.setDescription(s);
        String relPath = bind(imageKey);
        if (relPath != null && !relPath.equals(imageKey) && relPath.trim().length() > 0) {
            String dPath;
            String ePath;
            if (relPath.indexOf("/") >= 0) { //$NON-NLS-1$
                String path = relPath.substring(1);
                dPath = 'd' + path;
                ePath = 'e' + path;
            } else {
                dPath = "dlcl16/" + relPath; //$NON-NLS-1$
                ePath = "elcl16/" + relPath; //$NON-NLS-1$
            }
            ImageDescriptor image;

            image = UiPlugin.getDefault().getImageDescriptor(ePath);
            if (id != null) {
                a.setImageDescriptor(image);
            }
            image = UiPlugin.getDefault().getImageDescriptor(dPath);
            if (id != null) {
                a.setDisabledImageDescriptor(image);
            }
        }
    }

	private static String bind(String fieldName) {
		Field field;
		try {
			field = Messages.class.getDeclaredField(fieldName);
			return (String) field.get(null);
		} catch (Exception e) {
			UiPlugin.log("Error loading key " + fieldName, e); //$NON-NLS-1$
		}
		return null;
	}
}
