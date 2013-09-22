/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.image.georeferencing.internal.i18n;

import org.eclipse.osgi.util.NLS;
/**
 * 
 * 
 * @author Mauricio Pazos
 * 
 *  @since 1.3.3
 *
 */
public class Messages extends NLS {
	private static final String	BUNDLE_NAME	= "org.locationtech.udig.image.georeferencing.internal.i18n.messages"; //$NON-NLS-1$
	public static String 		GeoreferencingView_openMarksFile; 
	public static String		CoordinateTableComposite_cant_add_gcp;
	public static String		CoordinateTableComposite_itemAddTooltip;
	public static String		CoordinateTableComposite_itemDeleteAll_Tooltip;
	public static String		CoordinateTableComposite_itemDeleteTooltip;
	public static String		CoordinateTableComposite_itemMoveTooltip;
	public static String	CoordinateTableComposite_map;
	public static String		GeoReferencingCommand_addGrounControlPoint;
	public static String		GeoReferencingCommand_cmdNotReady;
	public static String		GeoReferencingCommand_defaultMessage;
	public static String		GeoReferencingCommand_executeOperation;
	public static String		GeoReferencingCommand_initialMessage;
	public static String		GeoReferencingCommand_insertMarks;
	public static String		GeoReferencingCommand_mapChange;
	public static String		GeoReferencingCommand_needOutputFile;
	public static String		GeoreferencingView_errorLoadingMarks;
	public static String		GeoreferencingView_loadActionText;
	public static String		GeoreferencingView_loadActionTooltip;
	public static String		GeoreferencingView_loadButtonText;
	public static String		GeoreferencingView_runActionText;
	public static String		GeoreferencingView_runActionTooltip;
	public static String		GeoreferencingView_runButtonText;
	public static String		GeoreferencingView_saveActionText;
	public static String		GeoreferencingView_saveActionTooltip;
	public static String		GeoreferencingView_saveButtonText;
	public static String		GeoreferencingView_saveMarksFile;
	
	public static String		ImageComposite_itemLoadTooltip;
	public static String		ImageComposite_itemAddTooltip;
	public static String		ImageComposite_itemDeleteTooltip;
	public static String		ImageComposite_itemDragDropTooltip;
	public static String		ImageComposite_itemZoomInTooltip;
	public static String		ImageComposite_itemZoomOutTooltip;
	public static String		ImageComposite_itemPanTooltip;
	public static String		ImageComposite_itemDelAllTooltip;
	public static String		ImageComposite_itemFitTooltip;
	public static String		MainComposite_browseText;
	public static String		MainComposite_mapGraphicFailText;
	public static String		MainComposite_outputText;
	public static String		MainComposite_outputToolTip;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
