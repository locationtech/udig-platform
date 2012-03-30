package eu.udig.tools.feature.utils.i18n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String	BUNDLE_NAME	= Messages.class.getPackage().getName() + ".messages";	//$NON-NLS-1$

	public static String		DialogUtil_title;

	public static String		DialogUtil_message;

	public static String		DialogUtil_runInBackground;

	public static String		GeometryUtil_CannotGetDimension;

	public static String		GeometryUtil_DonotKnowHowAdapt;

	public static String		GeometryUtil_ExpectedSimpleGeometry;

	public static String		GeoToolsUtils_FailCreatingFeature;

	public static String		GeoToolsUtils_FeatureTypeName;

	public static String		GeoToolsUtils_Geometry;

	public static String		GeoToolsUtils_Name;

	public static String		GeoToolsUtils_unitName_centimeters;
	public static String		GeoToolsUtils_unitName_degrees;
	public static String		GeoToolsUtils_unitName_feet;
	public static String		GeoToolsUtils_unitName_inches;
	public static String		GeoToolsUtils_unitName_kilometers;
	public static String		GeoToolsUtils_unitName_meters;
	public static String		GeoToolsUtils_unitName_pixels;
	public static String		GeoToolsUtils_unitName_yards;

	public static String		LayerUtil_CanNotResolveFeatureSource;

	public static String		SplitStrategy_illegal_geometry;

	public static String		TrimGeometryStrategy_defined_for_line_geometries;
	public static String		TrimGeometryStrategy_difference_unknown_type;
	public static String		TrimGeometryStrategy_point_not_on_line;
	public static String		TrimGeometryStrategy_trimming_line_intersect_one_point;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
