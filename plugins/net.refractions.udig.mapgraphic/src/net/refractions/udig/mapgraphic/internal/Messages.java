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
package net.refractions.udig.mapgraphic.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.refractions.udig.mapgraphic.internal.messages"; //$NON-NLS-1$
	public static String FontStyleConfigurator_example;
    public static String FontStyleConfigurator_Select;
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
    public static String LocationStyleConfigurator_y;
    public static String LocationStyleConfigurator_x;
    public static String ScalebarMapGraphic_zoomInRequiredMessage;
    public static String ScalebarStyleConfigurator_verticalAlignment;
    public static String ScalebarStyleConfigurator_horizontalAlignment;
    public static String ScalebarStyleConfigurator_right;
    public static String ScalebarStyleConfigurator_center;
    public static String ScalebarStyleConfigurator_left;
    public static String ScalebarStyleConfigurator_bottom;
    public static String ScalebarStyleConfigurator_middle;
    public static String ScalebarStyleConfigurator_top;
    public static String GridMapGraphic_grids_too_close;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
