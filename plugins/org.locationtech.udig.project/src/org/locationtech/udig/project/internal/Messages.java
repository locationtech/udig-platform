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
package org.locationtech.udig.project.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.project.internal.messages"; //$NON-NLS-1$

	public static String ChangeCRSCommand_undoName;
	public static String ChangeCRSCommand_name;
    public static String CommandManager_warning2;
	public static String DeleteLayersCommand_name;
    public static String LayerImpl_resolveAdapter;
    public static String LayerImpl_unkownCRS;
	public static String PlaceHolder_error;
	public static String ProjectPlugin_saving_task_name;
	public static String ProjectRegistryImpl_load_error;
    public static String SelectLayerCommand_name;
    public static String SelectLayerCommand_selecting;
    public static String SelectLayerCommand_undoing;
	public static String SetScaleCommand_name;
	public static String WriteFeatureChangesCommand_rollbackTask;
	public static String WriteEditFeatureCommand_rollbackTask;
	public static String WriteFeatureChangesCommand_commandName;
	public static String WriteEditFeatureCommand_runTask;
	public static String WriteFeatureChangesCommand_runTask;
	public static String MapImpl_CommandStack;
	public static String MapImpl_NavigationCommandStack;
	public static String CopyFeaturesCommand_undo;
	public static String CopyFeaturesCommand_name;
	public static String CommandManager_undo;
	public static String CommandManager_redo;
	public static String CommandManager_0;
	public static String LayerImpl_connectionFailed;
	public static String RollbackCommand_name;
	public static String CommitCommand_commitCommandName;
	public static String CreateLayerCommand_name;
	public static String CreateMapCommand_commandname;
	public static String CreateMapCommand_defaultname;
	public static String CreateLayerCommand_badID2;
	public static String CreateLayerCommand_badID;
	public static String UDIGTransaction_closeException;
	public static String UDIGTransaction_rollbackException;
	public static String UDIGTransaction_commitException;
	public static String TilingRenderer_disposedError;
	public static String StopRenderCommand_0;
	public static String UDIGFeatureStore_1;
	public static String UDIGFeatureStore_0;
	public static String NullGeoResource_0;
	public static String LayerImpl_status;
	public static String LayerImpl_unknown;
	public static String ProjectImpl_executor;
	public static String CommandManager_warning;
	public static String CommandManager_toggleMessage;
	public static String CommandManager_ProgressMonitor;
	public static String CommandManager_warningTitle;
	public static String AddFeatureCommand_name;
	public static String AddLayersCommand_name;
	public static String AddLayerCommand_Name;
	public static String CompositeRendererExecutorImpl_0;
	public static String SelectCommand_name;
	public static String SelectionListener_0;
	public static String SelectionListener_SelectionTimer;
	public static String ProjectImpl_commandManagerName;
	public static String ProjectRegistry_defaultName;
	public static String Styling_name;
	public static String Styling_default;
	public static String Styling_pointStyle;
	public static String Styling_blackLine_semitransparentYellowFill;
	public static String Styling_blackLine_semitransparentBlueFill;
	public static String Styling_blackLine_greenFill;
	public static String Styling_blackLine_blueFill;
	public static String Styling_blackLine;
	public static String Styling_greenLine;
	public static String Styling_blueLine;
	public static String ResetEditFeatureCommand_0;
	public static String RendererImpl_selectionFor;
	public static String RenderExecutorImpl_message;
	public static String RenderExecutorImpl_title;
	public static String NoSelectCommand_cancelSelections;
	public static String BBoxSelectionCommand_boxSelection;
	public static String FIDSelectCommand_featureSelection;
	public static String SetEditFeatureCommand_setCurrentEditFeature;
	public static String CreateLayerCommand_illegalRollback2;
	public static String CreateLayerCommand_illegalRollback;
	public static String CreateFeatureCommand_createFeature;
	public static String CreateFeatureCommand_error_message;
	public static String CreateFeatureCommand_error_title;
	public static String PanCommand_pan;
	public static String ZoomCommand_zoom;
	public static String SetAttributeCommand_setFeatureAttribute;
	public static String SetViewportCenterCommand_setViewCenter;
	public static String SetViewportBBoxCommand_setViewArea;
	public static String SetViewportHeight_setViewHeight;
	public static String SetApplicabilityCommand_name;
	public static String SetViewportWidth_setViewWidth;
	public static String ZoomExtentCommand_name;
	public static String DeleteManyFeaturesCommand_name;
	public static String DeleteLayerCommand_deleteLayer;
	public static String DeleteFeatureCommand_deleteFeature;
	public static String RenderExecutorImpl_1;
	public static String RenderExecutorImpl_2;
	public static String EditManagerImpl_rollback_message;
	public static String EditManagerImpl_commit_message;
	public static String ExportProjectWizard_Title;
	public static String ExportProjectWizard_Destination2;
	public static String ExportProjectWizard_Exporting;
	public static String ExportSelectionPage_Destination;
	public static String ExportSelectionPage_MissingDir;
	public static String ExportSelectionPage_Project;
	public static String ExportSelectionPage_SelectProject;
	public static String ExportSelectionPage_ExportProject;
	public static String SaveProject_Destination;
    public static  String SaveProject_Export;
    public static  String SaveProject_Overwrite;
    public static  String SaveProject_Success;
    public static  String SaveProject_Fail;
	
    public static String AddLayerItemsCommand_name;
    public static String AddLayerItemCommand_Name;
    public static String AddFolderItemCommand_Name;

    public static String DeleteLayerItemCommand_Name;

    public static String DeleteLayerItemsCommand_Name;
    public static String DeleteFolderItemCommand_Name;
    
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
