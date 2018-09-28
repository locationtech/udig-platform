/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.internal.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * I18n messages
 * <p>
 * 
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$

    public static String FeatureMergeWizard_feature_merge;

    public static String MergeFeaturesComposite_failed_creating_merge_feature;

    public static String MergeFeaturesComposite_feature;

    public static String MergeFeaturesComposite_merge_feature;

    public static String MergeFeaturesComposite_merge_result_title;

    public static String MergeFeaturesComposite_property;

    public static String MergeFeaturesComposite_result;

    public static String MergeFeaturesComposite_result_geometry;

    public static String MergeFeaturesComposite_result_will_be;

    public static String MergeFeaturesComposite_source;

    public static String MergeFeaturesComposite_value;

    public static String MergeTool_failed_executing;

    public static String MergeTool_failed_getting_selection;

    public static String MergeTool_failed_rollback;

    public static String MergeTool_select_features_to_merge;

    public static String MergeTool_successful;

    public static String MergeTool_title_tool;

    public static String SplitFeatureBuilder_cannotSplit;

    public static String SplitFeaturesCommand_did_not_apply_to_any_feature;

    public static String SplitFeaturesCommand_no_geometry_were_created;

    public static String SplitFeaturesCommand_splitter_line_contain_crs_user_data;

    public static String SplitFeaturesCommand_the_feature_has_invalid_geometry;

    public static String SplitFeaturesCommand_split_transaction_failed;

    public static String SplitFeaturesCommand_fail;

    public static String SplitFeaturesCommand_cannot_retrieve_features_to_be_split;

    public static String SplitFeaturesCommand_cannot_transform_the_splitline_crs;

    public static String SplitGeometryBehaviour_transaction_failed;

    public static String SplitTool_draw_line_to_split;

    public static String TrimFeaturesCommand_did_not_apply_to_any_feature;

    public static String TrimFeaturesCommand_no_features_modified;

    public static String TrimGeometryBehaviour_operation_failed;

    public static String MergeFeatureBehaviour_select_two_or_more;

    public static String MergeFeatureBehaviour_must_intersect;

    public static String MergeFeatureBehaviour_dialog_error_title;

    public static String TrimTool_draw_line_to_trim;

    public static String SelectGeometryCommand_name;

    public static String SetInitialPointCommand;

    public static String ParallelCommand;

    public static String PrecisionCopy_reference_line;

    public static String PrecisionCopy_initial_coorX;

    public static String PrecisionCopy_initial_coorY;

    public static String PrecisionCopy_distanceX;

    public static String PrecisionCopy_distanceY;

    public static String PrecisionCopy_length;

    public static String PrecisionCopy_buttonConversionText;

    public static String PrecisionCopy_buttonConversionToolTip;

    public static String PrecisionSegmentCopy_buttonRestartText;

    public static String PrecisionSegmentCopy_buttonRestartTooltip;

    public static String PrecisionSegmentCopy_bbox_feature_count_advise;

    public static String PrecisionSegmentCopy_Segment_not_contiguous;

    public static String PrecisionSegmentCopy_reference_line;

    public static String PrecisionSegmentCopy_initial_coorX;

    public static String PrecisionSegmentCopy_initial_coorY;

    public static String PrecisionSegmentCopy_distanceX;

    public static String PrecisionSegmentCopy_distanceY;

    public static String PrecisionSegmentCopy_length;

    public static String PrecisionSegmentCopy_buttonConversionText;

    public static String PrecisionSegmentCopy_buttonConversionToolTip;

    public static String PrecisionTool_reference_line;

    public static String PrecisionParallel_initial_coorX;

    public static String PrecisionParallel_initial_coorY;

    public static String PrecisionParallel_distance;

    public static String PrecisionParallel_distanceY;

    public static String PrecisionParallel_length;

    public static String PrecisionParallel_buttonSwitchText;

    public static String PrecisionParallel_buttonSwitchToolTip;

    public static String PrecisionParallel_buttonRestartTooltip;

    public static String PrecisionParallel_buttonRestartText;

    public static String PrecisionTool_apply_text;

    public static String PrecisionTool_apply_tooltip_text;

    public static String PrecisionTool_ok_text;

    public static String PrecisionTool_ok_tooltip_text;

    public static String PrecisionTool_cancel_text;

    public static String PrecisionTool_cancel_tooltip_text;

    public static String PrecisionParallelReferenceFeature;

    public static String PrecisionParallel_InitialMessage;

    public static String PrecisionParallel_Set_Distance;

    public static String PrecisionParallel_Set_Another_Distance;

    public static String PrecisionParallel_error_distance_NaN;

    public static String PrecisionParallel_error_distance_zero;

    public static String MergeView_cancel_tool_tip;

    public static String MergeView_finish_tool_tip;

    public static String Transformation_layer_not_compatible;

    public static String Transformation_2nd_stage;

    public static String MergeFeatureBehaviour_select_one_or_more;

    public static String MergeFeatureView_no_feature_to_delete;

    public static String MergeFeatureView_cant_remove;

    public static String MergeView_remove_tool_tip;

    public static String MergeView_remove_text;

    public static String GeometryUtil_DonotKnowHowAdapt;

    public static String TrimFeaturesCommand_unvalid_intersection;

    public static String PrecisionParallel_deformed_result;
    
    
    
    public static String DialogUtil_title;

    public static String DialogUtil_message;

    public static String DialogUtil_runInBackground;

    public static String GeometryUtil_CannotGetDimension;

    //public static String GeometryUtil_DonotKnowHowAdapt;

    public static String GeometryUtil_ExpectedSimpleGeometry;

    public static String GeoToolsUtils_FailCreatingFeature;

    public static String GeoToolsUtils_FeatureTypeName;

    public static String GeoToolsUtils_Geometry;

    public static String GeoToolsUtils_Name;

    public static String GeoToolsUtils_unitName_centimeters;

    public static String GeoToolsUtils_unitName_degrees;

    public static String GeoToolsUtils_unitName_feet;

    public static String GeoToolsUtils_unitName_inches;

    public static String GeoToolsUtils_unitName_kilometers;

    public static String GeoToolsUtils_unitName_meters;

    public static String GeoToolsUtils_unitName_pixels;

    public static String GeoToolsUtils_unitName_yards;

    public static String LayerUtil_CanNotResolveFeatureSource;

    public static String SplitStrategy_illegal_geometry;

    public static String TrimGeometryStrategy_defined_for_line_geometries;

    public static String TrimGeometryStrategy_difference_unknown_type;

    public static String TrimGeometryStrategy_point_not_on_line;

    public static String TrimGeometryStrategy_trimming_line_intersect_one_point;
    
    

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
