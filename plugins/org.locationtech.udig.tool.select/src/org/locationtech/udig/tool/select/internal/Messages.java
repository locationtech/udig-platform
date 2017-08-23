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
package org.locationtech.udig.tool.select.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.locationtech.udig.tool.select.internal.messages"; //$NON-NLS-1$
    public static String ArrowSelection_0;
    public static String TableView_0;
    public static String TableView_1;
    public static String TableView_allCheckText;
    public static String TableView_allToolTip;
    public static String TableView_compositeName;
    public static String TableView_deleteCommandName;
    public static String TableView_featureSelected;
    public static String TableView_noFeatureWarning;
    public static String TableView_promote_text;
    public static String TableView_promote_tooltip;
    public static String TableView_search;
    public static String TableView_search_any;
    public static String TableView_zoomToolText;
    public static String TableView_zoomToolToolTip;
    public static String Select_Title;
    public static String Select_Description;
    public static String Zoom_To_Selection;
    public static String Group_AOI;
    public static String Navigate_Selection;
    public static String Feature_Selection_Radius;
    public static String Feature_Selection_Radius_tooltip;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
