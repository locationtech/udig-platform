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
package net.refractions.udig.tool.info.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    
    private static final String BUNDLE_NAME = "net.refractions.udig.tool.info.internal.messages"; //$NON-NLS-1$

    public static String DistanceTool_distance;

    public static String DistanceTool_error;

    public static String InfoView_instructions_text;

    public static String InfoView2_information_request;

    public static String LayerPointInfo_toString;

    public static String docView_attach;

    public static String docView_attachFile;

    public static String docView_attachFiles;

    public static String docView_delete;

    public static String docView_errEmpty;

    public static String docView_errFileExistMulti;

    public static String docView_errFileExistSingle;

    public static String docView_errInvalidURL;

    public static String docView_errURLExist;

    public static String docView_featureDocs;

    public static String docView_link;

    public static String docView_linkDialogHeader;

    public static String docView_linkDialogTitle;

    public static String docView_linkURL;

    public static String docView_name;

    public static String docView_open;

    public static String docView_openDialogTitle;

    public static String docView_shapeDocs;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
