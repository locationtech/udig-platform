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
package org.locationtech.udig.render.gridcoverage.basic.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.render.gridcoverage.basic.internal.messages"; //$NON-NLS-1$
	public static String BasicGridCoverageRenderer_0;
    public static String BasicGridCoverageRenderer_error_message;
	public static String BasicGridCoverageRenderer_errorPainting;
	public static String BasicGridCoverageRenderer_rendering_status;
	public static String BasicGridCoverageRenderer_statusMessage;
    public static String ChannelViewer_BandLabel;
	public static String MosaicInfoPanel_BandLabel;
    public static String MosaicInfoPanel_BlueBandLabel;
    public static String MosaicInfoPanel_FileDateLabel;
    public static String MosaicInfoPanel_FileInformationHeader;
    public static String MosaicInfoPanel_FileLabel;
    public static String MosaicInfoPanel_FileSizeLabel;
    public static String MosaicInfoPanel_FileTypeLabel;
    public static String MosaicInfoPanel_GreenBandLabel;
    public static String MosaicInfoPanel_InvalidBandSelectionMessage;
    public static String MosaicInfoPanel_NoDataText;
    public static String MosaicInfoPanel_RedBandLabel;
    public static String MosaicInfoView_DefaultViewText;
    public static String MosaicInfoView_UndoAction;
    public static String MosaicInfoView_UndoActionToolTip;
    static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
