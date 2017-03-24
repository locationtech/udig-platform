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
package org.locationtech.udig.mapgraphic.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.mapgraphic.internal.messages"; //$NON-NLS-1$
	public static String BarStyle_LabelFilled;
    public static String BarStyle_LabelFilledLine;
    public static String BarStyle_LabelLine;
    public static String BarStyle_LabelSimple;
    public static String BarStyleConfigurator_barstylelabel;
    public static String BarStyleConfigurator_colorlable;
    public static String BarStyleConfigurator_divisionslabel;
    public static String BarStyleConfigurator_ImperialUnits;
    public static String BarStyleConfigurator_MetricUnits;
    public static String BarStyleConfigurator_AutoUnits;
    public static String BarStyleConfigurator_UnitsLabel;
    public static String GridStyleConfigurator_0;
    public static String GridStyleConfigurator_1;
    public static String GridStyleConfigurator_2;
    public static String GridStyleConfigurator_ChangeColor;
    public static String GridStyleConfigurator_HSpacing;
    public static String GridStyleConfigurator_LineColor;
    public static String GridStyleConfigurator_LineStyle;
    public static String GridStyleConfigurator_LineWidth;
    public static String GridStyleConfigurator_VSpacing;
    public static String MapGraphicResource_description;
	public static String MapGraphicService_description;
	public static String MapGraphicService_title;
	public static String MapGraphic_title;
    public static String OtherAction_addAll;
    public static String OtherAction_addButton;
    public static String OtherAction_message1;
    public static String OtherAction_p1;
    public static String OtherAction_p2;
    public static String OtherAction_shellText;
    public static String OtherAction_wizardTitle;
    public static String LocationStyleConfigurator_height;
    public static String LocationStyleConfigurator_width;
    public static String ScalebarMapGraphic_zoomInRequiredMessage;
    public static String ScalebarStyleConfigurator_verticalAlignment;
    public static String ScalebarStyleConfigurator_horizontalAlignment;
    public static String ScalebarStyleConfigurator_right;
    public static String ScalebarStyleConfigurator_center;
    public static String ScalebarStyleConfigurator_left;
    public static String ScalebarStyleConfigurator_bottom;
    public static String ScalebarStyleConfigurator_middle;
    public static String ScalebarStyleConfigurator_top;
    public static String ScaleDenomStyleConfigurator_DrawBackground;
    public static String ScaleDenomStyleConfigurator_labelPrefix;
    public static String Font_ExampleText;
    public static String GridMapGraphic_grids_too_close;
    public static String UnitListener_MixedUnits;
    public static String GraticuleCRSConfigurator_CRS;
    public static String GraticuleCRSConfigurator_Select;
    public static String GraticuleGraphic_Error;
    public static String GraticuleGraphic_Error_Too_Many_Squares;
    public static String GraticuleGraphic_Illegal_CRS;
    public static String GraticuleGraphic_Maximum_Scale;
    public static String GraticuleStyleConfigurator_Font_Color;
    public static String GraticuleStyleConfigurator_Show_Labels;
    public static String GraticuleStyleConfigurator_Opacity;
	public static String PositionStyleConfigurator_CustomPositionLabel;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
