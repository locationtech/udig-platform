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
package org.locationtech.udig.tool.edit.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.tool.edit.internal.messages"; //$NON-NLS-1$
	public static String AddVertexTool_add_vertex;
	public static String AddVertexTool_select_feature;
    public static String AddVertexWhileCreatingBehaviour_illegal;
	public static String AdvancedBehaviourCommandHandler_disabledLabel;
	public static String AdvancedBehaviourCommandHandler_enabledLabel;
    public static String ClearEditBlackboardCommand_name;
	public static String DefaultCancelEditingCommand_name;
	public static String EditBlackboardUtil_changes_will_be_lost;
	public static String EditBlackboardUtil_changes_will_be_overwritten;
	public static String EditBlackboardUtil_clear;
	public static String EditBlackboardUtil_clear_selection;
	public static String EditBlackboardUtil_count_remaining;
	public static String EditBlackboardUtil_data_changed;
	public static String EditBlackboardUtil_ignore;
	public static String EditBlackboardUtil_ignore_change;
	public static String EditBlackboardUtil_update;
	public static String EditBlackboardUtil_Update_Selection;
	public static String EditBlackboardUtil_update_selection_confirmation;
	public static String EditBlackboardUtil_updating_selected_features;
    public static String EditToolPreferences_CreateFeaturePreference;
	public static String HoleTool_add_vertex;
	public static String HoleTool_add_vertex_or_finish;
	public static String HoleTool_create_feature;
	public static String HoleTool_move_vertex;
    public static String LegalShapeValidator_holeIntersection;
    public static String LegalShapeValidator_holeOutside;
    public static String LegalShapeValidator_holeOverlap;
    public static String LegalShapeValidator_shellIntersection;
	public static String LineTool_add_vertex;
	public static String LineTool_add_vertex_or_finish;
	public static String LineTool_move_vertex;
	public static String LineTool_select_or_create_feature;
	public static String PointTool_add_vertex;
	public static String PointTool_add_vertex_or_finish;
	public static String PointTool_move_vertex;
	public static String PointTool_select_or_create_feature;
	public static String PolygonTool_add_vertex;
	public static String PolygonTool_add_vertex_or_finish;
	public static String PolygonTool_create_feature;
	public static String PolygonTool_move_vertex;
	public static String RemoveVertexTool_remove_vertex;
	public static String RemoveVertexTool_select_feature;
	public static String SelectionTool_add_vertex;
	public static String SelectionTool_move_vertex;
	public static String SelectionTool_select;
	public static String SnapBehaviourCommandHandler_grid;
	public static String SnapBehaviourCommandHandler_all;
	public static String SnapBehaviourCommandHandler_current;
	public static String SnapBehaviourCommandHandler_selected;
	public static String SnapBehaviourCommandHandler_off;
	public static String PerformancelPreferences_hide_features;
	public static String PerformancelPreferences_fill_polygons;
	public static String PerformancelPreferences_fill_vertex;
	public static String PerformancelPreferences_description;
	public static String EditToolPreferences_feedbackColor;
	public static String EditToolPreferences_vertexOutline;
	public static String EditToolPreferences_selected;
	public static String EditToolPreferences_selectedGeom;
	public static String EditToolPreferences_advanced_editing_name;
	public static String EditToolPreferences_vertexDiameter;
	public static String EditToolPreferences_vertexOpacity;
	public static String EditToolPreferences_fillOpacity;
	public static String EditToolPreferences_grid;
	public static String EditToolPreferences_all;
	public static String EditToolPreferences_current;
	public static String EditToolPreferences_noSnapping;
	public static String EditToolPreferences_gridLabel;
	public static String EditToolPreferences_behaviour;
	public static String EditToolPreferences_snapRadius;
	public static String EditToolPreferences_description;
	public static String DeleteGlobalActionSetterActivator_tooltip;
	public static String DeleteGlobalActionSetterActivator_title;
	public static String DifferenceFeatureCommand_name;
	public static String DifferenceFeatureCommand_undoTaskMessage;
	public static String DifferenceFeatureCommand_runTaskMessage;
	public static String InsertVertexCommand_name2;
	public static String InsertVertexCommand_name1;
	public static String AddVertexCommand_name;
	public static String RemoveEditGeomCommand_rollbackTaskMessage;
	public static String AddFeaturesCommand_undoTaskMessage;
	public static String AddFeaturesCommand_taskMessage;
	public static String differenceOp_multiGeoms;
	public static String differenceOp_inputError;
	public static String differenceOp_inputError2;
	public static String differenceOp_inputError1;
	public static String MoveSelectionCommand_name;
	public static String AddMissingGeomsCommand_name;
	public static String AddGeomCommand_name;
	public static String SplitFeatureCommand_undoMessage;
	public static String SplitLineCommand_name;
	public static String SplitFeatureCommand_name;
	public static String ClearBlackboardCommand_name;
	public static String SetCurrentGeomCommand_name;
	public static String SetEditGeomChangedStateCommand_name;
	public static String SetGeomCommand_runTask;
	public static String SetGeomCommand_name;
	public static String SelectGeometryCommand_name;
	public static String SelectPointCommand_name;
	public static String SetEditStateCommand_name;
	public static String SetGeomCommand_undoTask;
	public static String StartEditingCommand_undo;
	public static String StartEditingCommand_name;
	public static String RemoveEditGeomCommand_commandName;
	public static String RemoveEditGeomCommand_runTaskMessage;
	public static String RemoveSelectedVerticesCommand_name;
	public static String SnapToVertexCommand_name;
	public static String DeleteTool_confirmation_text;
	public static String DeleteTool_confirmation_text2;
	public static String DeleteTool_confirmation_title;
	public static String DeleteTool_warning;
	public static String DeleteTool_status;
	public static String DeleteToolPreferences_description;
	public static String DeleteToolPreferences_Delete_Radius;
	public static String DeleteToolPreferences_Delete_Radius_tooltip;
    public static String ValidHoleValidator_holeOverlap;
    public static String ValidHoleValidator_outsideShell;
    public static String ValidHoleValidator_selfIntersection;
	public static String ValidToolDetectionActivator_questionTitle;
	public static String ValidToolDetectionActivator_warning2;
	public static String ValidToolDetectionActivator_question;
	public static String ValidToolDetectionActivator_warning1;
	public static String DrawPointCommand_name;
	public static String DrawEditGeomsCommand_name;
	public static String CreateAndSelectHoleCommand_name;
	public static String CreateOrSetFeature_name;
	public static String CreateEditGeomCommand_name;
	public static String CreateAndSetNewFeature_name;
	public static String WriteChangesBehaviour_name;
	public static String WriteChangesBehaviour_commandName;
	public static String EventBehaviourCommand_name;
	public static String MoveVertexCommand_name;
	public static String AddToNearestEdgeCommand_name;
	public static String GeometryCreationUtil_errorMsg;
    public static String GeometryCreationUtil_errorTitle;
    static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
