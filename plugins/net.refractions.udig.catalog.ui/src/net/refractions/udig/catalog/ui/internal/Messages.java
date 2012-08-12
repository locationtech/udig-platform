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
package net.refractions.udig.catalog.ui.internal;

import java.lang.reflect.Field;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.catalog.ui.internal.messages"; //$NON-NLS-1$
	public static String action_remove_tooltip      ;
	public static String action_remove_label        ;
	public static String action_remove_image        ;
	public static String action_remove_description  ;
	public static String action_refresh_tooltip     ;
	public static String action_refresh_label       ;
	public static String action_refresh_image       ;
	public static String action_refresh_description ;
	public static String action_cancel_tooltip      ;
	public static String action_cancel_label        ;
	public static String action_cancel_image        ;
	public static String action_cancel_description  ;
	public static String action_add_tooltip         ;
	public static String action_add_label           ;
	public static String action_add_image           ;
    //public static String CatalogExport_fileNameNotChosen;
    public static String CatalogExport_reprojectError;
    //public static String CatalogExport_UnexpectedError;
    public static String CatalogExportWizard_0;
    public static String CatalogExportWizard_OverwriteDialogQuery;
    public static String CatalogExportWizard_SelectFile;
    public static String CatalogExportWizard_UnableToDelete;
    public static String CatalogExportWizard_WindowTitle;
    public static String CatalogTreeViewer_broken;
    public static String CatalogTreeViewer_permission;
	public static String ConnectionErrorState_error_name;
    public static String ConnectionFailurePage_canProcess;
    public static String ConnectionFailurePage_displayedTitle;
    public static String ConnectionFailurePage_message;
    public static String ConnectionFailurePage_selectChild;
    public static String ConnectionFailurePage_title;
    public static String ConnectionFailureState_name;
    public static String ExportPage_Destination;
    public static String ExportPage_Browse;
    public static String ExportPage_ExportDir;
    public static String ExportPage_ResourceList;
    public static String ReprojectingFeatureCollection_transformationError;
    public static String ReshapeOperation_2;
    public static String ReshapeOperation_3;
    public static String ReshapeOperation_4;
    public static String ReshapeOperation_5;
    public static String ReshapeOperation_6;
    public static String ReshapeOperation_createTempSpaceTask;
    public static String ReshapeOperation_DialogText;
    public static String ReshapeOperation_noAction;
    public static String ReshapeOperation_task;
    
    public static String ResourceSelectionPage_brokenReportError;
    public static String ResourceSelectionPage_brokenUnknown;
    public static String ResourceSelectionPage_connectedButNoResources;
    public static String ResourceSelectionPage_description;
    public static String ResourceSelectionPage_noPermission;
    public static String ResourceSelectionPage_noServices;
    public static String ResourceSelectionPage_NumLayersSelected;
	public static String ResourceSelectionPage_searching;
	public static String Workflow_task_name;
	public static String WorkflowWizardDialog_importTask ;
	public static String WorkflowWizard_noStates         ;
	public static String WorkflowWizard_noPage           ;
	public static String WorkflowWizard_PageTransitionError;
	public static String Workflow_busy ;
	//public static String SearchView_wait          ;
	public static String SearchView_title         ;
	public static String SearchView_server        ;
	public static String SearchView_searching_for ;
	public static String SearchView_searching     ;
	public static String SearchView_prompt        ;
	//public static String SearchView_notFound      ;
	public static String SearchView_name          ;
	public static String SearchView_keywords      ;
	//public static String SearchView_instructions  ;
	public static String SearchView_description   ;
	public static String SearchView_default       ;
	public static String SearchView_bboxTooltip   ;
	public static String SearchView_bbox          ;
	public static String ResourceSelectionState_taskName  ;
	public static String ResourceSelectionState_stateName ;
	public static String ResourceSelectionPage_title   ;
	public static String ResourceSelectionPage_message ;
	public static String ResolveTitlesDecorator_0 ;
	public static String ResolveLabelProvider_missingText ;
	public static String ResolveContentProvider_searching ;
	public static String OpenFilePage_pageTitle   ;
	//public static String OpenFilePage_backButton2 ;
	//public static String OpenFilePage_backButton1 ;
	public static String OpenFilePage_1           ;
	//public static String NewFeatureTypeState_name ;
	public static String NewFeatureTypeOp_2;
	public static String NewFeatureTypeOp_1;
	public static String NewFeatureTypeOp_0;
	public static String NewFeatureTypeOp_title             ;
	public static String NewFeatureTypeOp_shpTitle          ;
	public static String NewFeatureTypeOp_shpMessage        ;
	public static String NewFeatureTypeOp_message           ;
	//public static String NewFeatureTypeOp_cantWriteTitle    ;
	//public static String NewFeatureTypeOp_cantWriteMessage1 ;
	public static String FileConnectionPage_taskname ;
    public static String FileConnectionPage_waitMessage;
	//public static String FileChooserWizardPage_title                       ;
	//public static String FileChooserWizardPage_taskname                    ;
	//public static String FileChooserWizardPage_subtask_name                ;
	//public static String FileChooserWizardPage_rootDirectory_label_tooltip ;
	//public static String FileChooserWizardPage_rootDirectory_label_text    ;
	//public static String FileChooserWizardPage_directoryDialog_text        ;
	//public static String FileChooserWizardPage_browseButton_text           ;
	public static String DataSourceSelectionState_name ;
	public static String DataSourceSelectionPage_pageTitle      ;
	public static String DataSourceSelectionPage_defaultMessage ;
	//public static String DataImportWizard_importErrorMessage ;
    public static String DataBaseRegistryWizardPage_databaseMessage        ;
	public static String DataBaseRegistryWizardPage_schemaMessage          ;
	public static String DataBaseRegistryWizardPage_recent_tooltip   ;
	public static String DataBaseRegistryWizardPage_label_recent_text      ;
    public static String DataBaseRegistryWizardPage_username_tooltip ;
    public static String DataBaseRegistryWizardPage_label_username_text    ;
	public static String DataBaseRegistryWizardPage_schema_tooltip   ;
	public static String DataBaseRegistryWizardPage_label_schema_text      ;
	public static String DataBaseRegistryWizardPage_port_tooltip     ;
	public static String DataBaseRegistryWizardPage_label_port_text        ;
	public static String DataBaseRegistryWizardPage_password_tooltip ;
	public static String DataBaseRegistryWizardPage_label_password_text    ;
	public static String DataBaseRegistryWizardPage_host_tooltip     ;
	public static String DataBaseRegistryWizardPage_label_host_text        ;
	public static String DataBaseRegistryWizardPage_database_tooltip ;
	public static String DataBaseRegistryWizardPage_label_database_text    ;
    public static String DataBaseRegistryWizardPage_button_lookup_tooltip  ;
    public static String DataBaseRegistryWizardPage_button_lookup_text     ;
	public static String DataBaseRegistryWizardPage_label_advanced_tooltip ;
	public static String DataBaseRegistryWizardPage_label_advanced_text    ;
    public static String DataBaseRegistryWizardPage_button_connect_tooltip ;
    public static String DataBaseRegistryWizardPage_button_connect_text    ;
	//public static String CountOp_numberOfFeatures ;
	//public static String CountOp_featureCount     ;
	public static String ConnectionState_task         ;
	public static String ConnectionState_name         ;
	public static String ConnectionState_loadingLayer ;
	public static String ConnectionState_findLayers   ;
	public static String ConnectionPage_illegalHost  ;
	public static String ConnectionPage_genericError ;
	public static String ConnectionPage_badUsername  ;
	public static String ConnectionPage_badPassword  ;
	public static String ConnectionErrorPage_pageTitle       ;
	public static String ConnectionErrorPage_pageName        ;
	public static String ConnectionErrorPage_pageDescription ;
	public static String ConnectionErrorPage_message         ;
	public static String CatalogView_save_label ;
	public static String CatalogView_load_label ;
	//public static String CatalogUIPlugin_these_do_not_work       ;
	public static String CatalogUIPlugin_childContainerException ;
	public static String CatalogPreferencePage_fieldName   ;
	public static String CatalogPreferencePage_description ;
	//public static String CatalogImportWizard_title                 ;
	//public static String CatalogImportWizard_serviceErrorMessage   ;
	//public static String CatalogImportWizard_selectionErrorMessage ;
	//public static String CatalogImportWizard_resourceErrorMessage  ;
	//public static String CatalogImportWizard_pageName              ;
	//public static String CatalogImportWizard_pageMessage           ;
	//public static String CatalogImportWizard_importJobName         ;
	public static String CatalogImportDelegateWizard_windowTitle ;
	public static String CatalogImport_monitor_task                ;
    public static String NewServiceConnectionFactory_defaultGeom;
    public static String DependencyQueryPreferencePage_restartQuestion;
    public static String DependencyQueryPreferencePage_restartNeeded;
    public static String DependencyQueryPreferencePage_restartTitle;
    public static String DependencyQueryPreferencePage_notValid;
    public static String DependencyQueryPreferencePage_copyError;
    public static String DependencyQueryPreferencePage_archive;
    public static String DependencyQueryPreferencePage_browse;
    public static String DependencyQueryPreferencePage_fileExists;
    public static String DependencyQueryPreferencePage_fileNotFound;
    public static String CatalogExport_cannotWrite;
    //public static String CatalogExport_dupeTryAgain;
    //public static String CatalogExport_subtaskName;
    public static String CatalogExport_taskname;
    //public static String CatalogExport_duplicate;
    public static String CatalogExport_layerFail;
    public static String CatalogExport_exportLayersTask;
    //public static String CatalogExport_existsMulti;
    //public static String CatalogExport_exists;
    //public static String CatalogExport_save;
    public static String CatalogExport_taskName;
    public static String CountDownProgressMonitor_taskNamePart1;
    public static String CountDownProgressMonitor_taskNamePart2;
    public static String LayerSelectionPage_message;
    public static String LayerSelectionPage_title;

    
	public static String TransformDialog_Post_Action_Prompt;
    public static String TransformDialog_Title;


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

            image = CatalogUIPlugin.getDefault().getImageDescriptor(ePath);
            if (id != null) {
                CatalogUIPlugin.trace(id + ": '" + ePath + "' found " + id); //$NON-NLS-1$ //$NON-NLS-2$
                a.setImageDescriptor(image);
            }
            image = CatalogUIPlugin.getDefault().getImageDescriptor(dPath);
            if (id != null) {
                CatalogUIPlugin.trace(id + ": '" + dPath + "' found " + id); //$NON-NLS-1$ //$NON-NLS-2$
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
			CatalogUIPlugin.log("Error loading key " + fieldName, e); //$NON-NLS-1$
		}
		return null;
	}
}
